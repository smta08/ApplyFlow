package com.applyflow.notifications;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;

import java.util.concurrent.TimeUnit;

public final class NotificationHelper {

    private NotificationHelper() {
    }

    private static String workTag(int eventId) {
        return Constants.WORK_TAG_PREFIX + eventId;
    }

    public static void scheduleEventReminder(Context context,
                                             int eventId,
                                             int applicationId,
                                             String eventType,
                                             String companyName,
                                             String eventDateTime) {
        long eventDateTimeMillis = DateUtils.dateTimeToMillis(eventDateTime);
        if (eventDateTimeMillis < 0) {
            return;
        }
        long delayMs = eventDateTimeMillis - System.currentTimeMillis();
        if (delayMs < 0) {
            return;
        }

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(EventReminderWorker.class)
                .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                .addTag(workTag(eventId))
                .setInputData(new Data.Builder()
                        .putInt(Constants.KEY_EVENT_ID, eventId)
                        .putInt(Constants.KEY_APPLICATION_ID, applicationId)
                        .putString(Constants.KEY_EVENT_TYPE, eventType)
                        .putString(Constants.KEY_COMPANY_NAME, companyName)
                        .putString(Constants.KEY_EVENT_DATE_TIME, eventDateTime)
                        .build())
                .build();

        WorkManager.getInstance(context).enqueue(request);
    }

    public static void cancelEventReminder(Context context, int eventId) {
        WorkManager.getInstance(context).cancelAllWorkByTag(workTag(eventId));
    }
}
