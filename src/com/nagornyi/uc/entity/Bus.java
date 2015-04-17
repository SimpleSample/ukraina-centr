package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ISeatDAO;
import com.nagornyi.uc.dao.app.EntityDAO;
import com.nagornyi.uc.dao.app.SeatDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class Bus extends NamedEntity {

    private int seatsNum;
    private String schemeHTML;
    private List<Seat> seats = new ArrayList<Seat>();

    public Bus() {
    }

    public Bus(Entity entity) {
        super(entity);
    }

    public Bus(Key parentKey) {
        super(parentKey);
    }

    public int getSeatsNum() {
        Object s = getProperty("seatsNum");
        if (s instanceof Long) {
            return (int)((Long) s).longValue();
        }
        return (Integer)s;
    }

    public void setSeatsNum(int seatsNum) {
        setProperty("seatsNum", seatsNum);
    }

    public String getSchemeHTML() {
        return getProperty("schemeHTML");
    }

    public void setSchemeHTML(String schemeHTML) {
        setProperty("schemeHTML", schemeHTML);
    }

    public List<Seat> getSeats() {
        if (seats.isEmpty()) {
            seats = DAOFacade.findByParent(Seat.class, this.getEntity().getKey());
        }
        return seats;
    }
}
