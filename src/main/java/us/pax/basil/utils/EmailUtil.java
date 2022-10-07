package us.pax.basil.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.mail.javamail.MimeMessageHelper;

import us.pax.basil.entity.User;
import us.pax.basil.entity.password.VerificationToken;

public class EmailUtil {
    public static MimeMessage constructResendVerificationTokenEmail(final MimeMessage mimeMsg, 
    																	  final HttpServletRequest request, 
    		                                                              final Locale locale, 
    		                                                              final VerificationToken newToken, 
    		                                                              final String to,
    		                                                              final String from) {
        final String confirmationUrl = getAppUrl(request) + "/registrationConfirm.html?token=" + newToken.getToken();
        return constructEmail(mimeMsg, "Resend Registration Token", "message" + " \r\n" + confirmationUrl, to, from);
    }

    public static MimeMessage constructTokenEmail(final MimeMessage mimeMsg, 
    												final HttpServletRequest request, 
   													final String token, 
   													final String htmlFile, 
   													final String subject, 
   													final String frontEndUrl, 
   													final String linkTitle, 
   													final User user, 
   													final String from) {
        String htmlBody = readHtml(htmlFile);
        
        htmlBody = htmlBody.replace("xxx", user.getName());
        htmlBody = htmlBody.replace("yyy", getAppUrl(request) + frontEndUrl + "?token=" + token);
        
        htmlBody = htmlBody.replace("zzz",linkTitle);

        return constructEmail(mimeMsg, subject, htmlBody, user.getEmail(), from);
    }

    public static MimeMessage constructEmail(MimeMessage mimeMsg, String subject, String body, String to, String from) {

    	MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, "utf-8");

    	String htmlMsg = body;
    	
    	try {
			helper.setText(htmlMsg, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom(from);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    	return (mimeMsg);

    }

    private static String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @SuppressWarnings("unused")
	private static String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    
    private static String readHtml(String filename){
        try {
        	InputStream is = EmailUtil.class.getClassLoader().getResourceAsStream(filename);

            byte[] encoded = new byte[1024];
            is.read(encoded);

            return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return("Unable to open " + filename);
		}
    }
}