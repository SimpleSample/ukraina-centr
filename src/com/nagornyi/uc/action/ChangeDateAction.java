package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * @author Nagornyi
 *         Date: 03.07.14
 */
@Authorized(role = Role.PARTNER)
public class ChangeDateAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String tripId = req.getParam("tripId");
        String ticketId = req.getParam("ticketId");
        String seatId = req.getParam("seatId");

        Trip trip = DAOFacade.findById(Trip.class, KeyFactory.stringToKey(tripId));
        Seat seat = DAOFacade.findById(Seat.class, KeyFactory.stringToKey(seatId));
        Ticket ticket = DAOFacade.findById(Ticket.class, KeyFactory.stringToKey(ticketId));

        Ticket newTicket = new Ticket(trip);
        newTicket.setSeat(seat);
        newTicket.setOrder(ticket.getOrder());
        newTicket.setCalculatedPrice(ticket.getCalculatedPrice());
        newTicket.setStatus(ticket.getStatus());
        newTicket.setPassenger(ticket.getPassenger());
        newTicket.setPhone1(ticket.getPhone1());
        newTicket.setPhone2(ticket.getPhone2());
        newTicket.setPartial(ticket.isPartial());
        newTicket.setStartDate(trip.getStartDate());
        newTicket.setStartCity(ticket.getStartCity());
        newTicket.setEndCity(ticket.getEndCity());

        DAOFacade.delete(ticket);
        DAOFacade.save(newTicket);

        JSONObject respObj = new JSONObject();
        respObj.put("newTicketId", newTicket.getStringKey());
        resp.setDataObject(respObj);
    }
}
