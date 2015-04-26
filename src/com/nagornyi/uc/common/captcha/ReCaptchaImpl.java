/*
 * Copyright 2007 Soren Davidsen, Tanesha Networks
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nagornyi.uc.common.captcha;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.common.captcha.http.HttpLoader;
import com.nagornyi.uc.common.captcha.http.SimpleHttpLoader;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;

public class ReCaptchaImpl {

	public static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	private String privateKey;
	private HttpLoader httpLoader = new SimpleHttpLoader();

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public ReCaptchaResponse checkAnswer(String remoteAddr, String response) {
		String postParameters = "secret=" + URLEncoder.encode(privateKey) + "&remoteip=" + URLEncoder.encode(remoteAddr) +
			"&response=" + URLEncoder.encode(response);

		String message = httpLoader.httpPost(VERIFY_URL, postParameters);

		if (message == null) {
			return new ReCaptchaResponse(false, "Null read from server.");
		}

        Boolean success = false;
        String errorMessage = null;
        try {
            JSONObject captchaResponse = new JSONObject(message);
            success = captchaResponse.getBoolean("success");
        } catch (JSONException e) {
            try {
                JSONObject captchaResponse = new JSONObject(message);
                errorMessage = captchaResponse.getString("error-codes");
            } catch (JSONException e1) {
                return new ReCaptchaResponse(false, "Could not parse response: " + message);
            }
        }

		return new ReCaptchaResponse(success, errorMessage);
	}
}
