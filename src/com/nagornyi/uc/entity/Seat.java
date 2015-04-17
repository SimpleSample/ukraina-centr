package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * @author Nagorny
 *         Date: 12.05.14
 */
public class Seat extends EntityWrapper {

    private String seatNum;
    private boolean initiallyBlocked;

    public Seat(Key parentKey) {
        super(parentKey);
    }

    public Seat(Entity entity) {
        super(entity);
    }

    public String getSeatNum() {
        return getProperty("seatNum");
    }

    public void setSeatNum(String seatNum) {
        setProperty("seatNum", seatNum);
    }

    public boolean isInitiallyBlocked() {
        return getProperty("initiallyBlocked");
    }

    public void setInitiallyBlocked(boolean initiallyBlocked) {
        setProperty("initiallyBlocked", initiallyBlocked);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Seat)) return false;
        return this.getEntity().getKey().equals(((Seat) obj).getEntity().getKey());
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", KeyFactory.keyToString(getEntity().getKey()));
        object.put("seatNum", getSeatNum());
        return object;
    }

    public static Seat valueOf(JSONObject object) {
        return null; //TODO
    }
}
