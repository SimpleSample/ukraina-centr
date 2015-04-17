package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.List;
import java.util.Locale;

/**
 * @author Nagornyi
 *         Date: 12.06.14
 */
public class TicketsForTripAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String tripId = req.getParam("tripId");

        Trip trip = DAOFacade.findById(Trip.class, KeyFactory.stringToKey(tripId));

        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);

        List<Ticket> tickets = dao.getTicketsForTrip(trip);

        JSONObject respObj = new JSONObject();
        JSONArray ticketObjs = new JSONArray();
        for (Ticket ticket: tickets) {
            JSONObject obj = ticket.toJSON(Locale.forLanguageTag("uk"));
            String transactionId = ticket.getOrder() == null? "" : ticket.getOrder().getTransactionId();
            if (transactionId == null) transactionId = "";
            obj.put("transactionId", transactionId);
            ticketObjs.put(obj);
        }

        respObj.put("tickets", ticketObjs);

        resp.setDataObject(respObj);
    }
}
