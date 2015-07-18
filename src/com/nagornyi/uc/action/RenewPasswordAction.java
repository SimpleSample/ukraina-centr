package com.nagornyi.uc.action;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.common.mail.MailFacade;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.app.UserDAO;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by Artem on 29.06.2015.
 */
public class RenewPasswordAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        String email = req.getParam("email");
        UserDAO userDAO = DAOFacade.getDAO(User.class);
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            throw new UserFriendlyException("В системі не знайдено користувача зі вказаним e-mail");
        }
        String newPass = RandomStringUtils.random(8, true, true);
        String newEncPass = MD5Salt.encrypt(newPass, user.getEmail());
        user.setPassword(newEncPass);
        DAOFacade.save(user);
        MailFacade.sendRenewPass(user, newPass);
    }
}
