package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.app.UserDAO;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;

import java.util.Locale;

/**
 * @author Nagornyi
 * Date: 20.06.14
 */
@Authorized(role = Role.ADMIN)
public class AddUserAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String username = req.getParam("username");
        String email = req.getParam("email");
        String password = req.getParam("password");
        String surname = req.getParam("surname");
        String phone = req.getParam("phone");
        int role = Integer.parseInt((String) req.getParam("role"));
        Locale locale = Locale.forLanguageTag((String)req.getParam("lang"));

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);
        if (user != null) throw new UserFriendlyException("Користувач з поштою " + email + " вже існує в системі"); //TODO localize

        User newUser = new User();
        newUser.setName(username);
        newUser.setEmail(email);
        String encPass = MD5Salt.encrypt(password, email);
        newUser.setPassword(encPass);
        newUser.setSurname(surname);
        newUser.setRole(Role.valueOf(role));
        newUser.setPhone(phone);
        newUser.setUserLocale(locale);
        dao.create(newUser);
    }
}
