package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import com.nagornyi.uc.util.ActionUtil;

import java.util.UUID;

import static com.nagornyi.uc.Constants.*;

/**
 * @author Nagornyi
 * Date: 09.06.14
 */
@Authorized
public class LockTicketAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ActionUtil.checkRequired(req, TRIP_ID, SEAT_ID);

        String tripId = req.getParam(TRIP_ID);
        String seatId = req.getParam(SEAT_ID);
        Seat seat = DAOFacade.findByKey(Seat.class, KeyFactory.stringToKey(seatId));

        String id = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(id, tripId);
        ticket.setSeat(seat);
        ticket.setUser(req.getUser());

        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
        if (dao.sameTicketExists(ticket)) throw new UserFriendlyException("На жаль, цей квиток вже заброньовано");

        dao.lockTicket(ticket);

        JSONObject responseObj = new JSONObject();
        responseObj.put("ticketId", ticket.getStringKey());
        resp.setDataObject(responseObj);

        if (req.getParam(UNLOCK_TICKET_ID) != null) {
            dao.pollLockedTicket(tripId, (String)req.getParam(UNLOCK_TICKET_ID));
        }
    }
}
