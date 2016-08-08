package com.nagornyi.test.action;

import com.nagornyi.uc.cache.RouteCache;
import com.nagornyi.uc.common.RouteSearchResult;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ICityDAO;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Artem on 06.05.2015.
 */
public class TripTests {

    private static Route testRoute;

    @BeforeClass
    public static void setUpTests() {
        System.out.println("TripTests setup");

        String firstCtyStr = "Київ";
        String lasCityStr = "Рим";
        ICityDAO cityDao = DAOFacade.getDAO(City.class);
        City firstCity = cityDao.getByName(firstCtyStr);
        City lastCity = cityDao.getByName(lasCityStr);

        RouteSearchResult routeSearchResult = RouteCache.getRoute(firstCity.getKey(), lastCity.getKey());
        testRoute = routeSearchResult.getRoute();
    }

    @Test
    public void daoTest() {
        ITripDAO dao = DAOFacade.getDAO(Trip.class);
        List<Trip> trips = dao.getTripsByDateRange(testRoute, new Date(1L), new Date());
        assertTrue(trips.isEmpty());

        Long startDate = 1430994000000L; //07.05.2015 10:20 (GMT)
        Long endDate = 1432916400000L; //29.05.2015 16:20 (GMT)
        trips = dao.getOrCreateTripsByDateRange(testRoute, new Date(startDate), new Date(endDate), true);

        assertEquals(3, trips.size());

        Trip firstTrip = trips.get(0);
        assertEquals(1430998200000L, firstTrip.getStartDate().getTime());
        assertEquals(1431144000000L, firstTrip.getEndDate().getTime());

        Trip lastTrip = trips.get(2);
        assertEquals(1432207800000L, lastTrip.getStartDate().getTime());
        assertEquals(1432353600000L, lastTrip.getEndDate().getTime());

        // check if 8 seats were blocked
    }

    @AfterClass
    public static void tearDownTests() {
        System.out.println("TripTests tearDown");
        DAOFacade.getDAO(Trip.class).deleteAll();

        assertTrue(DAOFacade.getDAO(Trip.class).getAll().isEmpty());
        assertTrue(DAOFacade.getDAO(Ticket.class).getAll().isEmpty());
    }
}
