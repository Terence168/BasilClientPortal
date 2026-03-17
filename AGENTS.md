# AGENTS.md

## 约定
- 每次生成的后端代码都要加上详细的中文注释，log日志必须用英文输出。
- 前端生成的页面都是英文。

## 适用范围
本文件适用于 `BasilClientPortal_back` 仓库。

## 项目概览
- 技术栈：Java 8、Spring Boot 2.2.2、Spring Security、MyBatis/MyBatis-Plus、Maven。
- 启动入口：`src/main/java/us/pax/basil/ClientPortalApplication.java`。
- 主要环境切换：`src/main/resources/application.yml`（`spring.profiles.active`）。
- 常见返回封装：`QueryResultDTO`、`QueryResultArrayDTO`、`ApiResultDTO`。

## 当前架构
- `controller/`：REST 接口层，处理请求解析与权限注解。
- `service/` + `service/impl/`：业务逻辑与流程编排。
- `mapper/`（Java 接口）+ `resources/mapper/`（XML SQL）：数据访问层。
- `entity/` 与 `dto/`：领域实体与接口传输模型。
- `security/` + `config/`：认证鉴权、CORS、过滤器与框架配置。

## 不可违反的规则
- 保持包结构与分层一致，Controller 不得直接绕过 Service 访问数据层。
- 除非明确要求，保持接口契约兼容（路径、参数、返回结构）。
- 新增受保护接口必须添加 `@PreAuthorize(...)`，并使用合理的权限命名。
- 修改 Mapper 时必须同步更新 Java 接口与 XML；方法签名与 XML `id` 必须一致。
- 严禁在代码/YAML/日志中提交密钥、Token、密码等敏感信息。
- 非必要不要修改生成物或运行产物：`target/`、`service-logs/`、`backend-dev.log`。

## 编码约定
- 新增代码优先使用构造器注入；已存在文件若无重构需求，保持原有风格。
- 方法保持单一职责，SQL 相关逻辑放在 Mapper XML，不放在 Controller。
- 复用现有 DTO/结果封装模式，避免临时拼接不统一的响应格式。
- 异常路径补充简洁日志，但不得输出敏感字段。
- 对请求参数和数据库结果做明确的 `null/empty` 处理。

## MyBatis / SQL 约定
- Mapper 接口路径：`src/main/java/us/pax/basil/mapper/*Mapper.java`。
- XML 路径：`src/main/resources/mapper/*Mapper.xml`。
- 多参数 Mapper 方法使用 `@Param`。
- 禁止使用 `SELECT *`，必须显式列出所需字段。
- 保持当前命名映射风格（SQL 中使用 camelCase 别名对应 Java 字段）。

## 数据库字典文件约定
- 数据字典固定文件：
  - `docs/database-dictionary.md`
  - `docs/database-dictionary.xlsx`
- 以上两个文件必须保持同步更新：任一文件有结构或内容变更，另一个文件必须在同一任务中同步更新。
- 字典数据来源以数据库导出结果为准（例如全库表结构导出 txt）；禁止仅根据代码推测补全字段类型。
- `database-dictionary.md` 作为主审阅文档，要求可读、可 diff。
- `database-dictionary.xlsx` 作为交付和筛选版本，至少包含：
  - `columns` 工作表：字段级明细（database/table/column/type/nullable/key/description）
  - `tables` 工作表：表级汇总（database/table/field_count）
- 更新数据库结构（新增表、删字段、改类型、改索引）时，PR 必须包含这两个字典文件的更新。

## 安全约定
- 安全配置集中在 `security/WebSecurityConfiguration.java`。
- 公共接口由各环境 YAML 的 `security.permit-all.urls` 管控。
- 新增公开接口必须显式评估风险后再放开。
- CORS 为环境配置项，非必要不要扩大来源/方法范围。

## 构建与运行
- 编译：
  - `mvn clean compile`
- 打包（项目当前默认方式）：
  - `mvn clean package "-Dspring.profiles.active=prod" -DskipTests`
- 本地运行（dev 环境）：
  - `mvn spring-boot:run "-Dspring-boot.run.profiles=dev"`
- Jar 启动脚本（Linux）：
  - `src/main/backend/startup.sh <jar-path> <profile>`

## 交付前检查清单
- 至少完成受影响模块编译通过（最低 `mvn clean compile`）。
- 新增或修改接口具备正确权限注解，并保持请求/响应契约正确。
- Mapper Java 与 XML 改动一一对应，SQL 逻辑已完成基本验证。
- 未引入新的凭据或敏感信息到代码与配置中。
- 若接口行为、运行方式或构建方式发生变化，同步更新 `README.md`。
