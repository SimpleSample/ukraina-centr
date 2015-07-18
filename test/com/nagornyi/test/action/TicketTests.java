package com.nagornyi.test.action;

import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.cache.RouteCache;
import com.nagornyi.uc.common.RouteSearchResult;
import com.nagornyi.uc.dao.*;
import com.nagornyi.uc.entity.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Artem on 06.05.2015.
 */
public class TicketTests {

    @BeforeClass
    public static void setUpTests() {
        System.out.println("TicketTests setup");

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
    public void daoTest() {
        IUserDAO dao = DAOFacade.getDAO(User.class);
        User admin = dao.getUserByEmail("info@ukraina-centr.com");

        ITicketDAO ticketDao = DAOFacade.getDAO(Ticket.class);

        Long tillDate = 1430475600000L; //01.05.2015 10:20:00
        List<Ticket> tickets = ticketDao.getTicketsForUserByPeriod(admin, new Date(tillDate));
        assertEquals(24, tickets.size());

        Set<Key> deletedKeys = ticketDao.deleteTicketsForUserByPeriod(admin, new Date(tillDate));
        assertEquals(24, deletedKeys.size());

        tickets = ticketDao.getTicketsForUserByPeriod(admin, new Date(tillDate));
        assertTrue(tickets.isEmpty());
    }

    @AfterClass
    public static void tearDownTests() {
        System.out.println("TicketTests tearDown");
        DAOFacade.getDAO(Trip.class).deleteAll();

        assertTrue(DAOFacade.getDAO(Trip.class).getAll().isEmpty());
    }
}
