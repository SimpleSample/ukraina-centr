package com.nagornyi.uc.appinfo;

import com.nagornyi.uc.util.CurrencyUtil;

/**
 * @author Nagornyi
 * Date: 08.06.14
 */
public class AppInfo {
    private double currencyRate = 16.00;

    public double getCurrencyRate() {
        return currencyRate;
    }

    void setCurrencyRate(double currencyRate) {
        this.currencyRate = CurrencyUtil.round(currencyRate, 2);
    }
}
