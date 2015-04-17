package com.nagornyi.uc.common.liqpay;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LiqPay {
//    private Proxy __PROXY = null;
//    private String __PROXY_AUTH = null;

    private static String host = "https://www.liqpay.com/api/";
    private static String pub_key = "i30587714188";
    private static String priv_key = "fc1iwzi5oHaOhSqME8H849tqSuNi8At1x3g2RdqY";
    private static String result_url = "http://www.ukraina-centr.com/tickets.html?orderId=";
    private static String server_url = "http://www.ukraina-centr.com/lpCallback";
    private static String language = "ru";
    private static String sandbox = "0"; // TODO make it config
    private static String type = "buy";
    private static String currency = "UAH";
    private static String description = "Оплата бронювання квитків на сайті www.ukraina-centr.com";

    public LiqPay() {}

    public LiqPay(String public_key, String private_key, String url) {
        pub_key = public_key;
        priv_key = private_key;
        host = url;
    }

    public static JSONObject getLiqPayReservationJSON(int ticketsNumber, String orderId) throws JSONException {
        JSONObject responseObj = new JSONObject();
        int forOne = 200; // TODO make it config

        HashMap<String, String> liqpayParams = getLiqPayParams(forOne * ticketsNumber, currency, description, orderId);
        for(String param: liqpayParams.keySet()) {
            responseObj.put(param, liqpayParams.get(param));
        }
        return responseObj;
    }

    @SuppressWarnings("unchecked")
    public JSONObject api(String path, HashMap<String, String> list) throws Exception{

        JSONObject json = new JSONObject();

        json.put("public_key", pub_key);

        for (Map.Entry<String, String> entry: list.entrySet())
            json.put(entry.getKey(), entry.getValue());

        String dataJson = json.toString();
        String signature = LiqPayUtil.base64_encode( LiqPayUtil.sha1( priv_key + dataJson + priv_key) );

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("data", dataJson);
        data.put("signature", signature);
        String resp = LiqPayRequest.post(host + path, data, this);

        JSONObject jsonObj = new JSONObject(resp);

//        HashMap<String, Object> res_json = LiqPayUtil.parseJson(jsonObj);

        return jsonObj;

    }

    public String cnb_form(HashMap<String, String> list){

        String language = "ru";
        if(list.get("language") != null)
            language = list.get("language");

        String signature = cnb_signature(list);

        list.put("public_key", pub_key);
        list.put("signature", signature);

        String form = "";
        form += "<form method=\"post\" action=\"https://www.liqpay.com/api/pay\" accept-charset=\"utf-8\">\n";

        for (Map.Entry<String, String> entry: list.entrySet())
            form += "<input type=\"hidden\" name=\""+entry.getKey()+"\" value=\""+entry.getValue()+"\" />\n";

        form += "<input type=\"image\" src=\"//static.liqpay.com/buttons/p1"+language+".radius.png\" name=\"btn_text\" />\n";
        form += "</form>\n";

        return form;

    }

    public static HashMap<String, String> getLiqPayParams(double amount, String currency, String description, String orderId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("amount", ""+amount);
        params.put("currency", currency);
        params.put("description", description);
        params.put("order_id", orderId);
        params.put("type", type);
        params.put("result_url", result_url+orderId);
        params.put("server_url", server_url);
        params.put("public_key", pub_key);
        params.put("language", language);
        params.put("sandbox", sandbox);
        params.put("signature", cnb_signature(params));
        return params;
    }

    public static String getSignature(double amount, String currency, String description, String orderId, String type) {
        HashMap params = new HashMap();
        params.put("amount", ""+amount);
        params.put("currency", currency);
        params.put("description", description);
        params.put("order_id", orderId);
        params.put("type", type);

        return cnb_signature(params);
    }

    public static String cnb_signature(Map<String, String> list) {
        String amount = list.get("amount");
        String currency = list.get("currency");
        String order_id = list.get("order_id");
        String type = list.get("type");
        String description = list.get("description");
        String result_url = list.get("result_url");
        String server_url = list.get("server_url");
        String first_name = list.get("sender_first_name");
        String last_name = list.get("sender_last_name");
        String middle_name = list.get("sender_middle_name");
        String country_code = list.get("sender_country");
        String city_name = list.get("sender_city");
        String address = list.get("sender_address");
        String postal_code = list.get("sender_postal_code");
        String status = list.get("status");
        String transaction_id = list.get("transaction_id");
        String sender_phone = list.get("sender_phone");

        if(amount == null)
            throw new NullPointerException("amount can't be null");
        if(currency == null)
            throw new NullPointerException("currency can't be null");
        if(description == null)
            throw new NullPointerException("description can't be null");

        String sign_str = priv_key + amount + currency + pub_key;

        if(order_id != null)sign_str += order_id;
        if(type != null)sign_str += type;
        if(description != null) sign_str += description;
        if(result_url != null)sign_str += result_url;
        if(server_url != null)sign_str += server_url;
        if(first_name != null)sign_str += first_name;
        if(last_name != null)sign_str += last_name;
        if(middle_name != null)sign_str += middle_name;
        if(country_code != null)sign_str += country_code;
        if(city_name != null)sign_str += city_name;
        if(address != null)sign_str += address;
        if(postal_code != null)sign_str += postal_code;
        if(status != null)sign_str += status;
        if(transaction_id != null)sign_str += transaction_id;
        if(sender_phone != null)sign_str += sender_phone;

        return str_to_sign(sign_str);
    }

    public static boolean isValid(Map<String, String> params) {
        String signature = params.get("signature");
        if (signature == null) return false;

        String calcSignature = cnb_signature(params);
        return signature.equals(calcSignature);
    }

//    public void setProxy(String host, Integer port){
//        __PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
//    }
//
//    public void setProxy(String host, Integer port, Proxy.Type type){
//        __PROXY = new Proxy(type, new InetSocketAddress(host, port));
//    }
//
//
//    public void setProxyUser(String login, String password){
//        __PROXY_AUTH = new String(LiqPayUtil.base64_encode(new String(login + ":" + password).getBytes()));
//    }
//
//    public Proxy getProxy(){
//        return __PROXY;
//    }
//
//    public String getProxyUser(){
//        return __PROXY_AUTH;
//    }

    public static String str_to_sign(String str) {
        return LiqPayUtil.base64_encode( LiqPayUtil.sha1( str ) );
    }
}