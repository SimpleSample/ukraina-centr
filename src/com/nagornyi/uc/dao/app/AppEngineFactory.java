package com.nagornyi.uc.dao.app;

import com.nagornyi.uc.dao.DAO;
import com.nagornyi.uc.dao.DAOFactory;
import com.nagornyi.uc.entity.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nagorny
 * Date: 12.05.14
 */
public class AppEngineFactory implements DAOFactory {

    private static Map<Class, DAO> map = new ConcurrentHashMap<Class, DAO>();

    static {
        map.put(User.class, new UserDAO());
        map.put(Bus.class, new BusDAO());
        map.put(Seat.class, new SeatDAO());
        map.put(Price.class, new PriceDAO());
        map.put(City.class, new CityDAO());
        map.put(Country.class, new CountryDAO());
        map.put(Route.class, new RouteDAO());
        map.put(RouteLink.class, new RouteLinkDAO());
        map.put(EntityWrapper.class, new EntityDAO());
        map.put(Trip.class, new TripDAO());
        map.put(Ticket.class, new TicketDAO());
        map.put(Discount.class, new DiscountDAO());
        map.put(Order.class, new OrderDAO());
        map.put(Feedback.class, new FeedbackDAO());
    }

    @Override
    public <T extends DAO> T getDAO(Class kind) {
        return (T)map.get(kind);
    }
}
