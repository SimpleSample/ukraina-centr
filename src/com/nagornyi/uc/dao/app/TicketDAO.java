package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.*;
import com.nagornyi.uc.cache.TicketCache;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.common.DiscountCalculator;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IPriceDAO;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.PaginationBatch;
import com.nagornyi.uc.entity.*;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 22.05.14
 */
public class TicketDAO extends EntityDAO<Ticket> implements ITicketDAO {
    private static Logger log = Logger.getLogger(TicketDAO.class.getName());

    private DaoQuery ticketQuery = new DaoQuery();

    @Override
    protected Ticket createDAOEntity(Entity entity) {
        return new Ticket(entity);
    }

    @Override
    protected String getKind() {
        return "Ticket";
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
    public int countAllTicketsForUser(User user) {
        Query query = new Query(getKind())
                .setFilter(new Query.FilterPredicate("user", Query.FilterOperator.EQUAL, user.getEmail()))
                .addSort("startDate", Query.SortDirection.DESCENDING);
        return countForQuery(query);
    }

    @Override
    public int countReservedTicketsForTrip(Trip trip) {
        return countForQuery(getValidTicketsForTripQuery(trip.getKey())) + TicketCache.getLockedCount(trip.getStringKey());
    }

    @Override
    public List<Ticket> getTicketsForTrip(String tripKey) {
        List<Ticket> tickets = getValidTicketsForTrip(KeyFactory.stringToKey(tripKey));
        tickets.addAll(TicketCache.getLockedTickets(tripKey));
        return tickets;
    }

    @Override
    public List<Seat> getUnavailableSeatsForTrip(Trip trip) {
        List<Ticket> tickets = getValidTicketsForTrip(trip.getKey());
        List<Seat> seats = new ArrayList<Seat>();
        for(Ticket t: tickets) {
            seats.add(t.getSeat());
        }

        seats.addAll(TicketCache.getLockedSeats(trip.getStringKey()));
        return seats;
    }
    
    private List<Ticket> getValidTicketsForTrip(Key tripKey) {
        return getByQuery(getValidTicketsForTripQuery(tripKey));
    }

    private Query getValidTicketsForTripQuery(Key tripKey) {
        return new Query(getKind())
                .setFilter(new Query.FilterPredicate("status", Query.FilterOperator.NOT_EQUAL, Ticket.Status.INVALID.idx))
                .setAncestor(tripKey);
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
    public Ticket revealLockedTicket(String tripId, String ticketId) {
        return TicketCache.revealLockedTicket(tripId, ticketId);
    }

    @Override
    public Ticket createReservedTicket(String ticketId, Trip trip, Seat seat, String passenger, String phone1, String phone2, User user,
                               String startCityId, String endCityId, Date startDate, boolean isPartial, DiscountCategory category, Order order, String note) {

        if (category == null) category = DiscountCategory.NONE;
        log.info("\tCreating ticket, trip: " + trip.getTripName()+ "("+ DateFormatter.defaultFormat(startDate)+"), seat: " +
                seat.getSeatNum() + ", passenger: " + passenger + ", discount: " + category.getDiscount().getName());
        Ticket ticket;
        if (ticketId == null) {
            ticket = new Ticket(trip);
            ticket.setSeat(seat);
            ticket.setUser(user);
        } else {
            ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
            ticket = dao.revealLockedTicket(trip.getStringKey(), ticketId);
            if (ticket == null) {
                log.warning("Could not find ticket " + ticketId + " in cache");
                return null;
            }
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
        log.info("Calculated price for ticket " + resultPrice);
        ticket.setCalculatedPrice(resultPrice);

        if (order != null) ticket.setOrder(order);
        if (note != null) ticket.setNote(note);

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

    @Override
    public List<Ticket> getTicketsForUserByPeriod(User user, Date endDate) {
        Query query = ticketQuery.getTicketsForUserByPeriodQuery(user, endDate);

        return getByQuery(query);
    }

    @Override
    public Set<Key> deleteTicketsForUserByPeriod(User user, Date endDate) {
        Query query = ticketQuery.getTicketsForUserByPeriodQuery(user, endDate);

        return deleteForQuery(query);
    }

    @Override
    public Ticket getById(Key id) {
        Ticket lockedTicket = TicketCache.getLockedTicket(KeyFactory.keyToString(id.getParent()),
                KeyFactory.keyToString(id));

        return lockedTicket == null? super.getById(id) : lockedTicket;
    }

    @Override
    public void delete(Ticket entity) {
        Ticket revealingTicket = TicketCache.revealLockedTicket(entity.getStringParentKey(),
                KeyFactory.keyToString(entity.getKey()));
        //if not from cache
        if (revealingTicket == null) {
            super.delete(entity);
        }
    }

    // queries

    public class DaoQuery {

        public Query getTicketsForUserByPeriodQuery(User user, Date endDate) {
            if (endDate == null) endDate = new Date();

            Query.FilterPredicate userFilter = new Query.FilterPredicate("user",
                    Query.FilterOperator.EQUAL,
                    user.getEmail());

            Query.FilterPredicate endDateLessThan = new Query.FilterPredicate("startDate",
                    Query.FilterOperator.LESS_THAN_OR_EQUAL,
                    endDate);

            Query.Filter resultFilter =
                    Query.CompositeFilterOperator.and(userFilter, endDateLessThan);

            return new Query(getKind()).setFilter(resultFilter);
        }
    }
}
