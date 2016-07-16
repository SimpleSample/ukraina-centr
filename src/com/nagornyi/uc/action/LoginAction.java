package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Constants;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.DAOFacade;
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
        String email = (String) req.getSession().getAttribute(Constants.EMAIL);
        if (email != null) {
            log.log(Level.INFO, "User logged in with email:" + email);

            new WelcomeAction().perform(req, resp);
            if (req.getParam(Constants.AUTO) != null) {
                return;
            }
            throw new UserFriendlyException("Ви вже авторизовані в системі");
        }

        email = req.getParam(Constants.EMAIL);
        String password = req.getParam(Constants.PASSWORD);

        UserDAO dao = DAOFacade.getDAO(User.class);
        User user = dao.getUserByEmail(email);
        if (user == null) {
            throw new UserFriendlyException("Користувач з поштою " + email + " відсутній в системі"); //TODO localize
        } else {
            String encPass = MD5Salt.encrypt(password, email);
            log.log(Level.INFO, "User email:" + email + ", pass:"+password+", encrypted new:" + encPass + ", enc old:"+user.getPassword() + ", user role: " + user.getRole());
            if (!encPass.equals(user.getPassword())) {
                throw new UserFriendlyException("Пароль введений невірно"); //TODO localize
            }
        }

        JSONObject obj = new JSONObject();
        obj.put("name", user.getName());
        obj.put("surname", user.getSurname());
        obj.put("email", email);
        obj.put("pass", password);
        obj.put("phone", user.getPhone());
        resp.setDataObject(obj);

        req.getSession().setAttribute(Constants.EMAIL, email);
        req.getSession().setAttribute(Constants.ROLE, user.getRole().level);
    }
}
