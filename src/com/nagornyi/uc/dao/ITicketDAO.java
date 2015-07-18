package com.nagornyi.uc.dao;

import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Nagorny
 * Date: 22.05.14
 */
public interface ITicketDAO extends DAO<Ticket> {

    PaginationBatch<Ticket> getNextBatch(User user, String startCursor, int limit);

    int countAllTicketsForUser(User user);

    List<Seat> getUnavailableSeatsForTrip(Trip trip);

    List<Ticket> getTicketsForTrip(Trip trip);

    boolean sameTicketExists(Ticket newTicket);

    void lockTicket(Ticket ticket);

    Ticket revealLockedTicket(String tripId, String ticketId);

    Ticket createReservedTicket(String ticketId, Trip trip, Seat seat, String passenger, String phone1, String phone2, User user,
                                String startCityId, String endCityId, Date startDate, boolean isPartial, DiscountCategory category, Order order, String note);

    List<Ticket> getTicketsForUserByPeriod(User user, Date endDate);

    Set<Key> deleteTicketsForUserByPeriod(User user, Date endDate);
}
