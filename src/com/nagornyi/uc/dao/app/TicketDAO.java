package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.*;
import com.nagornyi.uc.cache.TicketCache;
import com.nagornyi.uc.common.DiscountCalculator;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IPriceDAO;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.PaginationBatch;
import com.nagornyi.uc.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Nagorny
 * Date: 22.05.14
 */
public class TicketDAO extends EntityDAO<Ticket> implements ITicketDAO {

    @Override
    protected Ticket createDAOEntity(Entity entity) {
        return new Ticket(entity);
    }

    @Override
    protected String getKind() {
        return "Ticket";
    }

    @Override
    public int getFreeSeatsNumberForTrip(Trip trip) {
        int count = countForQuery(new Query(getKind()).setAncestor(trip.getKey()).
                setFilter(new Query.FilterPredicate("status",
                        Query.FilterOperator.NOT_EQUAL,
                        Ticket.Status.INVALID.idx)));
        return trip.getSeatsNum() - count - TicketCache.getLockedCount(trip.getStringKey());
    }

    @Override
    public PaginationBatch<Ticket> getNextBatch(User user, String startCursor, int limit) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(limit);
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }

        Query query = new Query(getKind())
                .setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user.getEmail()))
                .addSort("startDate", Query.SortDirection.DESCENDING);

        QueryResultList<Entity> results = datastore.prepare(query).asQueryResultList(fetchOptions);
        PaginationBatch<Ticket> result = new PaginationBatch<Ticket>(results.getCursor().toWebSafeString());
        for (Entity entity: results) {
            result.addEntity(createDAOEntity(entity));
        }

        return result;
    }

    @Override
    public List<Ticket> getTicketsForTrip(Trip trip) {
       return getValidTicketsForTrip(trip);
    }

    @Override
    public List<Seat> getUnavailableSeatsForTrip(Trip trip) {
        List<Ticket> tickets = getValidTicketsForTrip(trip);
        List<Seat> seats = new ArrayList<Seat>();
        for(Ticket t: tickets) {
            seats.add(t.getSeat());
        }

        seats.addAll(TicketCache.getLockedSeats(trip.getStringKey()));
        return seats;
    }
    
    private List<Ticket> getValidTicketsForTrip(Trip trip) {
        return get(trip.getKey(),
                new Query.FilterPredicate("status",
                        Query.FilterOperator.NOT_EQUAL,
                        Ticket.Status.INVALID.idx), null, null);
    }

    @Override
    public boolean sameTicketExists(Ticket newTicket) {
        List<Ticket> tickets = get(newTicket.getParentKey(),
                new Query.FilterPredicate("seat",
                        Query.FilterOperator.EQUAL,
                        newTicket.getSeat().getKey()), null, null);

        return !tickets.isEmpty() || TicketCache.sameExists(newTicket);
    }

    @Override
    public void lockTicket(Ticket ticket) {
        TicketCache.lockTicket(ticket);
    }

    @Override
    public Ticket pollLockedTicket(String tripId, String ticketId) {
        return TicketCache.pollLockedTicket(tripId, ticketId);
    }

    public Ticket createReservedTicket(String ticketId, Trip trip, Seat seat, String passenger, String phone1, String phone2, User user,
                               String startCityId, String endCityId, Date startDate, boolean isPartial, DiscountCategory category, Order order) {
        Ticket ticket;
        if (ticketId == null) {
            ticket = new Ticket(trip);
            ticket.setSeat(seat);
            ticket.setUser(user);
        } else {
            ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
            ticket = dao.pollLockedTicket(trip.getStringKey(), ticketId);
            if (ticket == null) return null;
        }

        ticket.setPassenger(passenger);
        if (phone1 != null) ticket.setPhone1(phone1);
        if (phone2 != null) ticket.setPhone2(phone2);
        if (user.isPartner()) {
            ticket.setStatus(Ticket.Status.RESERVED);
        } else {
            ticket.setStatus(Ticket.Status.PROCESSING);
        }
        ticket.setStartCity(DAOFacade.findByKey(City.class, KeyFactory.stringToKey(startCityId)));
        ticket.setEndCity(DAOFacade.findByKey(City.class, KeyFactory.stringToKey(endCityId)));
        ticket.setStartDate(startDate);
        ticket.setPartial(isPartial);

        double resultPrice = getPrice(startCityId, endCityId, user, category, isPartial);
        ticket.setCalculatedPrice(resultPrice);

        if (order != null) ticket.setOrder(order);

        return ticket;
    }

    public double getPrice(String startCityId, String endCityId, User user, DiscountCategory category, boolean isPartial) {
        IPriceDAO priceDAO = DAOFacade.getDAO(Price.class);
        Price p = priceDAO.getPriceByCities(startCityId, endCityId);
        List<Discount> discounts = new ArrayList<Discount>();
        if (user.getDiscount() != null) discounts.add(user.getDiscount());
        if (category != null) discounts.add(category.getDiscount());
        return new DiscountCalculator().calculate(isPartial? p.getPriceBoth()/2 : p.getPrice(), discounts);
    }
}
