package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Nagorny
 * Date: 08.05.14
 */
public class SendMail implements Action {
    Logger log = Logger.getLogger(SendMail.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Ви вдало забронювали квиток (TEST)";

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setHeader("Content-Type", "text/plain; charset=UTF-8");
            msg.setFrom(new InternetAddress("info@ukraina-centr.com", "Admin"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress("forartforces@gmail.com", "Артем"));
            msg.setSubject("[ukraina-centr.com] Підтвердження", "utf-8");
            msg.setText(msgBody);
            Transport.send(msg);
        } catch (MessagingException e) {
            log.log(Level.WARNING, "Can't send mail", e);
        } catch (UnsupportedEncodingException e) {
            log.log(Level.WARNING, "Can't send mail", e);
        }

    }
}
