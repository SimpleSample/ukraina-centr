package com.nagornyi.uc;

import com.google.apphosting.utils.servlet.SessionCleanupServlet;
import com.nagornyi.uc.service.ServiceLocator;
import com.nagornyi.uc.service.TicketService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

/**
 * Created by Artem on 06.05.2015.
 */
public class CleanupServlet extends SessionCleanupServlet {
    private static Logger log = Logger.getLogger(CleanupServlet.class.getName());

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) {
        log.info("Start cleaning sessions");
        super.service(request, response);

        log.info("Start cleaning unpaid tickets");
        TicketService ticketService = ServiceLocator.getInstance().getTicketService();
        int deletedTickets = ticketService.deleteUnpaidTicketsForLastDay();

        log.info("Deleted unpaid tickets: " + deletedTickets);
    }
}
