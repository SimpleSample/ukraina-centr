package com.nagornyi.uc.action;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.appengine.repackaged.org.json.JSONException;
import com.nagornyi.uc.Constants;
import com.nagornyi.uc.Role;
import com.nagornyi.uc.common.UserFriendlyException;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.dao.IUserDAO;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.oauth2.AuthUtil;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import com.nagornyi.uc.util.ActionUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class ValidateGoogleUserAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        ActionUtil.checkRequired(req, Constants.ID_TOKEN);

        String idToken = req.getParam(Constants.ID_TOKEN);
        String email = req.getParam(Constants.EMAIL);
        String imageUrl = req.getParam(Constants.IMAGE_URL);

        GoogleIdToken.Payload payload = AuthUtil.validateAndGetGoogleUser(email, idToken);
        if (payload != null) {
            IUserDAO dao = DAOFacade.getDAO(User.class);
            User user = dao.getUserByEmail(email);
            if (user == null) {
                user = new User(payload.getSubject());
                user.setEmail(payload.getEmail());
                String[] nameAndSurname = ((String)req.getParam(Constants.NAME)).split(" ");
                user.setName(nameAndSurname[0]);
                if (nameAndSurname.length == 2) {
                    user.setSurname(nameAndSurname[1]);
                }

                String encPass = MD5Salt.encrypt(RandomStringUtils.randomAlphanumeric(8), email);
                user.setPassword(encPass);
                user.setRole(Role.USER);
                user.setUserLocale(Locale.forLanguageTag("uk")); //TODO localization issue
                user.setImageUrl(imageUrl);
                dao.create(user);
            } else {
                if (user.getImageUrl() == null) {
                    user.setImageUrl(imageUrl);
                    dao.save(user);
                }
            }

            req.getSession().setAttribute("email", email);
            req.getSession().setAttribute("role", user.getRole().level);
        } else {
            throw new UserFriendlyException("Помилка входу через Google");
        }
    }
}
