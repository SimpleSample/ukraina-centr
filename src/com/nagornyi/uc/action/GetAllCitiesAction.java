package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.ICityDAO;
import com.nagornyi.uc.entity.City;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.List;
import java.util.Locale;

/**
 * @author Nagorny
 * Date: 24.05.14
 */
public class GetAllCitiesAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        Locale loc = DateFormatter.UK_LOCALE; // TODO temp
        List<City> allCities = ((ICityDAO) DAOFacade.getDAO(City.class)).getAllCities();
        JSONObject respObj = new JSONObject();
        JSONArray cities = new JSONArray();
        for (City city: allCities) {
            if (!city.isHidden()) {
                cities.put(city.toJSON(loc));
            }
        }
        respObj.put("cities", cities);
        resp.setDataObject(respObj);
    }
}
