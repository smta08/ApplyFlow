package com.applyflow.util;

import android.content.Context;

import com.applyflow.R;

public final class PriorityUtils {

    private PriorityUtils() {
    }

    public static String priorityLabel(Context context, int priority) {
        switch (priority) {
            case Constants.PRIORITY_HIGH:
                return context.getString(R.string.priority_high);
            case Constants.PRIORITY_TOP:
                return context.getString(R.string.priority_top);
            default:
                return context.getString(R.string.priority_normal);
        }
    }

    public static String[] allPriorityLabels(Context context) {
        String[] labels = new String[Constants.PRIORITIES.length];
        for (int i = 0; i < Constants.PRIORITIES.length; i++) {
            labels[i] = priorityLabel(context, Constants.PRIORITIES[i]);
        }
        return labels;
    }

    public static int indexOf(int priority) {
        for (int i = 0; i < Constants.PRIORITIES.length; i++) {
            if (Constants.PRIORITIES[i] == priority) {
                return i;
            }
        }
        return 0;
    }
}
