package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Nagorny
 *         Date: 25.04.14
 */
public class Country extends LocalizedNamesEntity {

    public Country(Entity entity) {
        super(entity);
    }

    public Country(String name, Map<Locale, String> localizedNames, String id) {
        super(name, localizedNames, id);
        setName(name);
    }

    public List<City> getCities() {
        return new ArrayList<City>(); //TODO
    }

    public void setCities(List<City> cities) {
        //TODO
    }
}
