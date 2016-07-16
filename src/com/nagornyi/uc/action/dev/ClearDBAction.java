package com.nagornyi.uc.action.dev;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.action.Action;
import com.nagornyi.uc.action.Authorized;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.Bus;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Country;
import com.nagornyi.uc.entity.Discount;
import com.nagornyi.uc.entity.EntityWrapper;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Price;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.RouteLink;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 *         Date: 30.06.14
 */
@Authorized(role = Role.ADMIN)
public class ClearDBAction implements Action {
    private static final Logger log = Logger.getLogger(RunScriptsAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        if(true) return;
        Class<? extends EntityWrapper>[] kinds = new Class[]{Ticket.class, Trip.class, Order.class, Price.class, Discount.class,
                RouteLink.class, Route.class, City.class, Country.class, Bus.class, User.class};
        
        for(Class kind : kinds) {
            List<? extends EntityWrapper> entities = DAOFacade.findAll(kind);
            for (EntityWrapper e: entities) {
                DAOFacade.delete(e);
            }
        }
    }

    private void clearTrips(){
        Date startDate = new Date(1463011200000L);
        Date endDate = new Date(1683849600000L);

        ITripDAO tripDAO = DAOFacade.getDAO(Trip.class);
        ITicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);
        Route route = DAOFacade.findAll(Route.class).get(0);
        List<Trip> trips = tripDAO.getTripsByDateRange(route, startDate, endDate);
        log.info("Trips count " + trips.size());
        for (Trip trip: trips) {
            ticketDAO.deleteForQuery(ticketDAO.getQueryByParent(trip.getKey()));
            DAOFacade.delete(trip);
        }
    }
}
