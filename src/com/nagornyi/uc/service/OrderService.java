package com.nagornyi.uc.service;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.PurchaseResultNew;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ITicketDAO;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.DiscountCategory;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.helper.TripConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by artemnagorny on 30.08.15.
 */
public class OrderService implements UCService {
    private static Logger log = Logger.getLogger(OrderService.class.getName());

    public Ticket getTicket(Trip trip, String ticketId) {
        Ticket result;
        if (ticketId == null || ticketId.startsWith("temp-")) {
            result = new Ticket(trip);
        } else {
            ITicketDAO dao = DAOFacade.getDAO(Ticket.class);
            result = dao.revealLockedTicket(trip.getStringKey(), ticketId);
            if (result == null) {
                result = dao.getByKey(KeyFactory.stringToKey(ticketId));
                if (result == null) {
                    log.warning("Could not find ticket " + ticketId + " in cache and DB");
                }
            }
        }
        return result;
    }

    public PurchaseResultNew purchaseTickets(Trip forthTrip, Trip backTrip, JSONObject tickets, User currentUser, Order order) throws JSONException {

        PurchaseResultNew result = new PurchaseResultNew();

        JSONArray ticketIds = tickets.names();
        for (int i = 0, size = ticketIds.length(); i < size; i++) {
            JSONObject ticketObj = tickets.getJSONObject(ticketIds.getString(i));
            purchaseTicket(result, ticketObj, currentUser, order, forthTrip, backTrip != null, true/*forth*/);

            if (backTrip != null) {
                purchaseTicket(result, ticketObj, currentUser, order, backTrip, true/*partial*/, false/*forth*/);
            }
        }
        return result;
    }

    public void purchaseTicket(PurchaseResultNew result, JSONObject ticketObj, User currentUser, Order order,
                                Trip trip, boolean isPartial, boolean isForth) throws JSONException {
        String ticketId = isForth?
                ticketObj.has("ticketId")? ticketObj.getString("ticketId") : null :
                ticketObj.has("backTicketId")? ticketObj.getString("backTicketId") : null;

        Ticket ticket = getTicket(trip, ticketId);
        if (ticket != null) {
            ticket.setUser(currentUser);
            ticket.setOrder(order);
            ticket.setPartial(isPartial);
            fillTicketFromJson(currentUser, trip, ticketObj, ticket, isForth);
            if (currentUser.isPartner() || currentUser.isAdmin()) {
                ticket.setStatus(Ticket.Status.RESERVED);
            } else {
                ticket.setStatus(Ticket.Status.PROCESSING);
            }

            result.addTicket(ticket);
        } else {
            String seatNumKey = isForth? "seatNum" : "backSeatNum";
            result.addFailedTicket(trip, ticketObj.getString(seatNumKey));
        }
    }

    public void fillTicketFromJson(User user, Trip trip, JSONObject ticketJson, Ticket ticket, boolean isForth) throws JSONException {
        ticket.setPassenger(ticketJson.getString("passenger"));
        ticket.setPhone1(ticketJson.has("phone1") ? ticketJson.getString("phone1") : null);
        ticket.setPhone2(ticketJson.has("phone2") ? ticketJson.getString("phone2") : null);

        String startCityKey = ticketJson.has("startCity")? ticketJson.getString("startCity") : null;
        String endCityKey = ticketJson.has("endCity")? ticketJson.getString("endCity") : null;
        City startCity;
        City endCity;
        if (startCityKey == null && endCityKey == null) {
            startCity = trip.getRoute().getFirstCity();
            endCity = trip.getRoute().getLastCity();
        } else {
            startCity = DAOFacade.findByKey(City.class, KeyFactory.stringToKey(startCityKey));
            endCity = DAOFacade.findByKey(City.class, KeyFactory.stringToKey(endCityKey));
        }
        ticket.setStartCity(isForth ? startCity : endCity);
        ticket.setEndCity(isForth ? endCity : startCity);

        Long rawStartDate;
        if (isForth) {
            rawStartDate = ticketJson.has("rawStartDate")? ticketJson.getLong("rawStartDate") : null;
        } else {
            rawStartDate =  ticketJson.has("rawBackStartDate")? ticketJson.getLong("rawBackStartDate") : null;
        }
        if (rawStartDate == null) {
            rawStartDate = trip.getStartDate().getTime();
        }
        ticket.setStartDate(new Date(rawStartDate));

        DiscountCategory category = ticketJson.has("discountId")? DiscountCategory.valueOf(ticketJson.getString("discountId")) : DiscountCategory.NONE;
        PriceService priceService = ServiceLocator.getInstance().getPriceService();
        double price = priceService.getPrice(ticket.getStartCity().getStringKey(),
                ticket.getEndCity().getStringKey(),
                user,
                category,
                ticket.isPartial());
        ticket.setCalculatedPrice(price);

        String seatId = isForth? ticketJson.getString("seatId") : ticketJson.getString("backSeatId");
        Seat seat = DAOFacade.findById(Seat.class, KeyFactory.stringToKey(seatId));
        ticket.setSeat(seat);
        ticket.setNote(ticketJson.has("note")? ticketJson.getString("note") : null);
    }

    public String createFailedTicketsMessage(PurchaseResultNew result) {
        String message = "<pre>Не вдалось зберегти наступні місця:<br>";
        Iterator<String> keys = result.getFailedTripKeys();
        while (keys.hasNext()) {
            String tripKey = keys.next();
            Trip trip = result.getTrip(tripKey);
            message += "&nbsp;" + TripConverter.getTripData(trip) + "<br>";

            List<String> failedSeats = result.getFailedTicketsForTrip(tripKey);
            message += "&#9;" + StringUtils.join(failedSeats, ", ") + "<br>";
        }
        message += "</pre>";
        return message;
    }
}
