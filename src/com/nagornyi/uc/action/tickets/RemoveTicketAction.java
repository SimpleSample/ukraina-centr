package com.nagornyi.uc.action.tickets;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.org.json.JSONException;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 21.06.14
 */
@Authorized
public class RemoveTicketAction implements Action {
    private static Logger log = Logger.getLogger(RemoveTicketAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String ticketId = req.getParam("entityId");
        Ticket ticket = DAOFacade.findById(Ticket.class, KeyFactory.stringToKey(ticketId));

        if (ticket == null) return;

        Order order = ticket.getOrder();
        if (order == null) {
            DAOFacade.getDAO(Ticket.class).delete(ticket);
        } else {
            List<Ticket> tickets = order.getTickets();
            DAOFacade.getDAO(Ticket.class).delete(ticket);
            if (tickets.size() == 1) {
                log.info("Removing order " + order.getExternalId() + " for user " + req.getUserEmail());
                DAOFacade.getDAO(Order.class).delete(order);
            }
        }
    }
}
