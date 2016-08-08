package com.nagornyi.uc.dao;

import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.entity.DiscountCategory;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Nagorny
 * Date: 22.05.14
 */
public interface ITicketDAO extends DAO<Ticket> {

    PaginationBatch<Ticket> getNextBatch(String userEmail, String startCursor, int limit);

    int countAllTicketsForUser(String userEmail);

    int countReservedTicketsForTrip(Trip trip);

    List<Seat> getUnavailableSeatsForTrip(Trip trip);

    List<Ticket> getTicketsForTrip(String tripKey);

    List<Ticket> getAllNotPayedTicketsTillDate(Date tillDate);

    boolean sameTicketExists(Ticket newTicket);

    void lockTicket(Ticket ticket);

    Ticket revealLockedTicket(String tripId, String ticketId);

    int revealAllLockedTickets();

    Ticket createReservedTicket(String ticketId, Trip trip, Seat seat, String passenger, String phone1, String phone2, User user,
                                String startCityId, String endCityId, Date startDate, boolean isPartial, DiscountCategory category, Order order, String note);

    List<Ticket> getAllTicketsForUserTillDate(User user, Date endDate);

    Set<Key> deleteAllTicketsForUserTillDate(User user, Date endDate);

    Set<Key> deleteTicketsForTrip(String tripKey);
}
