package com.nagornyi.uc;

import com.google.appengine.api.datastore.Key;
import com.google.apphosting.utils.servlet.SessionCleanupServlet;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.service.ServiceLocator;
import com.nagornyi.uc.service.TicketService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
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

        log.info("Start cleaning admin tickets");
        IUserDAO dao = DAOFacade.getDAO(User.class);

        String adminEmail = ServiceLocator.getInstance().getUserService().getAdminEmail();
        User admin = dao.getUserByEmail(adminEmail);

        ITicketDAO ticketDao = DAOFacade.getDAO(Ticket.class);
        Set<Key> deletedKeys = ticketDao.deleteAllTicketsForUserTillDate(admin, getDateThreeDaysAgo());

        log.info("Deleted " + deletedKeys.size() + " tickets");

        TicketService ticketService = ServiceLocator.getInstance().getTicketService();

        int deletedTickets = ticketService.deleteUnpaidTicketsForLastDay();

        log.info("Deleted unpaid tickets: " + deletedTickets);
    }

    private Date getDateThreeDaysAgo() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        c.add(Calendar.DAY_OF_MONTH, -3);

        return c.getTime();
    }
}
