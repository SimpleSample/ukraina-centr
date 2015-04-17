package com.nagornyi.uc.currency.loader;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.uc.appinfo.AppInfoLoadException;
import com.nagornyi.uc.appinfo.AppInfoLoader;
import com.nagornyi.uc.common.captcha.http.HttpLoader;
import com.nagornyi.uc.common.captcha.http.SimpleHttpLoader;

import java.util.Map;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public class RateExchangeAppspotLoader implements AppInfoLoader {
    private static String LOAD_URI = "http://rate-exchange.appspot.com/currency?from=EUR&to=UAH";

    HttpLoader httpLoader = new SimpleHttpLoader();

    @Override
    public void load(Map<String, Object> map) throws AppInfoLoadException {
        String googleResp = httpLoader.httpGet(LOAD_URI);  //{"to": "UAH", "rate": 16.1401, "from": "EUR"}
        if (googleResp != null) {
            JSONObject object = new JSONObject();
            try {
                map.put("currencyRate", object.getDouble("rate"));
            } catch (JSONException e) {
                throw new AppInfoLoadException("Couldn't parse response from " + LOAD_URI, e);
            }
        }
    }
}
