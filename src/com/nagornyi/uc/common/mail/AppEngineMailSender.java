package com.nagornyi.uc.common.mail;

import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.service.ServiceLocator;

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
public class AppEngineMailSender implements MailSender {
    public static final String UC_SUBJECT = "[ukraina-centr.com] ";
    Logger log = Logger.getLogger(AppEngineMailSender.class.getName());

    private User user;
    private String message;
    private boolean includeAdmin = true;
    private String subject = UC_SUBJECT + "Підтвердження придбання квитків";

    public AppEngineMailSender(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public AppEngineMailSender setUser(User user) {
        this.user = user;
        return this;
    }

    public AppEngineMailSender setMessage(String message) {
        this.message = message;
        return this;
    }

    public AppEngineMailSender setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public AppEngineMailSender includeAdmin(boolean includeAdmin) {
        this.includeAdmin = includeAdmin;
        return this;
    }

    public void send() {
        log.info("Sending mail to " + user.getEmail());
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String adminEmail = ServiceLocator.getInstance().getUserService().getAdminEmail();

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(adminEmail, "Україна-Центр", "utf-8"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(user.getEmail(), user.getName(), "utf-8"));
            if (includeAdmin) {
                msg.addRecipient(Message.RecipientType.CC,
                        new InternetAddress(adminEmail, "Admin", "utf-8"));
            }
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
