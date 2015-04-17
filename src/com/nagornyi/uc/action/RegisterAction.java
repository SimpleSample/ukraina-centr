package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.common.captcha.Checker;
import com.nagornyi.uc.dao.app.UserDAO;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Locale;

/**
 * @author Nagorny
 * Date: 25.04.14
 */
public class RegisterAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {

		if (!Checker.isCaptchaValid(req)) throw new UserFriendlyException("Капча введена не вірно, спробуйте ще раз");

        String username = req.getParam("username");
        String email = req.getParam("email");
        String password = req.getParam("password");
        String surname = req.getParam("surname");
        String phone = req.getParam("phone");

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);
        if (user != null) throw new UserFriendlyException("Користувач з поштою " + email + " вже існує в системі"); //TODO localize

        User newUser = new User();
        newUser.setName(username);
        newUser.setEmail(email);
        String encPass = MD5Salt.encrypt(password, email);
        newUser.setPassword(encPass);
        newUser.setSurname(surname);
        newUser.setRole(Role.USER);
        newUser.setPhone(phone);
		newUser.setUserLocale(Locale.forLanguageTag("uk")); //TODO localization issue
        dao.create(newUser);

        JSONObject obj = new JSONObject();
        obj.put("name", username);
        obj.put("email", email);
        obj.put("pass", password);
        obj.put("surname", surname);
        obj.put("phone", phone);
        resp.setDataObject(obj);

		req.getSession().setAttribute("email", email);
		req.getSession().setAttribute("role", Role.USER.level);
    }
}
