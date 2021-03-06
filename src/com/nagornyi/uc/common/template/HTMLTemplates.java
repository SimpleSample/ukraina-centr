package com.nagornyi.uc.common.template;

import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.templates.TemplatesManager;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * Created by Yanko on 30.05.14.
 */
public class HTMLTemplates {

    public static String getUserReservationTemplate(double eurSummaryPrice, double summaryPrice, List<Ticket> tickets, User user) {
        Order order = tickets.iterator().next().getOrder();
        String orderExternalId = order == null? "[no order ID]" : order.getExternalId().toString();
        System.out.println("summary price " + summaryPrice);

        SoyMapData data = getDataForTickets(tickets, user);
        data.put("price", eurSummaryPrice);
        data.put("actualPrice", summaryPrice);
        data.put("orderId", orderExternalId);
        return TemplatesManager.INSTANCE.renderTemplate("templates.reserve", data);
    }

    public static String getTimedOutTicketsTemplate(User user, List<Ticket> tickets) {
        SoyMapData data = getDataForTickets(tickets, user);
        return TemplatesManager.INSTANCE.renderTemplate("templates.reservationTimedOut", data);
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

    private static SoyMapData getDataForTickets(List<Ticket> tickets, User user) {
        Locale locale = user.getUserLocale();
        SoyListData ticketsSoy = new SoyListData();
        for (Ticket ticket: tickets) {
            SoyMapData t = new SoyMapData();
            t.put("passenger", ticket.getPassenger());
            t.put("route", ticket.getStartCity().getLocalizedName(locale) + " - " + ticket.getEndCity().getLocalizedName(locale));
            t.put("startDate", DateFormatter.format(ticket.getStartDate(), locale));
            t.put("seat", ticket.getSeat().getSeatNum());
            t.put("phone1", ticket.getPhone1());
            if (ticket.getPhone2() != null && !"null".equals(ticket.getPhone2())) {
                t.put("phone2", ticket.getPhone2());
            }
            String note = ticket.getNote() == null? StringUtils.EMPTY : ticket.getNote();
            t.put("note", note);
            ticketsSoy.add(t);
        }

        SoyMapData data = new SoyMapData();
        String username = user.isPartner()? user.getPartnerName() : user.getUsername();
        data.put("username", username);
        data.put("tickets", ticketsSoy);
        return data;
    }
}
