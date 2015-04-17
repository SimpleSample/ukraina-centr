package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.List;

/**
 * @author Nagornyi
 * Date: 21.06.14
 */
public class RemoveTicketAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String ticketId = req.getParam("ticketId");
        Ticket ticket = DAOFacade.findById(Ticket.class, KeyFactory.stringToKey(ticketId));

        Order order = ticket.getOrder();
        if (order == null) {
            DAOFacade.getDAO(Ticket.class).delete(ticket);
            return;
        }
        List<Ticket> tickets = order.getTickets();
        DAOFacade.getDAO(Ticket.class).delete(ticket);
        if (tickets.size() == 1) DAOFacade.getDAO(Order.class).delete(order);
    }
}
