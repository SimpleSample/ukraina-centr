package com.nagornyi.uc.common;

import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yanko on 25.05.14.
 */
public class ReservationResult {
    private List<String> forthFailedTickets = new ArrayList<String>();
    private List<String> backFailedTickets = new ArrayList<String>();
    private Trip forthTrip;
    private Trip backTrip;
    private List<Ticket> allTickets = new ArrayList<Ticket>();

    public ReservationResult(Trip forthTrip, Trip backTrip) {
        this.forthTrip = forthTrip;
        this.backTrip = backTrip;
    }

    public void addTicket(Ticket ticket) {
        allTickets.add(ticket);
    }

    public List<Ticket> getAllTickets() {
        return allTickets;
    }

    public void addForthFailed(String seatNum) {
        forthFailedTickets.add(seatNum);
    }

    public void addBackFailed(String seatNum) {
        backFailedTickets.add(seatNum);
    }

    public boolean hasAnyFailed() {
        return !forthFailedTickets.isEmpty() || !backFailedTickets.isEmpty();
    }

    public Trip getForthTrip() {
        return forthTrip;
    }

    public Trip getBackTrip() {
        return backTrip;
    }

    public List<String> getForthFailedTickets() {
        return forthFailedTickets;
    }

    public List<String> getBackFailedTickets() {
        return backFailedTickets;
    }
}
