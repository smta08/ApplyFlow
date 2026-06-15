package com.applyflow.util;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    private DateUtils() {
    }

    private static final String ISO_DATE = "yyyy-MM-dd";
    private static final String ISO_DATE_TIME = "yyyy-MM-dd'T'HH:mm";
    private static final String ISO_DATE_TIME_FULL = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DISPLAY_DATE = "MMM d, yyyy";
    private static final String DISPLAY_DATE_TIME = "MMM d, yyyy  h:mm a";

    private static SimpleDateFormat fmt(String pattern) {
        return new SimpleDateFormat(pattern, Locale.US);
    }

    public static String nowCreatedAt() {
        return fmt(ISO_DATE_TIME_FULL).format(new Date());
    }

    public static String formatDate(Calendar calendar) {
        return fmt(ISO_DATE).format(calendar.getTime());
    }

    public static String formatDateTime(Calendar calendar) {
        return fmt(ISO_DATE_TIME).format(calendar.getTime());
    }

    public static String nowDateTime() {
        return fmt(ISO_DATE_TIME).format(new Date());
    }

    public static String dateTimePlusDays(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        return fmt(ISO_DATE_TIME).format(c.getTime());
    }

    public static String startOfTodayDateTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        return fmt(ISO_DATE_TIME).format(c.getTime());
    }

    public static String dateTimePlusDaysEndOfDay(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        return fmt(ISO_DATE_TIME).format(c.getTime());
    }

    public static String dateMinusDays(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -days);
        return fmt(ISO_DATE).format(c.getTime());
    }

    public static int daysBetweenDateAndToday(@Nullable String isoDate) {
        Calendar from = parseDate(isoDate);
        if (from == null) {
            return -1;
        }
        Calendar today = Calendar.getInstance();
        long fromDay = atMidnight(from).getTimeInMillis();
        long todayDay = atMidnight(today).getTimeInMillis();
        return (int) ((todayDay - fromDay) / (24L * 60L * 60L * 1000L));
    }

    @Nullable
    public static String monthKey(@Nullable String isoDate) {
        Calendar c = parseDate(isoDate);
        return c == null ? null : new SimpleDateFormat("yyyy-MM", Locale.US).format(c.getTime());
    }

    private static Calendar atMidnight(Calendar c) {
        Calendar copy = (Calendar) c.clone();
        copy.set(Calendar.HOUR_OF_DAY, 0);
        copy.set(Calendar.MINUTE, 0);
        copy.set(Calendar.SECOND, 0);
        copy.set(Calendar.MILLISECOND, 0);
        return copy;
    }

    @Nullable
    public static Calendar parseDate(@Nullable String iso) {
        if (iso == null || iso.trim().isEmpty()) {
            return null;
        }
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(fmt(ISO_DATE).parse(iso));
            return c;
        } catch (ParseException e) {
            return null;
        }
    }

    @Nullable
    public static Calendar parseDateTime(@Nullable String iso) {
        if (iso == null || iso.trim().isEmpty()) {
            return null;
        }
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(fmt(ISO_DATE_TIME).parse(iso));
            return c;
        } catch (ParseException e) {
            return null;
        }
    }

    public static long dateTimeToMillis(@Nullable String iso) {
        Calendar c = parseDateTime(iso);
        return c == null ? -1L : c.getTimeInMillis();
    }

    @Nullable
    public static String displayDate(@Nullable String iso) {
        Calendar c = parseDate(iso);
        return c == null ? null : fmt(DISPLAY_DATE).format(c.getTime());
    }

    @Nullable
    public static String displayDateTime(@Nullable String iso) {
        Calendar c = parseDateTime(iso);
        return c == null ? null : fmt(DISPLAY_DATE_TIME).format(c.getTime());
    }
}
