package com.applyflow.util;

public final class Constants {

    private Constants() {
    }

    public static final String DATABASE_NAME = "applyflow.db";

    public static final String NOTIFICATION_CHANNEL_ID = "applyflow_reminders";
    public static final String WORK_TAG_PREFIX = "event_";

    public static final String KEY_EVENT_ID = "event_id";
    public static final String KEY_EVENT_TYPE = "event_type";
    public static final String KEY_COMPANY_NAME = "company_name";
    public static final String KEY_EVENT_DATE_TIME = "event_date_time";
    public static final String KEY_APPLICATION_ID = "application_id";

    public static final String ARG_APPLICATION_ID = "applicationId";
    public static final String ARG_EVENT_ID = "eventId";

    public static final int NEW_ID = -1;

    public static final String STATUS_APPLIED = "APPLIED";
    public static final String STATUS_PHONE_SCREEN = "PHONE_SCREEN";
    public static final String STATUS_INTERVIEW = "INTERVIEW";
    public static final String STATUS_FINAL_ROUND = "FINAL_ROUND";
    public static final String STATUS_OFFER_RECEIVED = "OFFER_RECEIVED";
    public static final String STATUS_REJECTED = "REJECTED";

    public static final String[] STATUSES = {
            STATUS_APPLIED,
            STATUS_PHONE_SCREEN,
            STATUS_INTERVIEW,
            STATUS_FINAL_ROUND,
            STATUS_OFFER_RECEIVED,
            STATUS_REJECTED
    };

    public static final String EVENT_TYPE_INTERVIEW = "INTERVIEW";
    public static final String EVENT_TYPE_FOLLOW_UP = "FOLLOW_UP";

    public static final String[] EVENT_TYPES = {
            EVENT_TYPE_INTERVIEW,
            EVENT_TYPE_FOLLOW_UP
    };

    public static final int UPCOMING_WINDOW_DAYS = 7;
}
