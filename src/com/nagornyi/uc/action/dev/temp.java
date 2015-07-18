package com.nagornyi.uc.action.dev;

import com.nagornyi.uc.common.liqpay.LiqPay;
import com.nagornyi.uc.util.CurrencyUtil;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Nagorny
 *         Date: 17.05.14
 */
public class temp {
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(1429995600000L);

        System.out.println(new SimpleDateFormat().format(new Date(1429995600000L)));
        c.add(Calendar.DAY_OF_MONTH, 7);
        System.out.println("Adding 7 days");
        System.out.println(getMonthForInt(c.get(Calendar.MONTH)) + " " + c.get(Calendar.DAY_OF_MONTH) + "; " + getWeekForInt(c.get(Calendar.DAY_OF_WEEK)) +"; " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        int targetDayOfWeek = 4;
        if (c.get(Calendar.DAY_OF_WEEK) > targetDayOfWeek) {
            int leftDays = c.get(Calendar.DAY_OF_WEEK) - targetDayOfWeek;
            c.add(Calendar.DAY_OF_MONTH, 7 - leftDays);
        }

        System.out.println(getMonthForInt(c.get(Calendar.MONTH)) + " " + c.get(Calendar.DAY_OF_MONTH) + "; " + getWeekForInt(c.get(Calendar.DAY_OF_WEEK)) +"; " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        System.out.println(("ag9zfnVrcmFpbmEtY2VudHJyIQsSB0NvdW50cnkiA3VrcgwLEgRDaXR5GICAgICA5LEJDA".hashCode() + "ag9zfnVrcmFpbmEtY2VudHJyIAsSB0NvdW50cnkiAml0DAsSBENpdHkYgICAgIDksQgM".hashCode())*31);

        System.out.println("Currency util: " + CurrencyUtil.round(24.312323, 2));

        String test = "MyTest"+ LiqPay.UC_KEY;

        System.out.println(test.substring(0, test.indexOf(LiqPay.UC_KEY)));

        System.out.println(RandomStringUtils.random(8, true, true));
    }

    static String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    static String getWeekForInt(int num) {
        String weekday = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] weekdays = dfs.getShortWeekdays();
        if (num >= 1 && num <= 7 ) {
            weekday = weekdays[num];
        }
        return weekday;
    }
}
