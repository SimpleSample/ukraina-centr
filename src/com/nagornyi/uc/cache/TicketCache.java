package com.nagornyi.uc.cache;

import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 09.06.14
 */
public class TicketCache extends EntityCache {
    private static Logger log = Logger.getLogger(TicketCache.class.getName());
    private static Map<String, Map<String, TicketRecord>> lockedTickets = new ConcurrentHashMap<>();

    public static synchronized void lockTicket(Ticket ticket) {
        TicketRecord record = new TicketRecord();
        record.ticket = ticket;
        record.lockDate = new Date().getTime();
        record.username = ticket.getUser().getUsername();
        record.seatName = ticket.getSeat().getSeatNum();

        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(ticket.getStringParentKey());
        if (ticketsForTrip == null) {
            ticketsForTrip = new HashMap<>();
            lockedTickets.put(ticket.getStringParentKey(), ticketsForTrip);
        }

        log.info("locking ticket, user: " + record.username + ", seat: " + record.seatName);
        ticketsForTrip.put(ticket.getStringKey(), record);
    }

    public static synchronized int getLockedCount(String tripKey) {
        tryRevealLockedForTrip(tripKey);

        Map ticketsForTrip = lockedTickets.get(tripKey);
        return ticketsForTrip == null || ticketsForTrip.isEmpty()? 0 : ticketsForTrip.size();
    }

    public static synchronized List<Ticket> getLockedTickets(String tripKey) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripKey);
        if (ticketsForTrip == null) {
            return new ArrayList<>();
        }

        tryRevealLockedForTrip(tripKey);

        List<Ticket> lockedTickets = new ArrayList<Ticket>();
        for (String key: ticketsForTrip.keySet()) {
            lockedTickets.add(ticketsForTrip.get(key).ticket);
        }
        return lockedTickets;
    }

    public static synchronized Ticket getLockedTicket(String tripKey, String ticketId) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripKey);
        if (ticketsForTrip == null) {
            return null;
        }

        tryRevealLockedForTrip(tripKey);
        TicketRecord record = ticketsForTrip.get(ticketId);
        return record == null? null : record.ticket;
    }

    public static synchronized List<Seat> getLockedSeats(String tripKey) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripKey);
        if (ticketsForTrip == null) {
            return new ArrayList<>();
        }

        tryRevealLockedForTrip(tripKey);

        List<Seat> lockedSeats = new ArrayList<Seat>();
        for (String key: ticketsForTrip.keySet()) {
            lockedSeats.add(ticketsForTrip.get(key).ticket.getSeat());
        }
        return lockedSeats;
    }

    private static int tryRevealLockedForTrip(String tripKey) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripKey);
        int unlockedCount = 0;
        if (ticketsForTrip == null) {
            return unlockedCount;
        }

        Long currentTime = new Date().getTime();
        Set<String> ticketsToUnlock = new HashSet<String>();
        for (String key: ticketsForTrip.keySet()) {
            TicketRecord record = ticketsForTrip.get(key);
            if (record.lockDate + 15*60*1000 < currentTime) {
                log.info("revealing locked ticket by timeout, user: " + record.username + ", seat: " + record.seatName);
                ticketsToUnlock.add(key);
            }
        }
        unlockedCount = ticketsToUnlock.size();

        for (String key: ticketsToUnlock) {
            ticketsForTrip.remove(key);
        }

        if (ticketsForTrip.isEmpty()) {
            lockedTickets.remove(tripKey);
        }
        return unlockedCount;
    }

    public static boolean sameExists(Ticket ticket) {
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(ticket.getStringParentKey());
        if (ticketsForTrip == null) {
            return false;
        }

        for (String ticketId: ticketsForTrip.keySet()) {
            Ticket t = ticketsForTrip.get(ticketId).ticket;
            if (t.getSeat().equals(ticket.getSeat())) {
                return true;
            }
        }
        return false;

    }

    public static synchronized Ticket revealLockedTicket(String tripId, String ticketId) {
        log.info("Trying to reveal locked ticket for trip = " + tripId + ", ticketId = " + ticketId);
        Map<String, TicketRecord> ticketsForTrip = lockedTickets.get(tripId);
        if (ticketsForTrip == null) {
            return null;
        }
        TicketRecord record = ticketsForTrip.remove(ticketId);
        if (record == null) {
            return null;
        }

        if (ticketsForTrip.isEmpty()) {
            lockedTickets.remove(tripId);
        }
        log.info("revealed locked ticket, user: " + record.username + ", seat: " + record.seatName);
        return record.ticket;
    }

    public static synchronized int revealAllLockedTickets() {
        log.info("Trying to reveal all locked tickets");
        int revealedCount = 0;
        for (String tripId : lockedTickets.keySet()) {
            revealedCount += tryRevealLockedForTrip(tripId);
        }
        return revealedCount;
    }

    @Override
    public void fillCache() {
        //nothing to do here
    }

    static class TicketRecord {
        private Ticket ticket;
        private Long lockDate;

        // mostly for debugging purposes
        private String username;
        private String seatName;
    }
}
