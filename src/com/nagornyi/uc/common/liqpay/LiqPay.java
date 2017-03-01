package com.nagornyi.uc.common.liqpay;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.nagornyi.env.EnvVariablesStorage;
import com.nagornyi.uc.common.date.DateFormatter;
import com.nagornyi.uc.entity.Order;
import com.nagornyi.uc.entity.Ticket;
import com.nagornyi.uc.entity.User;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

public class LiqPay {
    private static Logger log = Logger.getLogger(LiqPay.class.getName());

    static final String ENV_PARAMETER_GROUP = LiqPay.class.getSimpleName();
    private static String LANGUAGE = "ru";
    private static String TYPE = "buy";
    private static String CURRENCY = "EUR"; // USD, EUR, RUB, UAH
    private static String DESCRIPTION = "Оплата квитків на сайті www.ukraina-centr.com";
    public static String UC_KEY = "@;";

    public static JSONObject getLiqPayReservationJSON(double price, String orderId, String orderDesc) throws JSONException {
        JSONObject responseObj = new JSONObject();


        HashMap<String, String> liqpayParams = getLiqPayParams(price, CURRENCY, DESCRIPTION, orderId, orderDesc);
        for(String param: liqpayParams.keySet()) {
            responseObj.put(param, liqpayParams.get(param));
        }
        return responseObj;
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
        StringBuilder result = new StringBuilder(Long.toString(order.getExternalId())).append(LiqPay.UC_KEY).append(" ").append(user.getUsername());

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
            result.append(", ").append(userTrip);
            String startDate = DateFormatter.defaultShortFormat(ticket.getStartDate());
            result.append(", ").append(startDate);
        }

        if (backTicket != null) {
            String startBackDate = DateFormatter.defaultShortFormat(backTicket.getStartDate());
            result.append(", повернення: ").append(startBackDate);

        }
        log.info("order_id length: " + result.toString().length());
        return result.toString();
    }

    public static HashMap<String, String> getLiqPayParams(double amount, String currency, String description, String orderId, String orderDesc) {
        HashMap<String, String> params = new HashMap<>();
        params.put("amount", Double.toString(amount));
        params.put("currency", currency);
        params.put("description", description);
        params.put("order_id", orderDesc);
        params.put("type", TYPE);
        String serverUrl = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "serverUrl");
        params.put("result_url", serverUrl + "?orderId=" + orderId);
        params.put("server_url", serverUrl + "lpCallback");
        String publicKey = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "publicKey");
        params.put("public_key", publicKey);
        params.put("language", LANGUAGE);
        String sandbox = EnvVariablesStorage.getValue(ENV_PARAMETER_GROUP, "sandbox");
        params.put("sandbox", sandbox);
        params.put("signature", createSignature(params));
        return params;
    }

    public static String createSignature(Map<String, String> paramsMap) {
        String result = new LiqPaySignatureBuilder(paramsMap.get("amount"), paramsMap.get("currency"), paramsMap.get("description"))
                .orderId(paramsMap.get("order_id"))
                .type(paramsMap.get("type"))
                .resultUrl(paramsMap.get("result_url"))
                .serverUrl(paramsMap.get("server_url"))
                .firstName(paramsMap.get("sender_first_name"))
                .lastName(paramsMap.get("sender_last_name"))
                .middleName(paramsMap.get("sender_middle_name"))
                .countryCode(paramsMap.get("sender_country"))
                .cityName(paramsMap.get("sender_city"))
                .address(paramsMap.get("sender_address"))
                .postalCode(paramsMap.get("sender_postal_code"))
                .status(paramsMap.get("status"))
                .transactionId(paramsMap.get("transaction_id"))
                .senderPhone(paramsMap.get("sender_phone"))
                .build();

        return sha1base64Encoding(result);
    }

    public static boolean isValid(Map<String, String> params) {
        String signature = params.get("signature");
        if (signature == null) {
            return false;
        }

        if (LiqPay_v3.isVersion3(params)) {
            return LiqPay_v3.isValid(params);
        } else {
            String calcSignature = createSignature(params);
            return signature.equals(calcSignature);
        }
    }

    public static String sha1base64Encoding(String str) {
        return LiqPayUtil.base64Encode(LiqPayUtil.sha1(str));
    }
}