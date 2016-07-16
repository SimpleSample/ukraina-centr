package com.nagornyi.uc.common.mail;

import com.nagornyi.uc.common.template.HTMLTemplates;
import com.nagornyi.uc.currency.CurrencyConverter;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.util.CurrencyUtil;

import java.util.List;

/**
 * @author Nagornyi
 * Date: 18.06.14
 */
public class MailFacade {

    public static void sendSuccessfulReservation(User user, List<Ticket> tickets) {
        double price = 0.0;
        for (Ticket ticket: tickets) {
            price += ticket.getCalculatedPrice();
        }
        double convertedPrice = CurrencyConverter.fromBase(price);
        String template = HTMLTemplates.getUserReservationTemplate(price, CurrencyUtil.round(convertedPrice, -1), tickets, user);
        new MailSender(user, template).send();
    }

    public static void sendFailedTicketsPurchaseFromLiqPay(User user, String transactionId) {
        String template = HTMLTemplates.getFailedTicketsPurchaseFromLiqPayTemplate(user, transactionId);
        new MailSender(user, template).send();
    }

    public static void sendRenewPass(User user, String newPass) {
        String template = HTMLTemplates.getRenewPassTemplate(user, newPass);
        new MailSender(user, template)
                .includeAdmin(false)
                .setSubject(MailSender.UC_SUBJECT +"Відновлення паролю")
                .send();
    }
}
