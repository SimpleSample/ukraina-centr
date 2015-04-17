package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.entity.Country;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Nagorny
 * Date: 13.05.14
 */
@Authorized(role = Role.ADMIN)
public class AddCityAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String cityString = req.getParam("city");
        if (cityString == null) return;

        JSONObject cityObj = new JSONObject(cityString);
        String name = cityObj.getString("name");
        boolean isHidden = cityObj.getBoolean("isHidden");
        JSONObject localized = cityObj.getJSONObject("localizedNames");
        Map<Locale, String> locales = new HashMap<Locale, String>();
        while(localized.keys().hasNext()) {
            String locale = (String)localized.keys().next();
            locales.put(Locale.forLanguageTag(locale), localized.getString(locale));
        }
        Country country = DAOFacade.findById(Country.class, KeyFactory.stringToKey((String) req.getParam("country")));

        City city = new City(name, locales, country, isHidden);
        DAOFacade.save(city);
    }
}
