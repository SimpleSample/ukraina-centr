package com.nagornyi.uc.action.tickets;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * @author Nagornyi
 *         Date: 12.06.14
 */
@Authorized
public class TicketsForTripAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String tripId = req.getParam("tripId");
        Boolean excludeBlockedTickets = Boolean.parseBoolean((String) req.getParam("excludeBlockedTickets"));

        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);

        List<Ticket> tickets = dao.getTicketsForTrip(tripId);

        if (Boolean.TRUE.equals(excludeBlockedTickets)) {
            excludeBlockedTickets(tickets);
        }
        JSONObject respObj = new JSONObject();
        JSONArray ticketObjs = new JSONArray();
        for (Ticket ticket: tickets) {
            JSONObject obj = ticket.toJson(Locale.forLanguageTag("uk")); //todo i18n
            String transactionId = getTransactionId(ticket);
            obj.put("transactionId", transactionId);
            ticketObjs.put(obj);
        }

        respObj.put("tickets", ticketObjs);

        resp.setDataObject(respObj);
    }

    private void excludeBlockedTickets(List<Ticket> tickets) {
        Iterator<Ticket> iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket nextTicket = iterator.next();
            if (nextTicket.getSeat().isInitiallyBlocked()) {
                iterator.remove();
            }
        }
    }

    private String getTransactionId(Ticket ticket) {
        if (ticket.getOrder() != null) {
            Order order = ticket.getOrder();
            if (order.getTransactionId() != null) {
                return order.getTransactionId();
            }
        }
        return StringUtils.EMPTY;
    }
}
