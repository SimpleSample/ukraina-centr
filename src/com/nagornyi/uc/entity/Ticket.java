package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.DateFormatter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IUserDAO;

import java.util.Date;
import java.util.Locale;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class Ticket extends EntityWrapper {

    private Seat seat;
    private String passenger;
    private String phone1;
    private String phone2;
    private Status status;
    private City startCity;
    private City endCity;
    private Date startDate;
    private boolean isPartial;
    private double calculatedPrice;
    private Order order;
    private User user;
    private String note;

    public Ticket(Trip trip) {
        super(trip.getEntity().getKey());
    }

    public Ticket(String ticketId, String tripId) {
        super(ticketId, KeyFactory.stringToKey(tripId));
    }

    public Ticket(Entity entity) {
        super(entity);
    }

    public Trip getTrip() {
        return DAOFacade.findById(Trip.class, getParentKey());
    }

    public User getUser() {
        return ((IUserDAO)DAOFacade.getDAO(User.class)).getUserByEmail((String)getProperty("user"));
    }

    public void setUser(User user) {
        setProperty("user", user.getEmail());
    }

    public Seat getSeat() {
        return DAOFacade.findById(Seat.class, (Key)getProperty("seat"));
    }

    public void setSeat(Seat seat) {
        setProperty("seat", seat.getEntity().getKey());
    }

    public String getPassenger() {
        return getProperty("passenger");
    }

    public void setPassenger(String passenger) {
        setProperty("passenger", passenger);
    }

    public String getPhone1() {
        return getProperty("phone1");
    }

    public void setPhone1(String phone) {
        setProperty("phone1", phone);
    }

    public String getPhone2() {
        return getProperty("phone2");
    }

    public void setPhone2(String phone) {
        setProperty("phone2", phone);
    }

    public Status getStatus() {
        Object s = getProperty("status");
        int i = s instanceof Long? (int)((Long) s).longValue() : (Integer)s;

        return Status.valueOf(i);
    }

    public String getPhones() {
        String phones = "";
        if (getPhone1() != null) phones += getPhone1();
        if (getPhone2() != null) phones += ", "+getPhone2();
        return phones;
    }

    public void setStatus(Status status) {
        setProperty("status", status.idx);
    }

    public City getStartCity() {
        return DAOFacade.findById(City.class, (Key)getProperty("startCity"));
    }

    public void setStartCity(City startCity) {
        setProperty("startCity", startCity.getKey());
    }

    public City getEndCity() {
        return DAOFacade.findById(City.class, (Key)getProperty("endCity"));
    }

    public void setEndCity(City endCity) {
        setProperty("endCity", endCity.getKey());
    }

    public Date getStartDate() {
        return getProperty("startDate");
    }

    public void setStartDate(Date startDate) {
        setProperty("startDate", startDate);
    }

    public boolean isPartial() {
        return getProperty("isPartial");
    }

    public void setPartial(boolean isPartial) {
        setProperty("isPartial", isPartial);
    }

    public double getCalculatedPrice() {
        return getProperty("calculatedPrice");
    }

    public void setCalculatedPrice(double calculatedPrice) {
        setProperty("calculatedPrice", calculatedPrice);
    }

    public String getNote() {
        return getProperty("note");
    }

    public void setNote(String note) {
        setProperty("note", note);
    }

    public Order getOrder() {
        if (order == null) {
            Key orderKey = getProperty("orderId");
            if (orderKey == null) return null;

            order = DAOFacade.findById(Order.class, orderKey);
        }
        return order;
    }

    public void setOrder(Order order) {
        setProperty("orderId", order.getKey());
    }

    public JSONObject toJSON(Locale locale) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", getStringKey());
        obj.put("passenger", getPassenger());
        obj.put("phones", getPhones());
        obj.put("trip", getStartCity().getLocalizedName(locale) + " - " + getEndCity().getLocalizedName(locale));
        obj.put("startDate", DateFormatter.format(getStartDate(), locale));
        obj.put("seat", getSeat().getSeatNum());
        obj.put("status", getStatus().name());
        obj.put("price", getCalculatedPrice());
        obj.put("note", getNote());
        return obj;
    }


    public enum Status {
        LOCKED(0),
        PROCESSING(1),
        RESERVED(2),
        BOUGHT(3),
        EXPIRED(4),
        INVALID(5);

        public final int idx;

        Status(int idx) {
            this.idx = idx;
        }

        public static Status valueOf(int idx) {
            for (Status value : Status.values()) {
                if (value.idx == idx) return value;
            }
            return null;
        }
    }

}
