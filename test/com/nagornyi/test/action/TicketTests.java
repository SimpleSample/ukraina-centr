package com.nagornyi.test.action;

import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.cache.RouteCache;
import com.nagornyi.uc.common.RouteSearchResult;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ICityDAO;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.entity.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by Artem on 06.05.2015.
 * TODO tests are dependent on each other
 */
public class TicketTests {

    private static ITicketDAO ticketDAO;

    @BeforeClass
    public static void setUpTests() {
        System.out.println("TicketTests setup");

        ticketDAO = DAOFacade.getDAO(Ticket.class);

        String firstCtyStr = "Київ";
        String lasCityStr = "Рим";
        ICityDAO cityDao = DAOFacade.getDAO(City.class);
        City firstCity = cityDao.getByName(firstCtyStr);
        City lastCity = cityDao.getByName(lasCityStr);

        RouteSearchResult routeSearchResult = RouteCache.getRoute(firstCity.getKey(), lastCity.getKey());
        Route testRoute = routeSearchResult.getRoute();
        ITripDAO dao = DAOFacade.getDAO(Trip.class);

        Long startDate = 1428402000000L; //07.04.2015 10:20 (GMT)
        Long endDate = 1430324400000L; //29.04.2015 16:20 (GMT)
        dao.getOrCreateTripsByDateRange(testRoute, new Date(startDate), new Date(endDate), true);
    }

    @Test
    public void shouldDeleteTicketsForTrip() {
        List<Ticket> tickets = ticketDAO.getAll();
        assertTrue(tickets.size() > 0);

        String someTripKey = tickets.iterator().next().getTrip().getStringKey();
        ticketDAO.deleteTicketsForTrip(someTripKey);

        List<Ticket> ticketList = ticketDAO.getTicketsForTrip(someTripKey);

        assertThat("All tickets for trip should be deleted", ticketList.size(), is(equalTo(0)));
    }

    @Test
    public void daoTest() {
        IUserDAO dao = DAOFacade.getDAO(User.class);
        User admin = dao.getUserByEmail("info@ukraina-centr.com");

        Long tillDate = 1430475600000L; //01.05.2015 10:20:00
        List<Ticket> tickets = ticketDAO.getAllTicketsForUserTillDate(admin, new Date(tillDate));
        assertEquals(16, tickets.size());

        Set<Key> deletedKeys = ticketDAO.deleteAllTicketsForUserTillDate(admin, new Date(tillDate));
        assertEquals(16, deletedKeys.size());

        tickets = ticketDAO.getAllTicketsForUserTillDate(admin, new Date(tillDate));
        assertTrue(tickets.isEmpty());
    }

    @Test
    public void shouldFindThatSameExists() {
        List<Ticket> tickets = ticketDAO.getAll();
        assertTrue(tickets.size() > 0);

        Ticket sameTicket = tickets.iterator().next();
        Ticket newTicket = new Ticket(sameTicket.getTrip());
        newTicket.setSeat(sameTicket.getSeat());
        newTicket.setStatus(Ticket.Status.PROCESSING);

        boolean newTicketExists = ticketDAO.sameTicketExists(newTicket);
        assertTrue("Tickets are the same if seats are equal for the same trip", newTicketExists);

        sameTicket.setStatus(Ticket.Status.INVALID);
        DAOFacade.save(sameTicket);

        newTicketExists = ticketDAO.sameTicketExists(newTicket);
        assertFalse("Tickets are not the same if existing ticket has status invalid", newTicketExists);
    }

    @AfterClass
    public static void tearDownTests() {
        System.out.println("TicketTests tearDown");
        DAOFacade.getDAO(Trip.class).deleteAll();

        assertTrue(DAOFacade.getDAO(Trip.class).getAll().isEmpty());
    }
}
