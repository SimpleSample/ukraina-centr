package com.nagornyi.uc.common.captcha;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.transport.ActionRequest;

/**
 * @author Nagornyi
 * Date: 5/27/14
 */
public class Checker {

	public static boolean isCaptchaValid(ActionRequest req) throws JSONException {
		JSONObject captchaObj = new JSONObject((String)req.getParam("captcha"));
		String ipAddress = req.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = req.getRemoteAddr();
		}
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey("6LfUKfQSAAAAAIRN5UWWlwfJ-UAZ6Q2OnL-BiL7f");

		String challenge = captchaObj.getString("challenge");
		String uresponse = captchaObj.getString("response");
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(ipAddress, challenge, uresponse);

		return reCaptchaResponse.isValid();
	}
}
