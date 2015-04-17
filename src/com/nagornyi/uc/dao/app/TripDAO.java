package com.nagornyi.uc.dao.app;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.nagornyi.uc.dao.*;
import com.nagornyi.uc.entity.*;

import java.util.*;

/**
 * @author Nagorny
 *         Date: 18.05.14
 */
public class TripDAO extends EntityDAO<Trip> implements ITripDAO {

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

        List<Trip> trips = new ArrayList<Trip>();
        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int days = (end.get(Calendar.MONTH) - start.get(Calendar.MONTH))*30 + (end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH));
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

            Query.Filter isForthFilter =
                    new Query.FilterPredicate("isForth",
                            Query.FilterOperator.EQUAL,
                            isForth);


            Query.Filter fullFilter = Query.CompositeFilterOperator.and(startDateRangeFilter, isForthFilter);

            List<Trip> weekTrips = get(route.getKey(), fullFilter, "startDate", Query.SortDirection.ASCENDING);
            if (weekTrips.isEmpty()) {
                Key key = createTrip(route, start.getTime(), isForth);
                Trip t = getByKey(key);
                trips.add(t);
            } else {
                trips.addAll(weekTrips);
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

        int days = (end.get(Calendar.MONTH) - start.get(Calendar.MONTH))*30 + (end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH));
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
    public Key createTrip(Route route, Date targetDate, boolean isForth) {

        Calendar cTargetDate = Calendar.getInstance();
        cTargetDate.setTime(targetDate);
        cTargetDate.set(Calendar.MILLISECOND, 0);

        Date startDate = isForth? route.getForthStartDate() : route.getBackStartDate();
        Date endDate = isForth? route.getForthEndDate() : route.getBackEndDate();

        Calendar cStartDate = Calendar.getInstance();
        cStartDate.set(Calendar.MILLISECOND, 0);
        cStartDate.setTime(startDate);
        Calendar cEndDate = Calendar.getInstance();
        cEndDate.setTime(endDate);
        cEndDate.set(Calendar.MILLISECOND, 0);

        cTargetDate.set(Calendar.HOUR_OF_DAY, cStartDate.get(Calendar.HOUR_OF_DAY));
        cTargetDate.set(Calendar.MINUTE, cStartDate.get(Calendar.MINUTE));
        cTargetDate.set(Calendar.SECOND, cStartDate.get(Calendar.SECOND));

        int leftDays;
        if (cTargetDate.get(Calendar.DAY_OF_WEEK) > cStartDate.get(Calendar.DAY_OF_WEEK)) {
            leftDays = 7 - (cTargetDate.get(Calendar.DAY_OF_WEEK) -  cStartDate.get(Calendar.DAY_OF_WEEK));
        } else {
            leftDays = cStartDate.get(Calendar.DAY_OF_WEEK) - cTargetDate.get(Calendar.DAY_OF_WEEK);
        }
        cTargetDate.add(Calendar.DAY_OF_MONTH, leftDays);


        int tripDurationHours = (cEndDate.get(Calendar.DAY_OF_MONTH) - cStartDate.get(Calendar.DAY_OF_MONTH))*24 +
                (cEndDate.get(Calendar.HOUR_OF_DAY) - cStartDate.get(Calendar.HOUR_OF_DAY));
        int tripDurationMinutes = cEndDate.get(Calendar.MINUTE) - cStartDate.get(Calendar.MINUTE);
        if (tripDurationMinutes < 0) {
            tripDurationHours--;
            tripDurationMinutes = 60 + tripDurationMinutes;
        }

        Calendar cTargetEndDate = (Calendar)cTargetDate.clone();

        cTargetEndDate.add(Calendar.HOUR_OF_DAY, tripDurationHours);
        cTargetEndDate.add(Calendar.MINUTE, tripDurationMinutes);
        Date resultStart = cTargetDate.getTime();
        Date resultEnd = cTargetEndDate.getTime();
        int seatsNum = route.getBus().getSeatsNum();
        Trip trip = new Trip(route, resultStart, resultEnd, seatsNum, isForth);
        Trip existing = getTripByDate(resultStart, resultEnd); //recheck
        if (existing != null) {
            return existing.getEntity().getKey();
        }
        Key saved = save(trip);
        reserveBlocked(route.getBus(), trip);
        return saved;
    }

    private void reserveBlocked(Bus bus, Trip trip) {
        List<Seat> seats = ((ISeatDAO) DAOFacade.getDAO(Seat.class)).getSeats(bus);
        User admin = ((IUserDAO)DAOFacade.getDAO(User.class)).getUserByEmail("info@ukraina-centr.com");
        City routeStartCity = trip.getRoute().getFirstCity();
        City routeEndCity = trip.getRoute().getLastCity();
        String startCityId = trip.isForth()? routeStartCity.getStringKey() : routeEndCity.getStringKey();
        String endCityId = trip.isForth()? routeEndCity.getStringKey() : routeStartCity.getStringKey();

        for (Seat seat: seats) {
            if (seat.isInitiallyBlocked()) {
                Ticket t = ((ITicketDAO)DAOFacade.getDAO(Ticket.class)).createReservedTicket(null, trip, seat,
                        admin.getName() + " " + admin.getSurname(), null, null, admin, startCityId, endCityId, trip.getStartDate(), true, null, null);
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
