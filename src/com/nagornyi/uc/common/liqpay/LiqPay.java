package com.nagornyi.uc.common.liqpay;

import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
import com.google.gdata.util.common.util.Base64;
import com.google.gdata.util.common.util.Base64DecoderException;
import com.nagornyi.env.EnvVariablesStorage;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class LiqPay {
    private static Logger log = Logger.getLogger(LiqPay.class.getName());

    static final String ENV_PARAMETER_GROUP = LiqPay.class.getSimpleName();
    private static final int LIQPAY_VERSION = 3;
    private static final String LANGUAGE = "uk";
    private static final String PAY_ACTION = "pay";
    private static final String CURRENCY = "EUR"; // USD, EUR, RUB, UAH
    private static final String DESCRIPTION = "Оплата квитків на сайті www.ukraina-centr.com";
    public static final String UC_KEY = "@;";

    public static LiqPayRequest createLiqPayRequest(double price, String orderId, String orderDesc) throws JSONException {
        String liqpayData = createLiqPayDataField(price, orderId, orderDesc);
        String signature = generateSignature(liqpayData);

        return new LiqPayRequest(liqpayData, signature);
    }

    public static String getPaymentDescription(Order order, User user, List<Ticket> tickets) {
        Ticket ticket = null;
        Ticket backTicket = null;
        for (Ticket ticket1: tickets) {
            if (ticket1.getTrip().isRouteForth()) {
                ticket = ticket1;
            } else {
                backTicket = ticket1;
            }
        }
        StringBuilder builder = new StringBuilder(Long.toString(order.getExternalId())).append(LiqPay.UC_KEY).append(" ").append(user.getUsername());

        if (ticket == null && backTicket != null) {
            ticket = backTicket;
            backTicket = null;
        }
        // we don't know which ticket is actually forth, so ...
        if (ticket != null && backTicket != null) {
            if (ticket.getStartDate().getTime() > backTicket.getStartDate().getTime()) {
                Ticket temp = ticket;
                ticket = backTicket;
                backTicket = temp;
            }
        }
        if (ticket != null) {
            String userTrip = ticket.getStartCity().getLocalizedName(Locale.ENGLISH) + " - " + ticket.getEndCity().getLocalizedName(Locale.ENGLISH);
            builder.append(", ").append(userTrip);
            String startDate = DateFormatter.defaultShortFormat(ticket.getStartDate());
            builder.append(", ").append(startDate);
        }

        if (backTicket != null) {
            String startBackDate = DateFormatter.defaultShortFormat(backTicket.getStartDate());
            builder.append(", повернення: ").append(startBackDate);

        }
        String result = builder.toString();
        log.info("payment description: " + result);
        return result;
    }

    public static String generateSignature(String liqpayData) {
        String privateKey = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "privateKey");
        return sha1base64Encoding(privateKey + liqpayData + privateKey);
    }

    private static String createLiqPayDataField(double amount, String orderId, String orderDesc) throws JSONException {
        String publicKey = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "publicKey");
        String serverUrl = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "serverUrl");

        JSONObject params = new JSONObject();
        params.put("version", LIQPAY_VERSION);
        params.put("public_key", publicKey);
        params.put("action", PAY_ACTION);
        params.put("amount", amount);
        params.put("currency", CURRENCY);
        params.put("description", DESCRIPTION);
        params.put("order_id", orderDesc);
        params.put("language", LANGUAGE);
        params.put("result_url", serverUrl + "?orderId=" + orderId);
        params.put("server_url", serverUrl + "lpCallback");

        return Base64.encode(params.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String sha1base64Encoding(String str) {
        return LiqPayUtil.base64Encode(LiqPayUtil.sha1(str));
    }

    public static boolean isValid(Map<String, String> params) {
        String signature = params.get("signature");
        String data = params.get("data");

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
        } catch (Base64DecoderException e) {
            throw new IllegalArgumentException("Couldn't Base64.decode " + dataJson);
        }

        return result;
    }
}