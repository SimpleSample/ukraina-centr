package com.nagornyi.uc.cache;

import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nagornyi
 * Date: 09.06.14
 */
public class TicketCache extends EntityCache {
    private static Map<String, Map<String, TicketRecord>> lockedTickets = new ConcurrentHashMap<String, Map<String, TicketRecord>>();

    public static synchronized void lockTicket(Ticket ticket) {
        TicketRecord record = new TicketRecord();
        record.ticket = ticket;
        record.lockDate = new Date().getTime();

        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(ticket.getStringParentKey());
        if (ticketsForTrip == null) {
            ticketsForTrip = new HashMap<String, TicketRecord>();
            lockedTickets.put(ticket.getStringParentKey(), ticketsForTrip);
        }


        ticketsForTrip.put(ticket.getStringKey(), record);

    }

    public static synchronized int getLockedCount(String tripKey) {
        revealLockedForTrip(tripKey);

        Map ticketsForTrip = lockedTickets.get(tripKey);
        return ticketsForTrip == null || ticketsForTrip.isEmpty()? 0 : ticketsForTrip.size();
    }

    public static synchronized List<Seat> getLockedSeats(String tripKey) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripKey);
        if (ticketsForTrip == null) return new ArrayList<Seat>();

        revealLockedForTrip(tripKey);

        List<Seat> lockedSeats = new ArrayList<Seat>();
        for (String key: ticketsForTrip.keySet()) {
            lockedSeats.add(ticketsForTrip.get(key).ticket.getSeat());
        }
        return lockedSeats;
    }

    private static synchronized void revealLockedForTrip(String tripKey) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripKey);
        if (ticketsForTrip == null) return;

        Long currentTime = new Date().getTime();
        Set<String> ticketsToUnlock = new HashSet<String>();
        for (String key: ticketsForTrip.keySet()) {
            TicketRecord record = ticketsForTrip.get(key);
            if (record.lockDate + 15*60*1000 < currentTime) {
                ticketsToUnlock.add(key);
            }
        }

        for (String key: ticketsToUnlock) {
            ticketsForTrip.remove(key);
        }

        if (ticketsForTrip.isEmpty()) {
            lockedTickets.remove(tripKey);
        }
    }

    public static boolean sameExists(Ticket ticket) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(ticket.getStringParentKey());
        if (ticketsForTrip == null) return false;

        for (String ticketId: ticketsForTrip.keySet()) {
            Ticket t = ticketsForTrip.get(ticketId).ticket;
            if (t.getSeat().equals(ticket.getSeat())) {
                return true;
            }
        }
        return false;

    }

    public static synchronized Ticket pollLockedTicket(String tripId, String ticketId) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripId);
        if (ticketsForTrip == null) return null;
        TicketRecord record = ticketsForTrip.remove(ticketId);
        if (record == null) return null;

        if (ticketsForTrip.isEmpty()) {
            lockedTickets.remove(tripId);
        }

        return record.ticket;

    }

    @Override
    public void fillCache() {
        //nothing to do here
    }

    static class TicketRecord {
        private Ticket ticket;
        private Long lockDate;
    }
}
