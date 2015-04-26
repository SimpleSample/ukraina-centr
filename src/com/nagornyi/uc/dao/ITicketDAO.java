package com.nagornyi.uc.dao;

import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Date;
import java.util.List;

/**
 * @author Nagorny
 * Date: 22.05.14
 */
public interface ITicketDAO extends DAO<Ticket> {

    int getFreeSeatsNumberForTrip(Trip trip);

    PaginationBatch<Ticket> getNextBatch(User user, String startCursor, int limit);

    List<Seat> getUnavailableSeatsForTrip(Trip trip);

    List<Ticket> getTicketsForTrip(Trip trip);

    boolean sameTicketExists(Ticket newTicket);

    void lockTicket(Ticket ticket);

    Ticket revealLockedTicket(String tripId, String ticketId);

    Ticket createReservedTicket(String ticketId, Trip trip, Seat seat, String passenger, String phone1, String phone2, User user,
                                String startCityId, String endCityId, Date startDate, boolean isPartial, DiscountCategory category, Order order);
}
