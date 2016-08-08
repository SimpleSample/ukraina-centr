package com.nagornyi.uc.service;

import com.nagornyi.uc.common.mail.MailFacade;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IOrderDAO;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TicketService {
    private static Logger log = Logger.getLogger(TicketService.class.getName());

    public int deleteUnpaidTicketsForLastDay() {
        ITicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);

        int revealedCount = ticketDAO.revealAllLockedTickets();
        log.info("Revealed locked tickets: " + revealedCount);

        List<Ticket> tickets = ticketDAO.getAllNotPayedTicketsTillDate(getDateTillThisDay());
        log.info("Found processing tickets: " + tickets.size());

        SearchResult groupedByUser = getGroupedByUser(tickets);
        for (Map.Entry<String, List<Ticket>> ticketEntry: groupedByUser.tickets.entrySet()) {
            MailFacade.sendTimedOutTicketsToBeRemoved(groupedByUser.users.get(ticketEntry.getKey()), ticketEntry.getValue());
        }

        IOrderDAO orderDAO = DAOFacade.getDAO(Order.class);
        List<Order> orders = orderDAO.getOrdersForTickets(tickets);

        ticketDAO.delete(tickets);

        orderDAO.sanitizeOrders(orders);

        return tickets.size();
    }

    private SearchResult getGroupedByUser(List<Ticket> tickets) {
        SearchResult result = new SearchResult();

        for (Ticket ticket: tickets) {
            User user = ticket.getUser();
            result.addUser(user);
            result.addTicket(ticket, user);
        }
        return result;
    }

    private Date getDateTillThisDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        log.info("Current date " + calendar.getTime());

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        log.info("Date for previous day " + calendar.getTime());

        return calendar.getTime();
    }

    private class SearchResult {
        private Map<String, User> users = new HashMap<>();
        private Map<String, List<Ticket>> tickets = new HashMap<>();

        private void addUser(User user) {
            users.put(user.getEmail(), user);
        }

        private void addTicket(Ticket ticket, User user) {
            List<Ticket> ticketsForUser = tickets.get(user.getEmail());
            if (ticketsForUser == null) {
                ticketsForUser = new ArrayList<>();
                tickets.put(user.getEmail(), ticketsForUser);
            }
            ticketsForUser.add(ticket);
        }
    }
}
