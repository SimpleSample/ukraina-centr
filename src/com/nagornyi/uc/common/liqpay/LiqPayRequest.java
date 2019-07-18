package com.nagornyi.uc.common.liqpay;

public class LiqPayRequest {
    private String data;
    private String signature;

    public LiqPayRequest(String data, String signature) {
        this.data = data;
        this.signature = signature;
    }

    public String getData() {
        return data;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "LiqPayRequest{" +
                "data='" + data + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
