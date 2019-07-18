package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public interface Action {

    void perform(ActionRequest req, ActionResponse resp) throws JSONException;
}
