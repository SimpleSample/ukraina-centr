package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.cache.BusCache;
import com.nagornyi.uc.cache.RouteCache;
import com.nagornyi.uc.common.DiscountCalculator;
import com.nagornyi.uc.common.RouteSearchResult;
import com.nagornyi.uc.common.price.DiscountHelper;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IPriceDAO;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import com.nagornyi.uc.util.CurrencyUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 14.05.14
 */
public class SearchTripsAction implements Action {
    Logger log = Logger.getLogger(SearchTripsAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        Locale loc = req.getLocale();
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM, yyyy kk:mm", loc);

        String tzOffset = req.getParam("tzOffset");
        String startCityId = req.getParam("startCityId");
        String endCityId = req.getParam("endCityId");
        String startDate = req.getParam("startDate");
        Calendar cStartDate = Calendar.getInstance();
        cStartDate.setTimeInMillis(Long.parseLong(startDate));
        cStartDate.set(Calendar.MILLISECOND, 0);
        String endDate = req.getParam("endDate");

        JSONObject result = new JSONObject();

        Price price = ((IPriceDAO)DAOFacade.getDAO(Price.class)).getPriceByCities(startCityId, endCityId);
        double resultingPrice = endDate == null? price.getPrice() : price.getPriceBoth();
        double discountedPrice = 0;
        if (req.isAuthorized() && req.getUser().getDiscount() != null) {
            discountedPrice = new DiscountCalculator().calculate(resultingPrice, req.getUser().getDiscount());
        }

        RouteSearchResult searchResult = getTrips(startCityId, endCityId, tzOffset, cStartDate);
        List<Trip> trips = searchResult.getTrips();

        Route route = searchResult.getRoute();
        City routeFirstCity = route.getFirstCity();
        City routeLastCity = route.getLastCity();
        String routeFirstCityStr = searchResult.isForth()? routeFirstCity.getLocalizedName(loc) : routeLastCity.getLocalizedName(loc);
        String routeLastCityStr = searchResult.isForth()? routeLastCity.getLocalizedName(loc) : routeFirstCity.getLocalizedName(loc);

        JSONArray tripsArray = new JSONArray();

        for (Trip trip: trips) {
			Date realStartDate = new Date(trip.getStartDate().getTime() + searchResult.getStartMilis());
			Date realEndDate = new Date(trip.getEndDate().getTime() - searchResult.getEndMilis());

			JSONObject tr = new JSONObject();
            tr.put("id", KeyFactory.keyToString(trip.getEntity().getKey()));
            tr.put("routeFirstCity", routeFirstCityStr);
            tr.put("routeEndCity", routeLastCityStr);
            tr.put("startDate", formatter.format(realStartDate));
            tr.put("rawStartDate",realStartDate.getTime());
            tr.put("endDate", formatter.format(realEndDate));
            tr.put("rawEndDate", realEndDate.getTime());
            tr.put("price", resultingPrice);
            if (discountedPrice != 0) tr.put("discPrice", CurrencyUtil.round(discountedPrice, -1));
            fillSeats(tr, searchAvailableSeats(KeyFactory.keyToString(route.getBusKey()), trip));
            tripsArray.put(tr);
        }
        result.put("forthTrips", tripsArray);
        if (endDate != null && !"".equals(endDate)) {
            Calendar cEndDate = Calendar.getInstance();
            cEndDate.setTimeInMillis(Long.parseLong(endDate));
            RouteSearchResult backSearchResult = getTrips(endCityId, startCityId, tzOffset, cEndDate);
            List<Trip> backTrips = backSearchResult.getTrips();
            JSONArray backTripsArray = new JSONArray();
            for (Trip trip: backTrips) {
				Date realStartDate = new Date(trip.getStartDate().getTime() + backSearchResult.getStartMilis());
				Date realEndDate = new Date(trip.getEndDate().getTime() - backSearchResult.getEndMilis());

                JSONObject tr = new JSONObject();
                tr.put("id", KeyFactory.keyToString(trip.getEntity().getKey()));
                tr.put("routeFirstCity", routeLastCityStr);
                tr.put("routeEndCity", routeFirstCityStr);
				tr.put("startDate", formatter.format(realStartDate));
				tr.put("rawStartDate",realStartDate.getTime());
				tr.put("endDate", formatter.format(realEndDate));
				tr.put("rawEndDate", realEndDate.getTime());
                tr.put("price", resultingPrice);
                if (discountedPrice != 0) tr.put("discPrice", discountedPrice);
                fillSeats(tr, searchAvailableSeats(KeyFactory.keyToString(route.getBusKey()), trip));
                backTripsArray.put(tr);
            }
            result.put("backTrips", backTripsArray);
        }
        result.put("discounts", DiscountHelper.getDiscountCategoriesAsJSON());
        resp.setDataObject(result);
    }

    private void fillSeats(JSONObject trip, List<Seat> seats) throws JSONException {
        JSONArray seatsArray = new JSONArray();
        for(Seat seat: seats) {
            seatsArray.put(seat.toJSON());
        }
        trip.put("seats", seatsArray);
    }

    private RouteSearchResult getTrips(String startCityId, String endCityId, String tzOffset, Calendar c) {
        RouteSearchResult result = RouteCache.getRoute(KeyFactory.stringToKey(startCityId), KeyFactory.stringToKey(endCityId));
        Calendar c2 = (Calendar)c.clone();
        c2.add(Calendar.DAY_OF_MONTH, 20); //TODO configure
        ITripDAO dao = DAOFacade.getDAO(Trip.class);
        List<Trip> trips = dao.getTripsByDateRange(result.getRoute(), c.getTime(), c2.getTime(), result.isForth());
        result.setTrips(trips);
        return result;
    }

    private List<Seat> searchAvailableSeats(String busId, Trip trip) {
        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
        List<Seat> unavSeats = dao.getUnavailableSeatsForTrip(trip);
        return BusCache.getFreeSeats(busId, unavSeats);
    }


}
