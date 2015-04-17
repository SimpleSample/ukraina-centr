package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.app.UserDAO;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nagorny
 * Date: 28.04.14
 */
public class LoginAction implements Action {
    private static Logger log = Logger.getLogger(LoginAction.class.getName());

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        if (req.getSession().getAttribute("email") != null) {
            String email = (String)req.getSession().getAttribute("email");
            log.log(Level.INFO, "User logged in with email:" + email);
            new WelcomeAction().perform(req, resp);
            if (req.getParam("auto") != null) return;
            throw new UserFriendlyException("Ви вже авторизовані в системі");
        }

        String email = req.getParam("email");
        String password = req.getParam("password");
        String errorMessage = null;
        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);
        if (user == null) {
            errorMessage = "Користувач з поштою " + email + " відсутній в системі"; //TODO localize
        } else {
            String encPass = MD5Salt.encrypt(password, email);
            log.log(Level.INFO, "User email:" + email + ", pass:"+password+", encrypted new:" + encPass + ", enc old:"+user.getPassword() + ", user role: " + user.getRole());
            if (!encPass.equals(user.getPassword())) {
                errorMessage = "Пароль введений невірно"; //TODO localize
            }
        }
        if (errorMessage != null) throw new UserFriendlyException(errorMessage);

        JSONObject obj = new JSONObject();
        obj.put("name", user.getName());
        obj.put("surname", user.getSurname());
        obj.put("email", email);
        obj.put("pass", password);
        obj.put("phone", user.getPhone());
        resp.setDataObject(obj);

        req.getSession().setAttribute("email", email);
        req.getSession().setAttribute("role", user.getRole().level);
    }
}
