package com.nagornyi.uc.util;

/**
 * @author Nagornyi
 * Date: 29.06.14
 */
public final class CurrencyUtil {

    public static double round(double value, int places) {
        double factor = places < 0 ? 1/Math.pow(10, Math.abs(places)) : Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
