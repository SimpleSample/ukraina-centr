package com.nagornyi.uc.common.mail;

import com.nagornyi.uc.entity.User;

public interface MailSender {

    MailSender setUser(User user);

    MailSender setMessage(String message);

    MailSender setSubject(String subject);

    MailSender includeAdmin(boolean includeAdmin);

    void send();
}
