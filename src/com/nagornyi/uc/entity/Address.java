package com.nagornyi.uc.entity;

/**
 * @author Nagorny
 *         Date: 25.04.14
 */
public class Address extends EntityWrapper {

    private String country;
    private String state;
    private String city;
    private String street;
    private String building;
    private String flat;

    public String getCountry() {
        return getProperty("country");
    }

    public void setCountry(String country) {
        setProperty("country", country);
    }

    public String getState() {
        return getProperty("state");
    }

    public void setState(String state) {
        setProperty("state", state);
    }

    public String getCity() {
        return getProperty("city");
    }

    public void setCity(String city) {
        setProperty("city", city);
    }

    public String getStreet() {
        return getProperty("street");
    }

    public void setStreet(String street) {
        setProperty("street", street);
    }

    public String getBuilding() {
        return getProperty("building");
    }

    public void setBuilding(String building) {
        setProperty("building", building);
    }

    public String getFlat() {
        return getProperty("flat");
    }

    public void setFlat(String flat) {
        setProperty("flat", flat);
    }
}
