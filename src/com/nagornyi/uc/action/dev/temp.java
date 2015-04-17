package com.nagornyi.uc.action.dev;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * @author Nagorny
 *         Date: 17.05.14
 */
public class temp {
    public static void main(String[] args) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(1400661000000L);
        System.out.println(getMonthForInt(c.get(Calendar.MONTH)) + " " + c.get(Calendar.DAY_OF_MONTH) + "; " + getWeekForInt(c.get(Calendar.DAY_OF_WEEK)) +"; " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        int targetDayOfWeek = 4;
        if (c.get(Calendar.DAY_OF_WEEK) > targetDayOfWeek) {
            int leftDays = c.get(Calendar.DAY_OF_WEEK) - targetDayOfWeek;
            c.add(Calendar.DAY_OF_MONTH, 7 - leftDays);
        }

        System.out.println(getMonthForInt(c.get(Calendar.MONTH)) + " " + c.get(Calendar.DAY_OF_MONTH) + "; " + getWeekForInt(c.get(Calendar.DAY_OF_WEEK)) +"; " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));
        System.out.println(("ag9zfnVrcmFpbmEtY2VudHJyIQsSB0NvdW50cnkiA3VrcgwLEgRDaXR5GICAgICA5LEJDA".hashCode() + "ag9zfnVrcmFpbmEtY2VudHJyIAsSB0NvdW50cnkiAml0DAsSBENpdHkYgICAgIDksQgM".hashCode())*31);
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
