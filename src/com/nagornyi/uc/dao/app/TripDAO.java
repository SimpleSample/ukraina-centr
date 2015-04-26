package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.nagornyi.uc.common.DateFormatter;
import com.nagornyi.uc.dao.*;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.util.DateUtil;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 18.05.14
 */
public class TripDAO extends EntityDAO<Trip> implements ITripDAO {
    private static Logger log = Logger.getLogger(TripDAO.class.getName());

    @Override
    protected Trip createDAOEntity(Entity entity) {
        return new Trip(entity);
    }

    @Override
    protected String getKind() {
        return "Trip";
    }

    @Override
    public List<Trip> getOtherTrips (Trip trip) {
        return get(trip.getParentKey(), new Query.FilterPredicate("isForth",
                Query.FilterOperator.EQUAL,
                trip.isForth()), null, null);
    }

    @Override
    public List<Trip> getTripsForTwoMonths(Route route, Calendar startDate, boolean isForth) {
        Calendar c2 = (Calendar)startDate.clone();
        c2.add(Calendar.DAY_OF_MONTH, 60);
        return getTripsByDateRange(route, startDate.getTime(), c2.getTime(), isForth);
    }

    @Override
    public synchronized List<Trip> getTripsByDateRange(Route route, Date startDate, Date endDate, boolean isForth) {
        log.info("Searching trips for date range [" + DateFormatter.defaultFormat(startDate) + " - " + DateFormatter.defaultFormat(endDate)+"]");
        List<Trip> trips = new ArrayList<Trip>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int days = DateUtil.getDatesDelta(start, end);
        log.info("\tDays period - " + days);
        int weeks = days/7;
        Calendar iterEndDate = (Calendar)start.clone();
        iterEndDate.add(Calendar.DAY_OF_MONTH, 7);
        while (weeks != 0) {
            log.info("searching for week [" + DateFormatter.defaultFormat(start.getTime()) + " - " + DateFormatter.defaultFormat(iterEndDate.getTime())+"]");

            Date routeStartDate = isForth? route.getForthStartDate() : route.getBackStartDate();
            Date routeEndDate = isForth? route.getForthEndDate() : route.getBackEndDate();
            DateUtil.DatePeriod period = DateUtil.getActualDatePeriodForRoute(start.getTime(), new DateUtil.DatePeriod(routeStartDate, routeEndDate));
            log.info("Calculated dates: " + DateFormatter.defaultFormat(period.getStartDate()) + " - " + DateFormatter.defaultFormat(period.getEndDate()));
            Trip trip = getTripByDate(period.getStartDate(), period.getEndDate());
            if (trip == null) {
                log.info("Nothing was found");
                Key key = createTrip(route, period.getStartDate(), period.getEndDate(), isForth);
                Trip t = getByKey(key);
                trips.add(t);
            } else {
                trips.add(trip);
            }

            start.add(Calendar.DAY_OF_MONTH, 7);
            iterEndDate.add(Calendar.DAY_OF_MONTH, 7);
            weeks--;
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsByDateRange(Route route, Date startDate, Date endDate) {

        List<Trip> trips = new ArrayList<Trip>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int days = DateUtil.getDatesDelta(start, end);
        int weeks = days/7;
        Calendar iterEndDate = (Calendar)start.clone();
        iterEndDate.add(Calendar.DAY_OF_MONTH, 7);
        while (weeks != 0) {
            Query.Filter startDateMinFilter =
                    new Query.FilterPredicate("startDate",
                            Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                            start.getTime());

            Query.Filter startDateMaxFilter =
                    new Query.FilterPredicate("startDate",
                            Query.FilterOperator.LESS_THAN_OR_EQUAL,
                            iterEndDate.getTime());

            Query.Filter startDateRangeFilter =
                    Query.CompositeFilterOperator.and(startDateMinFilter, startDateMaxFilter);


            List<Trip> weekTrips = get(route.getKey(), startDateRangeFilter, "startDate", Query.SortDirection.ASCENDING);

            trips.addAll(weekTrips);

            start.add(Calendar.DAY_OF_MONTH, 7);
            iterEndDate.add(Calendar.DAY_OF_MONTH, 7);
            weeks--;
        }
        return trips;
    }

    public synchronized Trip getTripByDate(Date startDate, Date endDate) {
        Query.Filter startDateMinFilter =
                new Query.FilterPredicate("startDate",
                        Query.FilterOperator.EQUAL,
                        startDate);

        Query.Filter startDateMaxFilter =
                new Query.FilterPredicate("endDate",
                        Query.FilterOperator.EQUAL,
                        endDate);

        Query.Filter startDateRangeFilter =
                Query.CompositeFilterOperator.and(startDateMinFilter, startDateMaxFilter);
        List<Trip> trips = getByFilter(startDateRangeFilter);
        return trips.isEmpty()? null : trips.get(0);
    }

    @Override
    public Key createTrip(Route route, Date tripStartDate, Date tripEndDate, boolean isForth) {
        log.info("Creating " + (isForth? "forth" : "back") +" trip for route " + route.getRouteName() +
                ", date range: "+DateFormatter.defaultFormat(tripStartDate) + " - " + DateFormatter.defaultFormat(tripEndDate));
        Trip trip = new Trip(route, tripStartDate, tripEndDate, route.getBus().getSeatsNum(), isForth);
        Key saved = save(trip);
        reserveBlocked(route.getBus(), trip);
        return saved;
    }

    private void reserveBlocked(Bus bus, Trip trip) {
        log.info("Reserving initially blocked seats");
        List<Seat> seats = ((ISeatDAO) DAOFacade.getDAO(Seat.class)).getSeats(bus);
        User admin = ((IUserDAO)DAOFacade.getDAO(User.class)).getUserByEmail("info@ukraina-centr.com");
        City routeStartCity = trip.getRoute().getFirstCity();
        City routeEndCity = trip.getRoute().getLastCity();
        String startCityId = trip.isForth()? routeStartCity.getStringKey() : routeEndCity.getStringKey();
        String endCityId = trip.isForth()? routeEndCity.getStringKey() : routeStartCity.getStringKey();

        for (Seat seat: seats) {
            if (seat.isInitiallyBlocked()) {
                log.info("Blocking seat " + seat.getSeatNum());
                Ticket t = ((ITicketDAO)DAOFacade.getDAO(Ticket.class)).createReservedTicket(null, trip, seat,
                        admin.getUsername(), null, null, admin, startCityId, endCityId, trip.getStartDate(), true, null, null);
                t.setStatus(Ticket.Status.RESERVED);
                DAOFacade.save(t);
            }
        }
    }

    private TimeZone getTimeZone(String tzOffset) {
        int timeZone = Integer.parseInt(tzOffset);
        if (timeZone >= 0) {
            tzOffset = "+" + timeZone;
        }

        return TimeZone.getTimeZone("GMT" + tzOffset);
    }
}
