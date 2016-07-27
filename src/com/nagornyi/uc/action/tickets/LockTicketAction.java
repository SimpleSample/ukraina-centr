package com.nagornyi.uc.action.tickets;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.converter.TicketConverter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import com.nagornyi.uc.util.ActionUtil;

import java.util.UUID;

import static com.nagornyi.uc.Constants.FORTH;
import static com.nagornyi.uc.Constants.TICKET;
import static com.nagornyi.uc.Constants.TRIP_ID;
import static com.nagornyi.uc.Constants.UNLOCK_TICKET_ID;

/**
 * @author Nagornyi
 * Date: 09.06.14
 */
@Authorized
public class LockTicketAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ActionUtil.checkRequired(req, TRIP_ID, TICKET, FORTH);

        String tripId = req.getParam(TRIP_ID);
        Boolean isUserForth = Boolean.valueOf((String)req.getParam(FORTH));
        Trip trip = DAOFacade.findByKey(Trip.class, KeyFactory.stringToKey(tripId));
        JSONObject ticketObj = new JSONObject((String)req.getParam(TICKET));
        String id = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(id, tripId);
        TicketConverter.populateTicketFromJson(ticket, ticketObj, isUserForth);
        TicketConverter.populateEmptyFieldsFromTrip(ticket, trip);
        ticket.setUser(req.getUser());
        ticket.setStatus(Ticket.Status.LOCKED);
        ticket.setCalculatedPrice(0.0); // throws NPE when GETting it. wtf?

        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
        if (dao.sameTicketExists(ticket)) throw new UserFriendlyException("На жаль, цей квиток вже придбано");

        dao.lockTicket(ticket);

        JSONObject responseObj = new JSONObject();
        responseObj.put("ticketId", ticket.getStringKey());
        resp.setDataObject(responseObj);

        if (req.getParam(UNLOCK_TICKET_ID) != null) {
            dao.revealLockedTicket(tripId, (String) req.getParam(UNLOCK_TICKET_ID));
        }
    }
}
