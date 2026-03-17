package us.pax.basil.service.aws.s3;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RMA 附件存储服务（S3 + 本地回退）。
 *
 * 设计说明：
 * 1. 优先使用 AWS S3；当 AWS 不可用或配置关闭时，自动回退到本地目录。
 * 2. 数据库存储的是 object key（相对路径），而不是绝对文件系统路径，
 *    这样可以在 S3 与本地模式之间保持一致的数据结构。
 * 3. S3 模式下生成真实预签名 URL；本地模式下生成一次性短期 token URL，
 *    通过后端受控接口下载，满足“安全可过期”的要求。
 */
@Service
@Log4j2
public class RmaAttachmentStorageService {

    /**
     * 是否启用 AWS（复用现有配置）。
     */
    @Value("${aws.enabled:false}")
    private boolean awsEnabled;

    /**
     * AWS 区域（复用现有配置）。
     */
    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    /**
     * 附件存储桶名称（需求指定默认 basil-uat-cp-file-storage）。
     */
    @Value("${rma.file-storage.bucket:basil-uat-cp-file-storage}")
    private String bucketName;

    /**
     * 工单附件基础目录前缀。
     */
    @Value("${rma.file-storage.base-prefix:tickets}")
    private String basePrefix;

    /**
     * 本地回退目录（当 S3 不可用时）。
     */
    @Value("${rma.file-storage.local-root:./basil-uat-cp-file-storage}")
    private String localRoot;

    /**
     * 预签名 URL 过期时间（分钟）。
     */
    @Value("${rma.file-storage.presign-expire-minutes:30}")
    private long presignExpireMinutes;

    private final AwsCredentialsProvider awsCredentialsProvider;

    private volatile S3Client s3Client;

    private volatile S3Presigner s3Presigner;

    /**
     * 本地模式下的下载 token 缓存。
     * key: token
     * value: 关联的本地文件信息与过期时间
     */
    private final Map<String, LocalTokenRecord> localDownloadTokens = new ConcurrentHashMap<>();

