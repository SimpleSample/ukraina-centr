package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.nagornyi.uc.cache.RouteCache;
import com.nagornyi.uc.common.RouteSearchResult;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.Trip;

import java.util.Calendar;
import java.util.Collections;

/**
 * Created by Artem on 24.06.2015.
 */
public class SearchTripsForWeekdaysAction extends SearchTripsAction {

    @Override
    protected RouteSearchResult getTrips(String startCityId, String endCityId, String tzOffset, Calendar c) {
        RouteSearchResult result = RouteCache.getRoute(KeyFactory.stringToKey(startCityId), KeyFactory.stringToKey(endCityId));
        if (result == null) return null; // no route found

        ITripDAO dao = DAOFacade.getDAO(Trip.class);
        Trip trip = dao.getOrCreateTrip(result.getRoute(), c.getTime(), result.isForth());
        result.setTrips(Collections.singletonList(trip));
        return result;
    }
}
