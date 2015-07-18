package com.nagornyi.test.action;

import com.nagornyi.uc.util.CurrencyUtil;
import com.nagornyi.uc.util.DateUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by Artem on 06.05.2015.
 */
public class UtilsTests {

    @BeforeClass
    public static void setUpTests() {
        System.out.println("UtilsTests setup");
    }

    @Test
    public void dateUtilTest() {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Long startDate = 1431361800000L; //11.05.2015 12:30
        Long endDate = 1436631600000L; //11.07.2015 12:20

        c1.setTimeInMillis(startDate);
        c2.setTimeInMillis(endDate);
        int daysCount = DateUtil.getDaysDelta(c1, c2);
        assertEquals(60, daysCount);

        startDate = 1359912000456L; //3.02.2013 12:20
        endDate = 1360129800123L; //6.02.2013 0:50

        Long closestDate = 1430967600000L; //6.05.2015 23:00

        DateUtil.DatePeriod result = DateUtil.getActualDatePeriodForRoute(new Date(closestDate), new DateUtil.DatePeriod(new Date(startDate), new Date(endDate)));
        Long actualStartDate = result.getStartDate().getTime();
        Long actualEndDate = result.getEndDate().getTime();

        Long expectedStartDate = 1431274800000L; //10.05.2015 12:20
        Long expectedEndDate = 1431492600000L; //13.05.2015 0:50

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        //TODO fixme
//        assertEquals("start date is wrong - " + formatter.format(result.getStartDate()), expectedStartDate, actualStartDate);
//        assertEquals("end date is wrong - " + formatter.format(result.getEndDate()), expectedEndDate, actualEndDate);
    }

    @Test
    public void currencyUtilTest() {
        double price = 24.312323;
        double actualPrice = CurrencyUtil.round(price, 2);
        assertEquals(24.31, actualPrice, 1e-15 /*delta*/);
    }
}
