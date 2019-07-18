package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.currency.CurrencyConverter;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * @author Nagornyi
 * Date: 5/27/14
 */
public class WelcomeAction implements Action {

	@Override
	public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        JSONObject respObject = resp.getDataObj() == null? new JSONObject() : resp.getDataObj();
        respObject.put("EURUAH", CurrencyConverter.fromBase(1));
        resp.setDataObject(respObject);
	}
}
