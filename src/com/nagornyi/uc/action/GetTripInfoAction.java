package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Nagornyi
 * Date: 6/6/14
 */
public class GetTripInfoAction implements Action {

	@Override
	public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
//		String periodStr = req.getParam("period"); //TODO temp
		String periodStr = "90"; //days
//		String routeId = req.getParam("routeId");//TODO temp
		String routeId = "ag9zfnVrcmFpbmEtY2VudHJyEgsSBVJvdXRlGICAgICr84cKDA";
		Route route = DAOFacade.findById(Route.class, KeyFactory.stringToKey(routeId));
		int period = Integer.parseInt(periodStr);
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, period);
		ITripDAO dao = DAOFacade.getDAO(Trip.class);
		List<Trip> trips = dao.getTripsByDateRange(route, start.getTime(), end.getTime(), true);
		trips.addAll(dao.getTripsByDateRange(route, start.getTime(), end.getTime(), false));

		for (Trip trip: trips) {

		}
	}

	private JSONObject toJSON(Route route, Trip trip, Locale locale) throws JSONException {
		JSONObject tripObj = new JSONObject();
		City startCity = trip.isForth()?  route.getFirstCity() : route.getLastCity();
		City endCity = trip.isForth()?  route.getLastCity() : route.getFirstCity();

		tripObj.put("startCity", startCity.getLocalizedName(locale));
		tripObj.put("endCity", endCity.getLocalizedName(locale));
        return tripObj;
	}
}
