package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.context.RequestContext;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

/**
 * @author Nagornyi
 *         Date: 01.07.14
 */
public class ChangePassAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String oldPass = req.getParam("oldPass");
        String newPass = req.getParam("newPass");
        if (oldPass != null && newPass != null) {
            User user = RequestContext.getUser();
            String encPass = MD5Salt.encrypt(oldPass, user.getEmail());
            if (encPass.equals(user.getPassword())) {
                String newEncPass = MD5Salt.encrypt(newPass, user.getEmail());
                user.setPassword(newEncPass);
                DAOFacade.save(user);

                JSONObject response = new JSONObject();
                response.put("newPass", newPass);
                resp.setDataObject(response);
            }
        }
    }
}
