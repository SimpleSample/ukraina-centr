package com.nagornyi.uc.common.captcha;

import com.google.appengine.repackaged.com.google.common.base.Strings;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.nagornyi.env.EnvVariablesStorage;
import com.nagornyi.uc.transport.ActionRequest;

import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 5/27/14
 */
public class Checker {
    private static Logger log = Logger.getLogger(Checker.class.getName());
	private static final String ENV_PARAMETER_GROUP = "captcha";

	public static boolean isCaptchaValid(ActionRequest req) throws JSONException {
		String ipAddress = req.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = req.getRemoteAddr();
		}
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		String captchaPrivateKey = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "privateKey");
		reCaptcha.setPrivateKey(captchaPrivateKey);

		String uresponse = req.getParam("captcha");
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(ipAddress, uresponse);

        if (!reCaptchaResponse.isValid()) {
            log.warning("Captcha validation failed: " + reCaptchaResponse.getErrorMessage());
        }

		return reCaptchaResponse.isValid();
	}
}
