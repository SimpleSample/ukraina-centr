package com.nagornyi.uc.converter;


import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.DiscountCategory;
import com.nagornyi.uc.entity.Seat;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.Trip;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.service.PriceService;
import com.nagornyi.uc.service.ServiceLocator;

import java.util.Date;

public final class TicketConverter {

    private TicketConverter() {

    }

    public static void populateForthTicketFromJson(Ticket ticket, JSONObject ticketJson) throws JSONException {
        populateTicketFromJson(ticket, ticketJson, true);
    }

    public static void populateBackTicketFromJson(Ticket ticket, JSONObject ticketJson) throws JSONException {
        populateTicketFromJson(ticket, ticketJson, false);
    }

    public static void populateTicketFromJson(Ticket ticket, JSONObject ticketJson, boolean isUserForth) throws JSONException {
        ticket.setPassenger(ticketJson.has("passenger") ? ticketJson.getString("passenger") : null);
        ticket.setPhone1(ticketJson.has("phone1") ? ticketJson.getString("phone1") : null);
        ticket.setPhone2(ticketJson.has("phone2") ? ticketJson.getString("phone2") : null);

        String startCityKey = ticketJson.has("startCity")? ticketJson.getString("startCity") : null;
        String endCityKey = ticketJson.has("endCity")? ticketJson.getString("endCity") : null;
        City startCity;
        City endCity;
        if (startCityKey != null && endCityKey != null) {
            startCity = DAOFacade.findByKey(City.class, KeyFactory.stringToKey(startCityKey));
            endCity = DAOFacade.findByKey(City.class, KeyFactory.stringToKey(endCityKey));
            ticket.setStartCity(isUserForth ? startCity : endCity);
            ticket.setEndCity(isUserForth ? endCity : startCity);
        }

        Long rawStartDate;
        if (isUserForth) {
            rawStartDate = ticketJson.has("rawStartDate")? ticketJson.getLong("rawStartDate") : null;
        } else {
            rawStartDate =  ticketJson.has("rawBackStartDate")? ticketJson.getLong("rawBackStartDate") : null;
        }
        if (rawStartDate != null) {
            ticket.setStartDate(new Date(rawStartDate));
        }

        String seatId = isUserForth? ticketJson.getString("seatId") : ticketJson.getString("backSeatId");
        if (seatId != null) {
            Seat seat = DAOFacade.findById(Seat.class, KeyFactory.stringToKey(seatId));
            ticket.setSeat(seat);
        }
        ticket.setNote(ticketJson.has("note")? ticketJson.getString("note") : null);
    }

    public static void populateEmptyFieldsFromTrip(Ticket ticket, Trip trip) {
        if (ticket.getStartCity() == null && ticket.getEndCity() == null) {
            ticket.setStartCity(trip.getStartCity());
            ticket.setEndCity(trip.getEndCity());
        }

        if (ticket.getStartDate() == null) {
            ticket.setStartDate(trip.getStartDate());
        }
    }

    public static void populatePrice(Ticket ticket, User user, JSONObject ticketJson) throws JSONException {
        DiscountCategory category = DiscountCategory.valueOf(ticketJson.getString("discountId"));
        PriceService priceService = ServiceLocator.getInstance().getPriceService();
        double price = priceService.getPrice(ticket.getStartCity().getStringKey(),
                ticket.getEndCity().getStringKey(),
                user,
                category,
                ticket.isPartial());
        ticket.setCalculatedPrice(price);
    }
}
