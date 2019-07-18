package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * @author Nagorny
 *         Date: 30.04.14
 */
public class LogoutAction implements Action {
    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        req.getSession().invalidate();
    }
}
