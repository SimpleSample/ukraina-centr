package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import java.util.Locale;
import java.util.Map;

/**
 * @author Nagorny
 * Date: 16.05.14
 */
public class LocalizedNamesEntity extends NamedEntity {

    public LocalizedNamesEntity() {
    }

    public LocalizedNamesEntity(Entity entity) {
        super(entity);
    }

    public LocalizedNamesEntity(Key parentKey) {
        super(parentKey);
    }

    public LocalizedNamesEntity(String name, Map<Locale, String> localizedNames, Key parentKey) {
        super(parentKey);
        setName(name);
        for (Locale locale: localizedNames.keySet()) {
            setLocalizedName(locale, localizedNames.get(locale));
        }
    }

    public LocalizedNamesEntity(String name, Map<Locale, String> localizedNames, String id) {
        super(id);
        setName(name);
        for (Locale locale: localizedNames.keySet()) {
            setLocalizedName(locale, localizedNames.get(locale));
        }
    }

    public String getLocalizedName(Locale loc) {
        if ("uk".equals(loc.getLanguage())) return getName();
        return getProperty(loc.getLanguage());
    }

    public void setLocalizedName(Locale loc, String localizedName) {
        if ("uk".equals(loc.getLanguage())) {
            setName(localizedName);
        } else {
            setProperty(loc.getLanguage(), localizedName);
        }
    }
}
