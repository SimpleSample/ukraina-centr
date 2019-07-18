package com.nagornyi.uc.action;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.uc.dao.DAOFacade;
import com.nagornyi.uc.entity.User;
import com.nagornyi.uc.transport.ActionRequest;
import com.nagornyi.uc.transport.ActionResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

@Authorized
public class UpdateUserAction implements Action {

    @Override
    public void perform(ActionRequest req, ActionResponse resp) throws JSONException {
        User user = req.getUser();

        String phone = req.getParam("phone");
        String language = req.getParam("language");
        String name = req.getParam("name");
        String surname = req.getParam("surname");

        String errors = "";
        if (StringUtils.isEmpty(name)) {
            errors += " Ім'я не заповнене<br>";
        }
        if (StringUtils.isEmpty(surname)) {
            errors += " Прізвище не заповнене<br>";
        }
        if (!checkPhone(phone)) {
            errors += " Некоректний формат телефону<br>";
        }
        if (StringUtils.isEmpty(language)) {
            errors += " Мова не заповнена<br>";
        }

        JSONObject result = new JSONObject();
        if (StringUtils.isNoneEmpty(errors)) {
            result.put("errorMessage", errors);
        } else {
            user.setPhone(phone);
            user.setUserLocale(Locale.forLanguageTag(language));
            user.setName(name);
            user.setSurname(surname);

            DAOFacade.save(user);
        }
        resp.setDataObject(result);
    }

    private boolean checkPhone(String phone) {
        Pattern pattern = Pattern.compile("^[\\s()+-]*([0-9][\\s()+-]*){6,20}$");
        return pattern.matcher(phone).matches();
    }

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private boolean checkEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        return pattern.matcher(email).matches();
    }
}
