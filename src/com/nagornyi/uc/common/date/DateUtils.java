package com.nagornyi.uc.common.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtils {
    private DateUtils() {

    }

    public static void main(String[] args) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        SimpleDateFormat uaFormatter = new SimpleDateFormat();
        uaFormatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        System.out.println("Current time " + formatter.format(new Date()));
        System.out.println("Summer time " + formatter.format(getSummerTimeBorder()));
        System.out.println("Winter time " + formatter.format(getWinterTimeBorder()));
        System.out.println("Ukraine time " + formatter.format(getUkraineDateNow()));
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), Calendar.APRIL, 3, 12, 0, 0);
        System.out.println("Ukraine time (my BD) " + formatter.format(getUkraineDateFor(c.getTime())));

    }

    public static Date getUkraineDateFor(Date serverDate) {
        Calendar result = Calendar.getInstance();
        result.setTime(serverDate);
        //ukrainian winter time
        result.add(Calendar.HOUR, 2);
        Long resultMiliss = result.getTimeInMillis();

        //checking that now is summer time, if true - adding one more hour
        if (resultMiliss > getSummerTimeBorder().getTime() && resultMiliss < getWinterTimeBorder().getTime()) {
            result.add(Calendar.HOUR, 1);
        }
        return result.getTime();
    }

    public static Date getUkraineDateNow() {
        return getUkraineDateFor(new Date());
    }

    // for Ukraine - last sunday of march at 3 a.m.
    private static Date getSummerTimeBorder() {
        Calendar result = Calendar.getInstance();
        result.set(result.get(Calendar.YEAR), Calendar.MARCH, 31);
        adjustToSunday(result);
        result.set(Calendar.HOUR_OF_DAY, 2); // can't set '3', becomes '4'
        result.set(Calendar.MINUTE, 59);
        result.set(Calendar.SECOND, 59);
        return result.getTime();
    }

    // for Ukraine - last sunday of october at 4 a.m.
    private static Date getWinterTimeBorder() {
        Calendar result = Calendar.getInstance();
        result.set(result.get(Calendar.YEAR), Calendar.OCTOBER, 31);
        adjustToSunday(result);
        result.set(Calendar.HOUR_OF_DAY, 4);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        return result.getTime();
    }

    private static void adjustToSunday(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        while (dayOfWeek != 1) { //sunday is the first day
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            dayOfWeek--;
            if (dayOfWeek == 0) {
                dayOfWeek = 7;
            }
        }
    }
}
