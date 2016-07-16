package com.nagornyi.uc.common.template;

import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.templates.TemplatesManager;

import java.util.List;
import java.util.Locale;

/**
 * Created by Yanko on 30.05.14.
 */
public class HTMLTemplates {

    //todo restore <span style="font-weight:100">або</span> {$realPrice}₴ in template when uah prices will be appropriate
    public static String getUserReservationTemplate(double eurSummaryPrice, double summaryPrice, List<Ticket> tickets, User user) {
        Long orderExternalId = tickets.iterator().next().getOrder().getExternalId();
        System.out.println("summary price " + summaryPrice);
        Locale locale = user.getUserLocale();
        SoyListData ticketsSoy = new SoyListData();
        for (Ticket ticket: tickets) {
            SoyMapData t = new SoyMapData();
            t.put("passenger", ticket.getPassenger());
            t.put("route", ticket.getStartCity().getLocalizedName(locale) + " - " + ticket.getEndCity().getLocalizedName(locale));
            t.put("startDate", DateFormatter.format(ticket.getStartDate(), locale));
            t.put("seat", ticket.getSeat().getSeatNum());
            t.put("phone1", ticket.getPhone1());
            t.put("phone2", ticket.getPhone2());
            String note = ticket.getNote() == null? "" : ticket.getNote();
            t.put("note", note);
            ticketsSoy.add(t);
        }

        SoyMapData data = new SoyMapData();
        String username = user.isPartner()? user.getPartnerName() : user.getUsername();
        data.put("username", username);
        data.put("tickets", ticketsSoy);
        data.put("price", eurSummaryPrice);
        data.put("realPrice", summaryPrice);
        data.put("orderId", orderExternalId.toString());
        return TemplatesManager.INSTANCE.renderTemplate("templates.reserve", data);
    }

    public static String getRenewPassTemplate(User user, String newPass) {
        SoyMapData data = new SoyMapData();
        String username = user.isPartner()? user.getPartnerName() : user.getUsername();
        data.put("username", username);
        data.put("newPass", newPass);
        return TemplatesManager.INSTANCE.renderTemplate("templates.renewPass", data);
    }


    public static String getFailedTicketsPurchaseTemplate(User user) {
        SoyMapData data = new SoyMapData();
        String username = user.isPartner()? user.getPartnerName() : user.getUsername();
        data.put("username", username);
        return TemplatesManager.INSTANCE.renderTemplate("templates.reserveFail", data);
    }

    public static String getFailedTicketsPurchaseFromLiqPayTemplate(User user, String transactionId) {
        SoyMapData data = new SoyMapData();
        String username = user.isPartner()? user.getPartnerName() : user.getUsername();
        data.put("username", username);
        data.put("transactionId", transactionId);
        return TemplatesManager.INSTANCE.renderTemplate("templates.liqpayPurchaseFailed", data);
    }
}
