package com.applyflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;

import org.junit.Test;

import java.util.Calendar;

public class DateAndConstantsTest {

    @Test
    public void allSixStatusesPresent() {
        assertEquals(6, Constants.STATUSES.length);
        assertEquals(Constants.STATUS_APPLIED, Constants.STATUSES[0]);
        assertEquals(Constants.STATUS_REJECTED, Constants.STATUSES[5]);
    }

    @Test
    public void twoEventTypesPresent() {
        assertEquals(2, Constants.EVENT_TYPES.length);
    }

    @Test
    public void parsesValidDateTimeToPositiveMillis() {
        assertTrue(DateUtils.dateTimeToMillis("2026-06-14T09:30") > 0);
    }

    @Test
    public void returnsNegativeOneForNullDateTime() {
        assertEquals(-1L, DateUtils.dateTimeToMillis(null));
    }

    @Test
    public void displayDateIsNullForNullInput() {
        assertNull(DateUtils.displayDate(null));
    }

    @Test
    public void startOfTodayIsMidnight() {
        assertTrue(DateUtils.startOfTodayDateTime().endsWith("T00:00"));
    }

    @Test
    public void endOfWindowIsEndOfDay() {
        assertTrue(DateUtils.dateTimePlusDaysEndOfDay(7).endsWith("T23:59"));
    }

    @Test
    public void eventEarlierTodayIsInsideUpcomingWindow() {
        String start = DateUtils.startOfTodayDateTime();
        String end = DateUtils.dateTimePlusDaysEndOfDay(Constants.UPCOMING_WINDOW_DAYS);
        String eventToday8am = start.substring(0, 10) + "T08:00";
        assertTrue(eventToday8am.compareTo(start) >= 0);
        assertTrue(eventToday8am.compareTo(end) <= 0);
    }

    @Test
    public void dateMinusDaysMatchesDaysBetween() {
        assertEquals(5, DateUtils.daysBetweenDateAndToday(DateUtils.dateMinusDays(5)));
    }

    @Test
    public void daysBetweenTodayIsZero() {
        String today = DateUtils.formatDate(Calendar.getInstance());
        assertEquals(0, DateUtils.daysBetweenDateAndToday(today));
    }

    @Test
    public void monthKeyHasYearMonthFormat() {
        assertEquals("2026-06", DateUtils.monthKey("2026-06-14"));
        assertNull(DateUtils.monthKey(null));
    }
}
