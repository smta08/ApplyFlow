package com.applyflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;

import org.junit.Test;

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
}
