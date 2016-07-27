package com.nagornyi.uc.action.tickets;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import com.nagornyi.uc.util.ActionUtil;

import static com.nagornyi.uc.Constants.TICKETS;
import static com.nagornyi.uc.Constants.TICKET_ID;
import static com.nagornyi.uc.Constants.TRIP_ID;

@Authorized
public class UnlockTicketsAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ActionUtil.checkRequired(req, TICKETS);
        JSONArray array = new JSONArray((String)req.getParam(TICKETS));
        if (array.length() == 0) {
            return;
        }
        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
        for (int i = 0, size = array.length(); i < size; i++) {
            JSONObject ticket = array.getJSONObject(i);
            dao.revealLockedTicket(ticket.getString(TRIP_ID), ticket.getString(TICKET_ID));
        }
    }
}
