package com.nagornyi.uc.transport;

import com.google.appengine.labs.repackaged.org.json.JSONObject;

/**
 * @author Nagorny
 *         Date: 29.04.14
 */
public class ActionResponse {
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
}
