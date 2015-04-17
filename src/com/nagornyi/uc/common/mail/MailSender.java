package com.nagornyi.uc.common.mail;

import com.nagornyi.uc.entity.User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 31.05.14
 */
public class MailSender {
    Logger log = Logger.getLogger(MailSender.class.getName());

    private User user;
    private String message;
    private String subject = "[ukraina-centr.com] Підтвердження бронювань";

    public MailSender(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void send() {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress("info@ukraina-centr.com", "Україна-Центр", "utf-8"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(user.getEmail(), user.getName(), "utf-8"));
            msg.addRecipient(Message.RecipientType.CC,
                    new InternetAddress("info@ukraina-centr.com", "Admin", "utf-8"));
            msg.setSubject(subject, "utf-8");

            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(message, "text/html");
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);

            Transport.send(msg);
        } catch (MessagingException e) {
            log.log(Level.WARNING, "Can't send mail", e);
        } catch (UnsupportedEncodingException e) {
            log.log(Level.WARNING, "Can't send mail", e);
        }
    }
}
