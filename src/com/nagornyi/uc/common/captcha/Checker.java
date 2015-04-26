package com.nagornyi.uc.common.captcha;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.transport.ActionRequest;

import java.util.logging.Logger;

/**
 * @author Nagornyi
 * Date: 5/27/14
 */
public class Checker {
    private static Logger log = Logger.getLogger(Checker.class.getName());

	public static boolean isCaptchaValid(ActionRequest req) throws JSONException {
		String ipAddress = req.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = req.getRemoteAddr();
		}
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey("6LfUKfQSAAAAAIRN5UWWlwfJ-UAZ6Q2OnL-BiL7f");

		String uresponse = req.getParam("captcha");
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(ipAddress, uresponse);

        if (!reCaptchaResponse.isValid()) {
            log.warning("Captcha validation failed: " + reCaptchaResponse.getErrorMessage());
        }

		return reCaptchaResponse.isValid();
	}
}
