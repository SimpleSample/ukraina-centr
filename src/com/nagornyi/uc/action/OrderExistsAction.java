package com.nagornyi.uc.action;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * @author Nagornyi
 * Date: 26.06.14
 */
public class OrderExistsAction implements Action {
    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String orderId = req.getParam("orderId");
        if (orderId != null) {
            try {
                //if decoded - ok
                KeyFactory.stringToKey(orderId);
                JSONObject response = new JSONObject();
                response.put("orderId", orderId);
                resp.setDataObject(response);
            } catch (Exception e) {
                //nothing to do, just a check
            }
        }
    }
}
