package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DeleteContext;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PostDelete;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.nagornyi.uc.cache.TicketCache;
import com.nagornyi.uc.common.DiscountCalculator;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IPriceDAO;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.PaginationBatch;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Discount;
import com.nagornyi.uc.entity.DiscountCategory;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Price;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Nagorny
 * Date: 22.05.14
 */
public class TicketDAO extends EntityDAO<Ticket> implements ITicketDAO {
    private static final String START_DATE_FIELD = "startDate";
    private static final String USER_FIELD = "user";

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
    public PaginationBatch<Ticket> getNextBatch(String userEmail, String startCursor, int limit) {
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(limit);
        if (startCursor != null) {
            fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor));
        }

        Query query = new Query(getKind())
                .setFilter(new Query.FilterPredicate(USER_FIELD, Query.FilterOperator.EQUAL, userEmail))
                .addSort(START_DATE_FIELD, Query.SortDirection.DESCENDING);

        QueryResultList<Entity> results = datastore.prepare(query).asQueryResultList(fetchOptions);
        PaginationBatch<Ticket> result = new PaginationBatch<>(results.getCursor().toWebSafeString());
        for (Entity entity: results) {
            result.addEntity(createDAOEntity(entity));
        }

        return result;
    }

    @Override
    public int countAllTicketsForUser(String userEmail) {
        Query query = new Query(getKind())
                .setFilter(new Query.FilterPredicate(USER_FIELD, Query.FilterOperator.EQUAL, userEmail))
                .addSort(START_DATE_FIELD, Query.SortDirection.DESCENDING);
        return countForQuery(query);
    }

    @Override
    public int countReservedTicketsForTrip(Trip trip) {
        return countForQuery(getValidTicketsForTripQuery(trip.getKey())) + TicketCache.getLockedCount(trip.getStringKey());
    }

    @Override
    public List<Ticket> getAllNotPayedTicketsTillDate(Date tillDate) {
        Query.Filter statusFilter = new Query.FilterPredicate("status", Query.FilterOperator.EQUAL, Ticket.Status.PROCESSING.idx);
        Query.Filter tillDateFilter = new Query.FilterPredicate("statusChangedDate", Query.FilterOperator.LESS_THAN, tillDate);

        return getByFilter(Query.CompositeFilterOperator.and(statusFilter, tillDateFilter));
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
        List<Seat> seats = new ArrayList<>();
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
                .setFilter(filterByValidTicket())
                .setAncestor(tripKey);
    }

    private Query.Filter filterByValidTicket() {
        return new Query.FilterPredicate("status", Query.FilterOperator.NOT_EQUAL, Ticket.Status.INVALID.idx);
    }

    @Override
    public boolean sameTicketExists(Ticket newTicket) {
        Query.Filter seatFilter = new Query.FilterPredicate("seat", Query.FilterOperator.EQUAL, newTicket.getSeat().getKey());
        Query.Filter fullFilter = Query.CompositeFilterOperator.and(seatFilter, filterByValidTicket());

        List<Ticket> tickets = get(newTicket.getParentKey(), fullFilter, null, null);

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
    public int revealAllLockedTickets() {
        return TicketCache.revealAllLockedTickets();
    }

    @Override
    public Ticket createReservedTicket(String ticketId, Trip trip, Seat seat, String passenger, String phone1, String phone2, User user,
                               String startCityId, String endCityId, Date startDate, boolean isPartial, DiscountCategory category, Order order, String note) {
        DiscountCategory discountCategory = category == null? DiscountCategory.NONE : category;

        log.info("\tCreating ticket, trip: " + trip.getTripName()+ "("+ DateFormatter.defaultFormat(startDate)+"), seat: " +
                seat.getSeatNum() + ", passenger: " + passenger + ", discount: " + discountCategory.getDiscount().getName());
        Ticket ticket;
        if (ticketId == null) {
            ticket = new Ticket(trip);
            ticket.setSeat(seat);
            ticket.setUserEmail(user.getEmail());
        } else {
            ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
            ticket = dao.revealLockedTicket(trip.getStringKey(), ticketId);
            if (ticket == null) {
                log.warning("Could not find ticket " + ticketId + " in cache");
                return null;
            }
        }

        ticket.setPassenger(passenger);
        if (phone1 != null) {
            ticket.setPhone1(phone1);
        }
        if (phone2 != null) {
            ticket.setPhone2(phone2);
        }
        if (user.isPartner()) {
            ticket.setStatus(Ticket.Status.RESERVED);
        } else {
            ticket.setStatus(Ticket.Status.PROCESSING);
        }
        ticket.setStartCity(DAOFacade.findByKey(City.class, KeyFactory.stringToKey(startCityId)));
        ticket.setEndCity(DAOFacade.findByKey(City.class, KeyFactory.stringToKey(endCityId)));
        ticket.setStartDate(startDate);
        ticket.setPartial(isPartial);

        double resultPrice = getPrice(startCityId, endCityId, user, discountCategory, isPartial);
        log.info("Calculated price for ticket " + resultPrice);
        ticket.setCalculatedPrice(resultPrice);

        if (order != null) {
            ticket.setOrder(order);
        }
        if (note != null) {
            ticket.setNote(note);
        }

        return ticket;
    }

    public double getPrice(String startCityId, String endCityId, User user, DiscountCategory category, boolean isPartial) {
        IPriceDAO priceDAO = DAOFacade.getDAO(Price.class);
        Price p = priceDAO.getPriceByCities(startCityId, endCityId);
        List<Discount> discounts = new ArrayList<>();
        if (user.getDiscount() != null) {
            discounts.add(user.getDiscount());
        }
        if (category != null) {
            discounts.add(category.getDiscount());
        }
        return new DiscountCalculator().calculate(isPartial? p.getPriceBoth()/2 : p.getPrice(), discounts);
    }

    @Override
    public List<Ticket> getAllTicketsForUserTillDate(User user, Date endDate) {
        Query query = ticketQuery.getTicketsForUserByPeriodQuery(user, endDate);

        return getByQuery(query);
    }

    @Override
    public Set<Key> deleteAllTicketsForUserTillDate(User user, Date endDate) {
        Query query = ticketQuery.getTicketsForUserByPeriodQuery(user, endDate);

        return deleteForQuery(query);
    }

    @Override
    public Set<Key> deleteTicketsForTrip(String tripKey) {
        return deleteForQuery(getQueryByParent(KeyFactory.stringToKey(tripKey)));
    }

    @Override
    public Ticket getById(Key id) {
        Ticket lockedTicket = TicketCache.getLockedTicket(KeyFactory.keyToString(id.getParent()),
                KeyFactory.keyToString(id));

        return lockedTicket == null ? super.getById(id) : lockedTicket;
    }

    @Override
    public void delete(Ticket entity) {
        Ticket revealingTicket = TicketCache.revealLockedTicket(entity.getStringParentKey(), entity.getStringKey());
        //if not from cache
        if (revealingTicket == null) {
            log.info(String.format("Deleting ticket %s, passenger: %s", entity.getStringKey(), entity.getPassenger()));

            super.delete(entity);
        }
    }

    @Override
    public void delete(List<Ticket> tickets) {
        for (Ticket ticket: tickets) {
            log.info(String.format("Deleting ticket %s, passenger: %s", ticket.getStringKey(), ticket.getPassenger()));
        }
        super.delete(tickets);
    }

    @Override
    protected void logDeletionByKeys(Set<Key> keys) {
        for (Key ticketKey: keys) {
            log.info(String.format("Deleting ticket %s", KeyFactory.keyToString(ticketKey)));
        }
    }

    @SuppressWarnings("unused")
    @PostDelete(kinds = "Trip")
    public void postDeleteTrip(DeleteContext context) {
        deleteTicketsForTrip(KeyFactory.keyToString(context.getCurrentElement()));
    }

    // queries

    private final class DaoQuery {

        Query getTicketsForUserByPeriodQuery(User user, Date endDate) {

            Query.FilterPredicate userFilter = new Query.FilterPredicate(USER_FIELD,
                    Query.FilterOperator.EQUAL,
                    user.getEmail());

            Query.FilterPredicate endDateLessThan = new Query.FilterPredicate(START_DATE_FIELD,
                    Query.FilterOperator.LESS_THAN_OR_EQUAL,
                    endDate == null? new Date() : endDate);

            Query.Filter resultFilter =
                    Query.CompositeFilterOperator.and(userFilter, endDateLessThan);

            return new Query(getKind()).setFilter(resultFilter);
        }
    }
}
