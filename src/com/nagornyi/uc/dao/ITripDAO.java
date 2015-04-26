package com.nagornyi.uc.dao;

import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Trip;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Nagorny
 * Date: 18.05.14
 */
public interface ITripDAO extends DAO<Trip> {

    List<Trip> getOtherTrips(Trip trip);

    List<Trip> getTripsForTwoMonths(Route route, Calendar startDate, boolean isForth);

    List<Trip> getTripsByDateRange(Route route, Date startDate, Date endDate, boolean isForth);

    List<Trip> getTripsByDateRange(Route route, Date startDate, Date endDate);

    Key createTrip(Route route, Date tripStartDate, Date tripEndDate, boolean isForth);
}
