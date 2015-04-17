package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.nagornyi.uc.dao.DAOFacade;

/**
 * @author Nagornyi
 * Date: 03.06.14
 */
public class Discount extends LocalizedNamesEntity {

    public enum Type {
        SUBTRACT,
        MULTIPLY
    }

    private Type type;
    private User user;
    private double value;

    public Discount(Type type, double value, String name) {
        setType(type);
        setValue(value);
        setName(name);
    }

    public Discount(User user, Type type, double value) {
        super(user.getKey());
        setType(type);
        setValue(value);
    }

    public Discount(Entity entity) {
        super(entity);
    }

    public Type getType() {
        return Type.valueOf((String)getProperty("type"));
    }

    public void setType(Type type) {
        setProperty("type", type.name());
    }

    public User getUser() {
        return DAOFacade.findById(User.class, getParentKey());
    }

    public double getValue() {
        return getProperty("value");
    }

    public void setValue(double value) {
        setProperty("value", value);
    }

}
