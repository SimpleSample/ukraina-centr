package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.dao.*;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.service.ServiceLocator;
import com.nagornyi.uc.util.DateUtil;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 18.05.14
 */
public class TripDAO extends EntityDAO<Trip> implements ITripDAO {
    private static final Logger log = Logger.getLogger(TripDAO.class.getName());

    @Override
    protected Trip createDAOEntity(Entity entity) {
        return new Trip(entity);
    }

    @Override
    protected String getKind() {
        return "Trip";
    }

    @Override
    public List<Trip> getOtherTrips(Trip trip) {
        return get(trip.getParentKey(), new Query.FilterPredicate("forth",
                Query.FilterOperator.EQUAL,
                trip.isRouteForth()), null, null);
    }

    @Override
    public List<Trip> getOrCreateTripsForTwoMonths(Route route, Calendar startDate, boolean isForth) {
        Calendar c2 = (Calendar)startDate.clone();
        c2.add(Calendar.DAY_OF_MONTH, 60);
        return getOrCreateTripsByDateRange(route, startDate.getTime(), c2.getTime(), isForth);
    }

    @Override
    public synchronized List<Trip> getOrCreateTripsByDateRange(Route route, Date startDate, Date endDate, boolean isForth) {
        log.info("Searching trips for date range [" + DateFormatter.defaultFormat(startDate) + " - " + DateFormatter.defaultFormat(endDate)+"]");
        List<Trip> trips = new ArrayList<Trip>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int days = DateUtil.getDaysDelta(start, end);
        log.info("\tDays period - " + days);
        int weeks = days/7;
        Calendar iterEndDate = (Calendar)start.clone();
        iterEndDate.add(Calendar.DAY_OF_MONTH, 7);
        while (weeks != 0) {
            log.info("searching for week [" + DateFormatter.defaultFormat(start.getTime()) + " - " + DateFormatter.defaultFormat(iterEndDate.getTime())+"]");

            trips.add(getOrCreateTrip(route, start.getTime(), isForth));

            start.add(Calendar.DAY_OF_MONTH, 7);
            iterEndDate.add(Calendar.DAY_OF_MONTH, 7);
            weeks--;
        }
        return trips;
    }

    @Override
    public Trip getOrCreateTrip(Route route, Date startDate, boolean isForth) {
        Date routeStartDate = isForth? route.getForthStartDate() : route.getBackStartDate();
        Date routeEndDate = isForth? route.getForthEndDate() : route.getBackEndDate();
        DateUtil.DatePeriod period = DateUtil.getActualDatePeriodForRoute(startDate, new DateUtil.DatePeriod(routeStartDate, routeEndDate));
        log.info("Calculated dates: " + DateFormatter.defaultFormat(period.getStartDate()) + " - " + DateFormatter.defaultFormat(period.getEndDate()));

        Trip trip = getTripByDate(period.getStartDate(), period.getEndDate());
        if (trip == null) {
            log.info("Nothing was found, creating...");
            Key key = createTrip(route, period.getStartDate(), period.getEndDate(), isForth);
            return getByKey(key);
        } else {
            return trip;
        }
    }

    @Override
    public List<Trip> getTripsByDateRange(Route route, Date startDate, Date endDate) {

            Query.Filter startDateMinFilter =
                    new Query.FilterPredicate("startDate",
                            Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                            startDate);

            Query.Filter startDateMaxFilter =
                    new Query.FilterPredicate("startDate",
                            Query.FilterOperator.LESS_THAN_OR_EQUAL,
                            endDate);

            Query.Filter startDateRangeFilter =
                    Query.CompositeFilterOperator.and(startDateMinFilter, startDateMaxFilter);

            return get(route.getKey(), startDateRangeFilter, "startDate", Query.SortDirection.ASCENDING);
    }

    @Override
    public List<Trip> getTripsByDateRange(Route route, Date startDate, Date endDate, boolean isForth) {

        Query.Filter startDateMinFilter =
                new Query.FilterPredicate("startDate",
                        Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                        startDate);

        Query.Filter startDateMaxFilter =
                new Query.FilterPredicate("startDate",
                        Query.FilterOperator.LESS_THAN_OR_EQUAL,
                        endDate);

        Query.Filter forthFilter =
                new Query.FilterPredicate("forth",
                        Query.FilterOperator.EQUAL,
                        isForth);

        Query.Filter startDateRangeFilter =
                Query.CompositeFilterOperator.and(forthFilter, startDateMinFilter, startDateMaxFilter);

        return get(route.getKey(), startDateRangeFilter, "startDate", Query.SortDirection.ASCENDING);
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
        log.info("Creating " + (isForth ? "forth" : "back") + " trip for route " + route.getRouteName() +
                ", date range: " + DateFormatter.defaultFormat(tripStartDate) + " - " + DateFormatter.defaultFormat(tripEndDate));
        Trip trip = new Trip(route, tripStartDate, tripEndDate, route.getBus().getSeatsNum(), isForth);
        Key saved = save(trip);
        reserveBlocked(route.getBus(), trip);
        return saved;
    }

    /**
     * reserving first two rows of seats for administrator
     * @param bus bus
     * @param trip trip
     */
    private void reserveBlocked(Bus bus, Trip trip) {
        log.info("Reserving initially blocked seats");
        List<Seat> seats = ((ISeatDAO) DAOFacade.getDAO(Seat.class)).getSeats(bus);
        String adminEmail = ServiceLocator.getInstance().getUserService().getAdminEmail();
        User admin = ((IUserDAO)DAOFacade.getDAO(User.class)).getUserByEmail(adminEmail);
        String startCityId = trip.getStartCity().getStringKey();
        String endCityId = trip.getEndCity().getStringKey();

        for (Seat seat: seats) {
            if (seat.isInitiallyBlocked()) {
                log.info("Blocking seat " + seat.getSeatNum());
                Ticket t = ((ITicketDAO)DAOFacade.getDAO(Ticket.class)).createReservedTicket(null, trip, seat,
                        admin.getUsername(), null, null, admin, startCityId, endCityId, trip.getStartDate(), true, null, null, null);
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
