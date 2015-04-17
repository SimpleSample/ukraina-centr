package com.nagornyi.uc.currency;

import com.nagornyi.uc.appinfo.AppInfoManager;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public class CurrencyConverter {

    public static double fromBase(double basePriceValue) {
//        Currency currency = RequestContext.getCurrency();
//        if (currency.getCurrencyCode().equals("EUR")) {
//            return basePriceValue;
//        }
        double rate = AppInfoManager.getInstance().getCurrencyRate();
        return basePriceValue*rate;
    }

    public static double toBase(double priceValue) {
        double rate = AppInfoManager.getInstance().getCurrencyRate();
        return priceValue/rate;
    }
}
