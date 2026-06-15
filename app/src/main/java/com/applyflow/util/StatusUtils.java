package com.applyflow.util;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.applyflow.R;

public final class StatusUtils {

    private StatusUtils() {
    }

    public static String statusLabel(Context context, String status) {
        return context.getString(statusLabelRes(status));
    }

    private static int statusLabelRes(String status) {
        if (status == null) {
            return R.string.status_applied;
        }
        switch (status) {
            case Constants.STATUS_PHONE_SCREEN:
                return R.string.status_phone_screen;
            case Constants.STATUS_INTERVIEW:
                return R.string.status_interview;
            case Constants.STATUS_FINAL_ROUND:
                return R.string.status_final_round;
            case Constants.STATUS_OFFER_RECEIVED:
                return R.string.status_offer_received;
            case Constants.STATUS_REJECTED:
                return R.string.status_rejected;
            default:
                return R.string.status_applied;
        }
    }

    public static int statusColor(Context context, String status) {
        return ContextCompat.getColor(context, statusColorRes(status));
    }

    private static int statusColorRes(String status) {
        if (status == null) {
            return R.color.status_applied;
        }
        switch (status) {
            case Constants.STATUS_PHONE_SCREEN:
                return R.color.status_phone_screen;
            case Constants.STATUS_INTERVIEW:
                return R.color.status_interview;
            case Constants.STATUS_FINAL_ROUND:
                return R.color.status_final_round;
            case Constants.STATUS_OFFER_RECEIVED:
                return R.color.status_offer_received;
            case Constants.STATUS_REJECTED:
                return R.color.status_rejected;
            default:
                return R.color.status_applied;
        }
    }

    public static String eventTypeLabel(Context context, String type) {
        if (Constants.EVENT_TYPE_FOLLOW_UP.equals(type)) {
            return context.getString(R.string.event_type_follow_up);
        }
        return context.getString(R.string.event_type_interview);
    }

    public static String[] allStatusLabels(Context context) {
        String[] labels = new String[Constants.STATUSES.length];
        for (int i = 0; i < Constants.STATUSES.length; i++) {
            labels[i] = statusLabel(context, Constants.STATUSES[i]);
        }
        return labels;
    }

    public static String[] allEventTypeLabels(Context context) {
        String[] labels = new String[Constants.EVENT_TYPES.length];
        for (int i = 0; i < Constants.EVENT_TYPES.length; i++) {
            labels[i] = eventTypeLabel(context, Constants.EVENT_TYPES[i]);
        }
        return labels;
    }

    public static int statusIndex(String status) {
        for (int i = 0; i < Constants.STATUSES.length; i++) {
            if (Constants.STATUSES[i].equals(status)) {
                return i;
            }
        }
        return 0;
    }

    public static int eventTypeIndex(String type) {
        for (int i = 0; i < Constants.EVENT_TYPES.length; i++) {
            if (Constants.EVENT_TYPES[i].equals(type)) {
                return i;
            }
        }
        return 0;
    }
}
