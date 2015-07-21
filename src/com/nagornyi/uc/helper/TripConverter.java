package com.nagornyi.uc.helper;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IPriceDAO;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.dao.app.TicketDAO;
import com.nagornyi.uc.entity.Price;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;

import java.util.Iterator;
import java.util.List;

import static com.nagornyi.uc.action.ActionKeys.ALL_COUNT_KEY;
import static com.nagornyi.uc.action.ActionKeys.ID_KEY;
import static com.nagornyi.uc.action.ActionKeys.PASS_COUNT_KEY;
import static com.nagornyi.uc.action.ActionKeys.ROUTE_FORTH_KEY;
import static com.nagornyi.uc.action.ActionKeys.START_DATE_KEY;
import static com.nagornyi.uc.action.ActionKeys.STRING_DATA_KEY;
import static com.nagornyi.uc.action.ActionKeys.TICKETS_KEY;

/**
 * Created by artemnagorny on 14.08.15.
 */
public final class TripConverter {
    private TripConverter() {

    }

    public static JSONObject convertTripWithTicketsExcludeAdmin(Trip trip) throws JSONException {
        JSONObject tripObj = convertTrip(trip, false);

        ITicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);

        List<Ticket> tickets = ticketDAO.getTicketsForTrip(trip);
        tripObj.put(PASS_COUNT_KEY, tickets.size());
        Iterator<Ticket> iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket nextTicket = iterator.next();
            if ("info@ukraina-centr.com".equals(nextTicket.getUser().getEmail())) {
                iterator.remove();
            }
        }
        addTicketsToTripJson(tripObj, tickets);
        return tripObj;
    }

    public static JSONObject convertTripWithTickets(Trip trip) throws JSONException {
        JSONObject tripObj = convertTrip(trip, false);

        ITicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);

        List<Ticket> tickets = ticketDAO.getTicketsForTrip(trip);
        tripObj.put(PASS_COUNT_KEY, tickets.size());
        addTicketsToTripJson(tripObj, tickets);
        return tripObj;
    }

    public static void addTicketsToTripJson(JSONObject tripJson, List<Ticket> tickets) throws JSONException {
        JSONArray ticketArray = new JSONArray();
        for (Ticket ticket: tickets) {
            ticketArray.put(ticket.toJson(DateFormatter.UK_LOCALE));
        }
        tripJson.put(TICKETS_KEY, ticketArray);
    }

    public static JSONObject convertTrip(Trip trip, boolean addNotAvailableCount) throws JSONException {
        JSONObject tripObj = new JSONObject();

        tripObj.put(ID_KEY, trip.getStringKey());
        tripObj.put(ALL_COUNT_KEY, trip.getSeatsNum());
        tripObj.put(STRING_DATA_KEY, getTripData(trip));
        tripObj.put(START_DATE_KEY, trip.getStartDate().getTime());
        tripObj.put(ROUTE_FORTH_KEY, trip.isRouteForth());

        if (addNotAvailableCount) {
            TicketDAO ticketDAO = DAOFacade.getDAO(Ticket.class);
            int notAvailableTicketCount = ticketDAO.countReservedTicketsForTrip(trip);
            tripObj.put(PASS_COUNT_KEY, notAvailableTicketCount);
        }

        IPriceDAO prices = DAOFacade.getDAO(Price.class);
        Price price = prices.getPriceByCities(trip.getStartCity().getStringKey(), trip.getEndCity().getStringKey());
        if (price != null) tripObj.put("price", price.getPrice());

        return tripObj;
    }

    public static String getTripData(Trip trip) {
        return trip.getTripName() + ", " + DateFormatter.defaultFormat(trip.getStartDate());
    }
}
