package com.nagornyi.uc.action.tickets;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.common.PurchaseResultNew;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.service.OrderService;
import com.nagornyi.uc.service.ServiceLocator;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Iterator;

/**
 * Created by artemnagorny on 30.08.15.
 */
public class SaveTicketsAction implements Action {

    private static final String REMOVED_STATUS = "removed";
    private static final String ADDED_STATUS = "added";

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        JSONObject changedTickets = new JSONObject((String)req.getParam("changedTickets"));

        User user = req.getUser();
        Order order = new Order(user);
        order.setStatus(Order.Status.SUCCESS);
        DAOFacade.save(order);

        OrderService orderService = ServiceLocator.getInstance().getOrderService();

        Iterator tripKeys = changedTickets.keys();
        PurchaseResultNew result = new PurchaseResultNew();
        while (tripKeys.hasNext()) {
            String tripKey = (String)tripKeys.next();
            Trip trip = DAOFacade.findByKey(Trip.class, KeyFactory.stringToKey(tripKey));
            JSONObject tickets = changedTickets.getJSONObject(tripKey);
            Iterator ticketKeys = tickets.keys();
            while (ticketKeys.hasNext()) {
                String ticketKey = (String)ticketKeys.next();
                JSONObject ticketObj = tickets.getJSONObject(ticketKey);
                String editStatus = ticketObj.getString("editStatus");
                if (REMOVED_STATUS.equals(editStatus)) {
                    DAOFacade.delete(DAOFacade.findByKey(Ticket.class, KeyFactory.stringToKey(ticketKey)));
                } else {
                    orderService.purchaseTicket(result, ticketObj, user, order, trip, false, true);
                }
            }
        }

        if (result.hasFailedTickets()) {
            String message = orderService.createFailedTicketsMessage(result);
            throw new UserFriendlyException(message);
        } else {
            DAOFacade.bulkSave(result.getAllTickets());
            DAOFacade.save(order);
        }
    }
}
