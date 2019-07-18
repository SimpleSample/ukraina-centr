package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;


@Authorized
public class GetUserAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        User user = req.getUser();
        JSONObject result = new JSONObject();
        result.put("name", user.getName());
        result.put("surname", user.getSurname());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("language", user.getLanguageTag());
        resp.setDataObject(result);
    }
}
