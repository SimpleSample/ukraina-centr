package com.nagornyi.uc.common.mail;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.entity.User;

import java.util.logging.Logger;

public class SendgridMailSender implements MailSender {

    private static final String UC_SUBJECT = "[ukraina-centr.com] ";
    private static final Logger log = Logger.getLogger(SendgridMailSender.class.getName());

    private User user;
    private String message;
    private boolean includeAdmin = true;
    private String subject = UC_SUBJECT + "Підтвердження придбання квитків";

    @Override
    public MailSender setUser(User user) {
        this.user = user;

        return this;
    }

    @Override
    public MailSender setMessage(String message) {
        this.message = message;

        return this;
    }

    @Override
    public MailSender setSubject(String subject) {
        this.subject = subject;

        return this;
    }

    @Override
    public MailSender includeAdmin(boolean includeAdmin) {
        this.includeAdmin = includeAdmin;

        return this;
    }

    @Override
    public void send() {
        log.info("Sending mail to " + user.getEmail() + ", subject " + subject);

        Sendgrid sendgrid = new Sendgrid();
        sendgrid.setSubject(subject)
                .setHtml(message)
                .setFrom("info@ukraina-centr.com").setFromName("Україна-Центр")
                .addTo(user.getEmail(), user.getName());

        if (includeAdmin) {
            sendgrid.addTo("info@ukraina-centr.com", "Admin");
        }

        try {
            sendgrid.send();
        } catch (JSONException e) {
            log.severe("Could not send mail " +  message);
        }
    }
}
