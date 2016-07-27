package com.nagornyi.uc.dto.trip;

import com.nagornyi.uc.dto.ticket.Ticket;

import java.util.List;

public class Trip extends PricedTrip {

    private List<Ticket> tickets;

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
