package com.planb.util;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static com.planb.constant.EmailConstant.ADMIN_EMAIL;
import static com.planb.constant.EmailConstant.EMAIL_AUTHORIZATION_CODE;

/**
 * 发送邮件工具类
 */
public class MailUtil {
    public static void sendEmail(String recipientEmail, String subject, String content) throws Exception {
        final String from = ADMIN_EMAIL; // QQ邮箱

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.ciphersuites", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, EMAIL_AUTHORIZATION_CODE);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject(subject);
        message.setContent(content, "text/html;charset=utf-8");

        Transport.send(message);
    }

}
