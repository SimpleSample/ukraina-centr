package com.nagornyi.uc.common.liqpay;

import java.util.HashMap;
import java.util.Map;

public class LiqPay_V3Test {

    public static void main(String[] args) {
        String data = "eyJwYXltZW50X2lkIjo3ODU5MDkyMCwidHJhbnNhY3Rpb25faWQiOjc4NTkwOTIwLCJzdGF0dXMiOiJzdWNjZXNzIiwidmVyc2lvbiI6MywidHlwZSI6ImJ1eSIsInB1YmxpY19rZXkiOiJpMzA1ODc3MTQxODgiLCJhY3FfaWQiOjQxNDk2Mywib3JkZXJfaWQiOiIxNDQ0NDk2NjQxMTk3QDsg0K/QvdCwINCU0LjQsdC10L3QutC+LCBDaGVya2FzeSAtIFBlcnVnaWEsIDE1LjEwLjE1IiwibGlxcGF5X29yZGVyX2lkIjoiODgzMjk3N3UxNDQ0NDk3MzI4NzI4NDM5IiwiZGVzY3JpcHRpb24iOiLQntC/0LvQsNGC0LAg0LrQstC40YLQutGW0LIg0L3QsCDRgdCw0LnRgtGWIHd3dy51a3JhaW5hLWNlbnRyLmNvbSIsInNlbmRlcl9waG9uZSI6IjM4MDY3NzY3NTk5NCIsImFtb3VudCI6MTA4LjAsImN1cnJlbmN5IjoiRVVSIiwic2VuZGVyX2NvbW1pc3Npb24iOjAuMCwicmVjZWl2ZXJfY29tbWlzc2lvbiI6Mi45NywiYWdlbnRfY29tbWlzc2lvbiI6MC4wLCJhbW91bnRfZGViaXQiOjI4MTkuODUsImFtb3VudF9jcmVkaXQiOjI4MTkuODUsImNvbW1pc3Npb25fZGViaXQiOjAuMCwiY29tbWlzc2lvbl9jcmVkaXQiOjc3LjU1LCJjdXJyZW5jeV9kZWJpdCI6IlVBSCIsImN1cnJlbmN5X2NyZWRpdCI6IlVBSCIsInNlbmRlcl9ib251cyI6MC4wLCJhbW91bnRfYm9udXMiOjAuMH0=";
        String signature = "1c5K5ZPYBeYNDTBGYg97bqApC3s=";

        Map<String, String> params = new HashMap<>();
        params.put("data", data);
        params.put("signature", signature);
        System.out.println("valid? " + LiqPay_v3.isValid(params));
        System.out.println("data json " + LiqPay_v3.parseParams(params));
        System.out.println("Contains order_id? " + LiqPay_v3.parseParams(params).containsKey("order_id"));
    }
}