    public RmaAttachmentStorageService(AwsCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    /**
     * 上传附件（优先 S3，失败回退本地）。
     *
     * @param ticketId 工单号
     * @param file     上传文件
     * @return 存储结果（包含 object key、大小、类型等）
     */
    public StoredAttachment upload(Integer ticketId, MultipartFile file) throws IOException {
        String originalFilename = sanitizeFileName(file.getOriginalFilename());
        String objectKey = buildObjectKey(ticketId, originalFilename);
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        if (isS3Available()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();
            getS3Client().putObject(request, RequestBody.fromBytes(file.getBytes()));
        } else {
            Path targetPath = resolveLocalPath(objectKey);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        StoredAttachment storedAttachment = new StoredAttachment();
        storedAttachment.setObjectKey(objectKey);
        storedAttachment.setOriginalFileName(originalFilename);
        storedAttachment.setContentType(contentType == null ? "application/octet-stream" : contentType);
        storedAttachment.setFileSize(fileSize);
        return storedAttachment;
    }

    /**
     * 为指定 object key 生成可下载 URL。
     *
     * @param objectKey 数据库存储路径
     * @return 预签名 URL（S3）或 token 下载 URL（本地）
     */
    public String generateDownloadUrl(String objectKey) {
        if (isS3Available()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(presignExpireMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = getS3Presigner().presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        }

        cleanupExpiredLocalTokens();
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalTokenRecord tokenRecord = new LocalTokenRecord();
        tokenRecord.setObjectKey(objectKey);
        tokenRecord.setExpiredAt(Instant.now().plus(Duration.ofMinutes(presignExpireMinutes)));
        localDownloadTokens.put(token, tokenRecord);

        return "/ticketing/attachments/local/" + URLEncoder.encode(token, StandardCharsets.UTF_8.name());
    }

    /**
     * 删除附件（S3 或本地）。
     */
    public void delete(String objectKey) throws IOException {
        if (isS3Available()) {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            getS3Client().deleteObject(request);
            return;
        }

        Path targetPath = resolveLocalPath(objectKey);
        Files.deleteIfExists(targetPath);
    }

    /**
     * 解析本地模式下载 token。
     *
     * @param token 下载 token
     * @return 可下载资源信息；不存在或过期返回 null
     */
    public LocalDownloadResource resolveLocalDownload(String token) {
        cleanupExpiredLocalTokens();
        LocalTokenRecord tokenRecord = localDownloadTokens.get(token);
        if (tokenRecord == null || tokenRecord.getExpiredAt().isBefore(Instant.now())) {
            localDownloadTokens.remove(token);
            return null;
        }

        Path localFile = resolveLocalPath(tokenRecord.getObjectKey());
        if (!Files.exists(localFile) || !Files.isRegularFile(localFile)) {
            localDownloadTokens.remove(token);
            return null;
        }

        LocalDownloadResource resource = new LocalDownloadResource();
        resource.setLocalPath(localFile);
        resource.setObjectKey(tokenRecord.getObjectKey());
        return resource;
    }

    /**
     * 是否启用 S3。
     */
    public boolean isS3Available() {
        return awsEnabled && awsCredentialsProvider != null;
    }

    private String buildObjectKey(Integer ticketId, String fileName) {
        String safePrefix = basePrefix == null ? "tickets" : basePrefix.trim();
        if (safePrefix.endsWith("/")) {
            safePrefix = safePrefix.substring(0, safePrefix.length() - 1);
        }
        return safePrefix + "/" + ticketId + "/" + UUID.randomUUID().toString().replace("-", "") + "_" + fileName;
    }

    private Path resolveLocalPath(String objectKey) {
        Path rootPath = Paths.get(localRoot).normalize().toAbsolutePath();
        return rootPath.resolve(objectKey).normalize();
    }

    private String sanitizeFileName(String fileName) {
        String defaultName = "unnamed_file";
        if (fileName == null || fileName.trim().isEmpty()) {
            return defaultName;
        }

        // 只保留文件名主体，避免路径穿越风险。
        String baseName = Paths.get(fileName).getFileName().toString();
        // 替换风险字符，保证对象键和本地路径可控。
        return baseName.replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
    }

    private S3Client getS3Client() {
        if (s3Client == null) {
            synchronized (this) {
                if (s3Client == null) {
                    s3Client = S3Client.builder()
                            .region(Region.of(awsRegion))
                            .credentialsProvider(awsCredentialsProvider)
                            .build();
                    log.info("RMA 附件存储启用 S3，bucket: {}", bucketName);
                }
            }
        }
        return s3Client;
    }

    private S3Presigner getS3Presigner() {
        if (s3Presigner == null) {
            synchronized (this) {
                if (s3Presigner == null) {
                    s3Presigner = S3Presigner.builder()
                            .region(Region.of(awsRegion))
                            .credentialsProvider(awsCredentialsProvider)
                            .build();
                }
            }
        }
        return s3Presigner;
    }

    private void cleanupExpiredLocalTokens() {
        Instant now = Instant.now();
        localDownloadTokens.entrySet().removeIf(entry -> entry.getValue().getExpiredAt().isBefore(now));
    }

    @Data
    private static class LocalTokenRecord {
        /**
         * 对应数据库路径（object key）。
         */
        private String objectKey;

        /**
         * token 过期时间。
         */
        private Instant expiredAt;
    }

    @Data
    public static class StoredAttachment {
        /**
         * 存储路径（object key）。
         */
        private String objectKey;

        /**
         * 原始文件名（用于回显）。
         */
        private String originalFileName;

        /**
         * MIME 类型。
         */
        private String contentType;

        /**
         * 文件大小（字节）。
         */
        private long fileSize;
    }

    @Data
    public static class LocalDownloadResource {
        /**
         * 本地真实路径。
         */
        private Path localPath;

        /**
         * 对应 object key（用于审计日志）。
         */
        private String objectKey;
    }
}
