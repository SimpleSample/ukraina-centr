package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.common.date.DateUtils;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ISeatDAO;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.Route;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.helper.TripConverter;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import static com.nagornyi.uc.action.ActionKeys.*;

@Authorized(role = Role.ADMIN)
public class GetTripsForPeriodAction implements Action {
    private static final Logger LOG = Logger.getLogger(GetTripsForPeriodAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String startDateMiliseconds = req.getParam(START_DATE_KEY);
        Calendar startDate = Calendar.getInstance();
        LOG.info("Current time: " + startDate.getTimeInMillis());
        if (startDateMiliseconds != null) {
            startDate.setTimeInMillis(Long.valueOf(startDateMiliseconds));
        }
        startDate.setTime(DateUtils.getUkraineDateFor(startDate.getTime()));
        String endDateMiliseconds = req.getParam(END_DATE_KEY);
        Calendar endDate = Calendar.getInstance();
        if (endDateMiliseconds != null) {
            endDate.setTimeInMillis(Long.valueOf(endDateMiliseconds));
        } else {
            endDate.add(Calendar.DAY_OF_MONTH, 90/*days*/); // todo configure
        }
        endDate.setTime(DateUtils.getUkraineDateFor(endDate.getTime()));
        LOG.info("Start date: " + startDate.getTimeInMillis() + ", end Date: " + endDate.getTimeInMillis());

        Route route = DAOFacade.findAll(Route.class).get(0);
        ITripDAO tripDAO = DAOFacade.getDAO(Trip.class);
        JSONArray tripArray = new JSONArray();
        List<Trip> trips = tripDAO.getTripsByDateRange(route, startDate.getTime(), endDate.getTime());
        // first trip with all tickets
        Trip firstTrip = trips.remove(0);

        JSONObject firstTripJson = TripConverter.convertTripWithTicketsExcludeAdmin(firstTrip);
        ISeatDAO dao = DAOFacade.getDAO(Seat.class);
        List<Seat> seats = dao.getSeats(route.getBus());
        JSONArray allSeats = new JSONArray();
        for (Seat seat: seats) {
            allSeats.put(seat.toJSON());
        }
        firstTripJson.put(ALL_SEATS_KEY, allSeats);
        tripArray.put(firstTripJson);


        for (Trip trip: trips) {
            tripArray.put(TripConverter.convertTrip(trip, true/*addNotAvailableCount*/));
        }
        JSONObject result = new JSONObject();
        result.put(TRIPS_KEY, tripArray);
        resp.setDataObject(result);
    }

}
