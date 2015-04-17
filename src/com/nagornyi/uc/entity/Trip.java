package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.dao.DAOFacade;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Nagorny
 * Date: 13.05.14
 */
public class Trip extends EntityWrapper {

    private Route route;
    private List<Seat> seats;
    Date startDate;
    Date endDate;
    boolean isForth;
    int seatsNum;

    public Trip(Entity entity) {
        super(entity);
    }

    public Trip(Route route, Date startDate, Date endDate, int seatsNum, boolean isForth) {
        super(route.getEntity().getKey());
        setStartDate(startDate);
        setEndDate(endDate);
        setForth(isForth);
        setSeatsNum(seatsNum);
    }

    public boolean isForth() {
        return getProperty("forth");
    }

    public void setForth(boolean forth) {
        setProperty("forth", forth);

    }

    public int getSeatsNum() {
        Object s = getProperty("seatsNum"); //TODO weird
        if (s instanceof Long) {
            return (int)((Long) s).longValue();
        }
        return (Integer)s;
    }

    public void setSeatsNum(int seatsNum) {
        setProperty("seatsNum", seatsNum);
    }

    public Route getRoute() {
        return DAOFacade.findByKey(Route.class, this.getEntity().getParent());
    }

    public List<Seat> getSeats() {
        return DAOFacade.findByParent(Seat.class, this.getEntity().getKey());
    }

    public Date getStartDate() {
        return getProperty("startDate");
    }

    public void setStartDate(Date startDate) {
        setProperty("startDate", startDate);
    }

    public Date getEndDate() {
        return getProperty("endDate");
    }

    public void setEndDate(Date endDate) {
        setProperty("endDate", endDate);
    }
}
