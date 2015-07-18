package com.nagornyi.uc.action.dev;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.List;

/**
 * @author Nagornyi
 *         Date: 30.06.14
 */
@Authorized(role = Role.ADMIN)
public class ClearDBAction implements Action {
    
    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        Class<? extends EntityWrapper>[] kinds = new Class[]{Ticket.class, Trip.class, Order.class, Price.class, Discount.class,
                RouteLink.class, Route.class, City.class, Country.class, Bus.class, User.class};
        
        for(Class kind : kinds) {
            List<? extends EntityWrapper> entities = DAOFacade.findAll(kind);
            for (EntityWrapper e: entities) {
                DAOFacade.delete(e);
            }
        }
    }
}
