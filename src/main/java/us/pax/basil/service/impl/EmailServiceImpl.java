package us.pax.basil.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import us.pax.basil.service.EmailService;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private String port;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private Boolean isTlsEnable;
    @Value("${spring.mail.properties.mail.smtp.auth}")
    private Boolean isAuth;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    @Async
    public void sendRmaConfirmationEmail(int mo_oid, String recipientEmail) throws Exception {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", isAuth);
            props.put("mail.smtp.starttls.enable", isTlsEnable);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            log.debug(port, username, password);
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // Create a MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set the sender address
            message.setFrom(new InternetAddress(username));

            // Set the recipient address
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));

            // Set the subject
            message.setSubject("Important Information - Device Return and Support");

            String content = String.format("<p><b><u>***Please include a power supply with all units, especially if the device has power/charging issues.***</u></b></p>\n" +
                    "\n" +
                    "    <p>If the unit is having a software issue, please contact the PAX technical support first at <a href=\"tel:+18778590099\">(877) 859-0099</a> or email us at <a href=\"mailto:support@pax.us\">support@pax.us</a> to see if your issues can be resolved through them. If your issues persist and you would like to send in your device(s), continue following the instructions provided below.</p>\n" +
                    "\n" +
                    "    <p></u>Return the terminal to the address below with the RMA number visible on the package:</u></p>\n" +
                    "    <p><b>PAX Technology, Inc.</b></p>\n" +
                    "    <p><b>RMA # %d (insert ticket ID here)</b></p>\n" +
                    "    <p><b>3680 Clear Channel Place, Jacksonville, FL 32224</b></p>\n" +
                    "\n" +
                    "    <p>Due to component shortages, we are experiencing a delay in repairing the devices. Because of this, it may take longer than usual to return the devices. We apologize in advance for the inconvenience.</p>\n" +
                    "\n" +
                    "    <p>Thank you!</p>", mo_oid);
            message.setContent(content, "text/html");
            Transport.send(message);
        }catch (Exception e){
            throw  new Exception("Fail to delivery email to " + recipientEmail + ".");
        }
    }
}
