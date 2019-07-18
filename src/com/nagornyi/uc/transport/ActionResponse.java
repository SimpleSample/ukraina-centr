package com.nagornyi.uc.transport;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 29.04.14
 */
public class ActionResponse {
    private static final Logger log = Logger.getLogger(ActionResponse.class.getName());
    // old, to be removed
    private JSONObject dataObj;
    // new
    private Object data;


    public void setDataObject(JSONObject obj) {
        dataObj = obj;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public JSONObject getDataObj() {
        return dataObj;
    }

    public void setResponseParam(String key, String value) {
        setValue(key, value);
    }

    public void setResponseParam(String key, JSONObject value) {
        setValue(key, value);
    }

    public void setResponseParam(String key, JSONArray value) {
        setValue(key, value);
    }

    private void setValue(String key, Object value) {
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
