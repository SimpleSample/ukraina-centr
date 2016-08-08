package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
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

        ITicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);
        Ticket newTicket = new Ticket(trip);
        newTicket.setSeat(seat);
        if (ticketDAO.sameTicketExists(newTicket)) {
            throw new UserFriendlyException("На жаль, цей квиток вже придбано");
        } else {
            if (ticket.getOrder() != null) {
                newTicket.setOrder(ticket.getOrder());
            }
            newTicket.setCalculatedPrice(ticket.getCalculatedPrice());
            newTicket.setStatus(ticket.getStatus());
            newTicket.setStatusChangedDate(ticket.getStatusChangedDate());
            newTicket.setPassenger(ticket.getPassenger());
            newTicket.setPhone1(ticket.getPhone1());
            newTicket.setPhone2(ticket.getPhone2());
            newTicket.setPartial(ticket.isPartial());
            newTicket.setStartDate(trip.getStartDate());
            newTicket.setStartCity(ticket.getStartCity());
            newTicket.setEndCity(ticket.getEndCity());
            newTicket.setUserEmail(ticket.getUser().getEmail());

            DAOFacade.delete(ticket);
            DAOFacade.save(newTicket);

            JSONObject respObj = new JSONObject();
            respObj.put("newTicketId", newTicket.getStringKey());
            resp.setDataObject(respObj);
        }
    }
}
