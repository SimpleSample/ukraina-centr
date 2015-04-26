package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.dao.DAOFacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * It is supposed that Route is one for every week
 *
 * @author Nagorny
 * Date: 12.05.14
 */
public class Route extends EntityWrapper {

    private Status status;
    private Date forthStartDate;
    private Date forthEndDate;
    private Date backStartDate;
    private Date backEndDate;
    private Bus bus;
    private Key firstLinkKey;
    private Key lastLinkKey;

    private List<RouteLink> routeLinks = new ArrayList<RouteLink>();

    public Route(Key parentKey) {
        super(parentKey);
    }

    public Route(Status status, Date forthStartDate, Date forthEndDate, Date backStartDate, Date backEndDate, Bus bus) {
        super();
        setStatus(status);
        setForthStartDate(forthStartDate);
        setForthEndDate(forthEndDate);
        setBackStartDate(backStartDate);
        setBackEndDate(backEndDate);
        setBus(bus);
    }

    public Route(Entity entity) {
        super(entity);
    }

    public List<RouteLink> getRouteLinks() {
        return routeLinks;
    }

    public void setRouteLinks(List<RouteLink> routeLinks) {
        this.routeLinks = routeLinks;
    }

    public Status getStatus() {
        Integer status = getProperty("status");
        if (status == null) return Status.ACTIVE;

        return Status.valueOf(status);
    }

    public void setStatus(Status status) {
        setProperty("status", status.idx);
    }

    public Date getForthStartDate() {
        return getProperty("forthStartDate");
    }

    public void setForthStartDate(Date forthStartDate) {
        setProperty("forthStartDate", forthStartDate);
    }

    public Date getForthEndDate() {
        return getProperty("forthEndDate");
    }

    public void setForthEndDate(Date forthEndDate) {
        setProperty("forthEndDate", forthEndDate);
    }

    public Date getBackEndDate() {
        return getProperty("backEndDate");
    }

    public void setBackEndDate(Date backEndDate) {
        setProperty("backEndDate", backEndDate);
    }

    public Date getBackStartDate() {
        return getProperty("backStartDate");
    }

    public void setBackStartDate(Date backStartDate) {
        setProperty("backStartDate", backStartDate);
    }

    public void setBus(Bus bus) {
        setProperty("bus", bus.getEntity().getKey());
    }

    public Bus getBus() {
        return DAOFacade.findByKey(Bus.class, (Key)getProperty("bus"));
    }

    public Key getBusKey() {
        return getProperty("bus");
    }

    public Key getFirstLinkKey() {
        return getProperty("firstLinkKey");
    }

    public void setFirstLinkKey(Key firstLinkKey) {
        setProperty("firstLinkKey", firstLinkKey);
    }

    public void setLastLinkKey(Key lastLinkKey) {
        setProperty("lastLinkKey", lastLinkKey);
    }

    public String getRouteName() {
        return getFirstCity().getName() + " - " + getLastCity().getName();
    }

    public City getFirstCity() {
        RouteLink first = DAOFacade.findByKey(RouteLink.class, (Key)getProperty("firstLinkKey"));
        return DAOFacade.findByKey(City.class, first.getPreviousCityKey());
    }

    public City getLastCity() {
        RouteLink first = DAOFacade.findByKey(RouteLink.class, (Key)getProperty("lastLinkKey"));
        return DAOFacade.findByKey(City.class, first.getNextCityKey());
    }

    public enum Status {
        ACTIVE(0),
        INACTIVE(1);

        private int idx;

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
