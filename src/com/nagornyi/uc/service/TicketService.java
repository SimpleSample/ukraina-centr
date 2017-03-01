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
import java.util.Iterator;
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

        filterTickets(groupedByUser);

        for (Map.Entry<String, List<Ticket>> ticketEntry: groupedByUser.tickets.entrySet()) {
            MailFacade.sendTimedOutTicketsToBeRemoved(groupedByUser.users.get(ticketEntry.getKey()), ticketEntry.getValue());
        }

        IOrderDAO orderDAO = DAOFacade.getDAO(Order.class);
        List<Order> orders = orderDAO.getOrdersForTickets(tickets);

        ticketDAO.delete(tickets);

        orderDAO.sanitizeOrders(orders);

        return tickets.size();
    }

    // TODO delete after a problem with removed tickets for admin will be resolved
    private void filterTickets(SearchResult groupedByUser) {
        for (Map.Entry<String, List<Ticket>> entry: groupedByUser.tickets.entrySet()) {
            List<Ticket> tickets = entry.getValue();
            User currentUser = groupedByUser.users.get(entry.getKey());
            Iterator<Ticket> iterator = tickets.iterator();
            while (iterator.hasNext()){
                Ticket ticket = iterator.next();
                if (ticket.getStatus().idx != Ticket.Status.PROCESSING.idx) {
                    log.warning("was about to delete ticket for user " + currentUser.getEmail() + ", role " + currentUser.getRole() + " with status " + ticket.getStatus() + ", start date " + ticket.getStartDate() + ", passenger " + ticket.getPassenger());
                    iterator.remove();
                }

                if (currentUser.getEmail().equals(ServiceLocator.getInstance().getUserService().getAdminEmail()) || "Admin".equals(currentUser.getName())) {
                    log.warning("was about to delete ticket for admin " + currentUser.getEmail()  + ", start date " + ticket.getStartDate() + ", passenger " + ticket.getPassenger());
                    iterator.remove();
                }
            }
        }

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
