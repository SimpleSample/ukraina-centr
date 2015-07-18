package com.nagornyi.uc.entity;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class City extends LocalizedNamesEntity implements Comparable<City> {

    private String info;
    private Boolean hidden;
	private String name;

    public City(Entity entity) {
        super(entity);
    }

    public City(String name, Map<Locale, String> localizedNames, Country country, boolean isHidden) {
        super(name, localizedNames, country.getEntity().getKey());
        setHidden(isHidden);
    }

	@Override
	public String getName() {
		if (name == null) {
			name = super.getName();
		}
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		super.setName(name);
	}

	public String getInfo() {
        return getProperty("info");
    }

    public void setInfo(String info) {
        setProperty("info", info);
    }

    public boolean isHidden() {
		if (hidden == null) {
			hidden = getProperty("hidden");
		}
        return hidden;
    }

    public void setHidden(boolean hidden) {
		this.hidden = hidden;
        setProperty("hidden", hidden);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof City)) return false;

        return getEntity().getKey().equals(((City)obj).getEntity().getKey());
    }

    @Override
    public int hashCode() {
        return getEntity().getKey().hashCode();
    }

    public JSONObject toJSON(Locale loc) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", getStringKey());
        obj.put("name", getLocalizedName(loc));
        obj.put("country", getStringParentKey());
        return obj;
    }

	@Override
	public int compareTo(City o) {
		return this.getName().compareTo(o.getName());
	}

    public static City valueOf(String name, String engName, String itName, String rusName, Country country, boolean isHidden) {
        Map<Locale, String> locales = new HashMap<Locale, String>();
        locales.put(Locale.ENGLISH, engName);
        locales.put(Locale.ITALIAN, itName);
        locales.put(Locale.forLanguageTag("ru"), rusName);
        return new City(name, locales, country, isHidden);
    }
}
