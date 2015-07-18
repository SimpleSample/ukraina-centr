package com.nagornyi.uc.transport;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 29.04.14
 */
public class ActionResponse {
    private static final Logger log = Logger.getLogger(ActionResponse.class.getName());

    private JSONObject responseObj = new JSONObject();
    private JSONObject dataObj;


    public ActionResponse(JSONObject responseObj) {
        this.responseObj = responseObj;
    }

    public void setDataObject(JSONObject obj) {
        dataObj = obj;
    }

    public JSONObject getDataObj() {
        return dataObj;
    }

    public void setResponseParam(String key, String value) {
        if (dataObj == null) {
            dataObj = new JSONObject();
        }
        try {
            dataObj.put(key, value);
        } catch (JSONException e) {
            log.warning("Couldn't set value '" + key +"=" +value+"' to response");
        }
    }
}
