package com.nagornyi.uc.util;

import com.nagornyi.uc.common.DateFormatter;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Artem on 26.04.2015.
 */
public class DateUtil {

    public static int getDatesDelta(Calendar start, Calendar end) {
        return (end.get(Calendar.MONTH) - start.get(Calendar.MONTH))*30 + (end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH));
    }

    public static DatePeriod getActualDatePeriodForRoute (Date closestStartDate, DatePeriod routeDatePeriod) {
        Calendar cTargetDate = Calendar.getInstance();
        cTargetDate.setTime(closestStartDate);
        cTargetDate.set(Calendar.MILLISECOND, 0);

        Calendar cStartDate = Calendar.getInstance();
        cStartDate.setTime(routeDatePeriod.getStartDate());
        cStartDate.set(Calendar.MILLISECOND, 0);

        Calendar cEndDate = Calendar.getInstance();
        cEndDate.setTime(routeDatePeriod.getEndDate());
        cEndDate.set(Calendar.MILLISECOND, 0);

        cTargetDate.set(Calendar.HOUR_OF_DAY, cStartDate.get(Calendar.HOUR_OF_DAY));
        cTargetDate.set(Calendar.MINUTE, cStartDate.get(Calendar.MINUTE));
        cTargetDate.set(Calendar.SECOND, cStartDate.get(Calendar.SECOND));

        int leftDays;
        if (cTargetDate.get(Calendar.DAY_OF_WEEK) > cStartDate.get(Calendar.DAY_OF_WEEK)) {
            leftDays = 7 - (cTargetDate.get(Calendar.DAY_OF_WEEK) -  cStartDate.get(Calendar.DAY_OF_WEEK));
        } else {
            leftDays = cStartDate.get(Calendar.DAY_OF_WEEK) - cTargetDate.get(Calendar.DAY_OF_WEEK);
        }
        cTargetDate.add(Calendar.DAY_OF_MONTH, leftDays);


        int tripDurationHours = (cEndDate.get(Calendar.DAY_OF_MONTH) - cStartDate.get(Calendar.DAY_OF_MONTH))*24 +
                (cEndDate.get(Calendar.HOUR_OF_DAY) - cStartDate.get(Calendar.HOUR_OF_DAY));
        int tripDurationMinutes = cEndDate.get(Calendar.MINUTE) - cStartDate.get(Calendar.MINUTE);
        if (tripDurationMinutes < 0) {
            tripDurationHours--;
            tripDurationMinutes = 60 + tripDurationMinutes;
        }

        Calendar cTargetEndDate = (Calendar)cTargetDate.clone();

        cTargetEndDate.add(Calendar.HOUR_OF_DAY, tripDurationHours);
        cTargetEndDate.add(Calendar.MINUTE, tripDurationMinutes);
        return new DatePeriod(cTargetDate.getTime(), cTargetEndDate.getTime());
    }



    public static class DatePeriod {
        private Date startDate;
        private Date endDate;

        public DatePeriod(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        @Override
        public String toString() {
            return DateFormatter.defaultFormat(startDate) +" - " + DateFormatter.defaultFormat(endDate);
        }
    }
}
