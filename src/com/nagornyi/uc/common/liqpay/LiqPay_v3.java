package com.nagornyi.uc.common.liqpay;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.repackaged.com.google.common.util.Base64;
import com.nagornyi.env.EnvVariablesStorage;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LiqPay_v3 {

    public static boolean isVersion3(Map<String, String> params) {
        return params.containsKey("data");
    }

    public static boolean isValid(Map<String, String> params) {
        String data = params.get("data");
        String signature = params.get("signature");

        if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(data)) {
            return false;
        }

        String privateKey = EnvVariablesStorage.getValue(LiqPay.ENV_PARAMETER_GROUP, "privateKey");
        return signature.equals(LiqPay.sha1base64Encoding(privateKey + data + privateKey));
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> parseParams(Map<String, String> requestData) {
        Map<String, String> result = new HashMap<>();
        String dataJson = requestData.get("data");
        try {
            String parsed = new String(Base64.decode(dataJson));
            JSONObject data = new JSONObject(parsed);
            Iterator<String> keys = data.keys();
            while (keys.hasNext()) {
                String nextKey = keys.next();
                result.put(nextKey, data.get(nextKey).toString());
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException("Couldn't parse liqpay request data " + dataJson);
        }

        return result;
    }
}
