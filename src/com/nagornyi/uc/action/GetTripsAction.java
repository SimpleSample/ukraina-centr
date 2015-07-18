package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.cache.BusCache;
import com.nagornyi.uc.common.DateFormatter;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.ITripDAO;
import com.nagornyi.uc.entity.*;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Nagornyi
 * Date: 03.07.14
 */
public class GetTripsAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String ticketId = req.getParam("ticketId");
        Ticket ticket = DAOFacade.findById(Ticket.class, KeyFactory.stringToKey(ticketId));
        if (ticket.getStartDate().getTime() < new Date().getTime()) {
            throw new UserFriendlyException("Даний квиток є простроченим. Оберіть, будь ласка, активний квиток");
        }
        Trip currentTrip = ticket.getTrip();
        User user = req.getUser();
        Route route = currentTrip.getRoute();
        String routeFirstCity = route.getFirstCity().getLocalizedName(user.getUserLocale());
        String routeLastCity = route.getLastCity().getLocalizedName(user.getUserLocale());

        ITripDAO dao = DAOFacade.getDAO(Trip.class);

        List<Trip> trips = dao.getOrCreateTripsForTwoMonths(route, Calendar.getInstance(), currentTrip.isForth());
        JSONArray tripsArr = new JSONArray();
        for (Trip trip: trips) {
            JSONObject trp = new JSONObject();
            trp.put("id", trip.getStringKey());
            trp.put("startCity", trip.isForth() ? routeFirstCity : routeLastCity);
            trp.put("endCity", trip.isForth() ? routeLastCity : routeFirstCity);
            trp.put("allSeatsCount", trip.getSeatsNum());
            List<Ticket> tickets = DAOFacade.findByParent(Ticket.class, trip.getEntity().getKey());
            trp.put("reservedSeatsCount", tickets.size());
            trp.put("startDate", DateFormatter.format(trip.getStartDate(), user.getUserLocale()));
            fillSeats(trp, searchAvailableSeats(trip.getRoute().getBus().getStringKey(), trip));
            tripsArr.put(trp);
        }
        JSONObject respObj = new JSONObject();
        respObj.put("trips", tripsArr);
        resp.setDataObject(respObj);
    }

    private void fillSeats(JSONObject trip, List<Seat> seats) throws JSONException {
        JSONArray seatsArray = new JSONArray();
        for(Seat seat: seats) {
            seatsArray.put(seat.toJSON());
        }
        trip.put("seats", seatsArray);
    }

    private List<Seat> searchAvailableSeats(String busId, Trip trip) {
        ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
        List<Seat> unavSeats = dao.getUnavailableSeatsForTrip(trip);
        return BusCache.getFreeSeats(busId, unavSeats);
    }
}
