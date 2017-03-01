package com.nagornyi.uc.common.liqpay;

import com.nagornyi.env.EnvVariablesStorage;
import org.apache.commons.lang3.StringUtils;

public class LiqPaySignatureBuilder {

    private StringBuilder result = new StringBuilder();

    private String orderId;
    private String type;
    private String description;
    private String resultUrl;
    private String serverUrl;
    private String firstName;
    private String lastName;
    private String middleName;
    private String countryCode;
    private String cityName;
    private String address;
    private String postalCode;
    private String status;
    private String transactionId;
    private String senderPhone;

    public LiqPaySignatureBuilder(String amount, String currency, String description) {
        assertHasParameter("amount", amount);
        assertHasParameter("currency", currency);
        assertHasParameter("description", description);

        String publicKey = EnvVariablesStorage.getValue(LiqPay.ENV_PARAMETER_GROUP, "publicKey");
        String privateKey = EnvVariablesStorage.getValue(LiqPay.ENV_PARAMETER_GROUP, "privateKey");

        result.append(privateKey).append(amount).append(currency).append(publicKey);

        this.description = description;
    }

    public LiqPaySignatureBuilder orderId(String orderId) {
        this.orderId = StringUtils.defaultString(orderId);
        return this;
    }

    public LiqPaySignatureBuilder type(String type) {
        this.type = StringUtils.defaultString(type);
        return this;
    }

    public LiqPaySignatureBuilder resultUrl(String resultUrl) {
        this.resultUrl = StringUtils.defaultString(resultUrl);
        return this;
    }

    public LiqPaySignatureBuilder serverUrl(String serverUrl) {
        this.serverUrl = StringUtils.defaultString(serverUrl);
        return this;
    }

    public LiqPaySignatureBuilder firstName(String firstName) {
        this.firstName = StringUtils.defaultString(firstName);
        return this;
    }

    public LiqPaySignatureBuilder lastName(String lastName) {
        this.lastName = StringUtils.defaultString(lastName);
        return this;
    }

    public LiqPaySignatureBuilder middleName(String middleName) {
        this.middleName = StringUtils.defaultString(middleName);
        return this;
    }

    public LiqPaySignatureBuilder countryCode(String countryCode) {
        this.countryCode = StringUtils.defaultString(countryCode);
        return this;
    }

    public LiqPaySignatureBuilder cityName(String cityName) {
        this.cityName = StringUtils.defaultString(cityName);
        return this;
    }

    public LiqPaySignatureBuilder address(String address) {
        this.address = StringUtils.defaultString(address);
        return this;
    }

    public LiqPaySignatureBuilder postalCode(String postalCode) {
        this.postalCode = StringUtils.defaultString(postalCode);
        return this;
    }

    public LiqPaySignatureBuilder status(String status) {
        this.status = StringUtils.defaultString(status);
        return this;
    }

    public LiqPaySignatureBuilder transactionId(String transactionId) {
        this.transactionId = StringUtils.defaultString(transactionId);
        return this;
    }

    public LiqPaySignatureBuilder senderPhone(String senderPhone) {
        this.senderPhone = StringUtils.defaultString(senderPhone);
        return this;
    }
    
    public String build() {
        return result.append(orderId).append(type).append(description).append(resultUrl).append(serverUrl)
                .append(firstName).append(lastName).append(middleName).append(countryCode).append(cityName).append(address)
                .append(postalCode).append(status).append(transactionId).append(senderPhone)
                .toString();
    }

    private void assertHasParameter(String parameter, String parameterName) {
        if (StringUtils.isEmpty(parameter)) {
            throw new IllegalArgumentException(parameterName + " parameter should be specified");
        }
    }
}
