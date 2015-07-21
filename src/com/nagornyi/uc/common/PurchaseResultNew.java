package com.nagornyi.uc.common;

import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;

import java.util.*;

/**
 * Created by artemnagorny on 04.09.15.
 */
public class PurchaseResultNew {

    private Map<String, List<String>> failedTickets = new HashMap<>();
    private Map<String, Trip> trips = new HashMap<>();
    private List<Ticket> allTickets = new ArrayList<Ticket>();

    public void addFailedTicket(Trip trip, String seatNum) {
        String tripKey = trip.getStringKey();
        trips.put(tripKey, trip);
        List<String> failedTicketsForTrip = failedTickets.get(tripKey);
        if (failedTicketsForTrip == null) {
            failedTicketsForTrip = new ArrayList<>();
            failedTickets.put(tripKey, failedTicketsForTrip);
        }
        failedTicketsForTrip.add(seatNum);
    }

    public void addTicket(Ticket ticket) {
        allTickets.add(ticket);
    }

    public Iterator<String> getFailedTripKeys() {
        return trips.keySet().iterator();
    }

    public Trip getTrip(String tripKey) {
        return trips.get(tripKey);
    }

    public List<String> getFailedTicketsForTrip(String tripKey) {
        return Collections.unmodifiableList(failedTickets.get(tripKey));
    }

    public boolean hasFailedTickets() {
        return trips.size() > 0;
    }

    public List<Ticket> getAllTickets() {
        return Collections.unmodifiableList(allTickets);
    }
}
