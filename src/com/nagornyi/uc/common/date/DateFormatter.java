package com.nagornyi.uc.common.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Nagornyi
 * Date: 5/28/14
 */
public class DateFormatter {
	public static final Locale UK_LOCALE = new Locale.Builder().setLanguageTag("uk").setRegion("UA").build();

	public static String format(Date date, Locale locale) {
		return new SimpleDateFormat("d MMMM, yyyy kk:mm", locale).format(date);
	}

	public static String defaultFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM, yyyy kk:mm", UK_LOCALE);
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));
		return formatter.format(date);
	}

    public static String defaultShortFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return formatter.format(date);
    }

    public static String defaultDayMonthFormat(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMMM", UK_LOCALE);
//        formatter.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        return formatter.format(date);
    }
}
