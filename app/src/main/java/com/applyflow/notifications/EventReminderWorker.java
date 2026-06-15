package com.applyflow.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.applyflow.MainActivity;
import com.applyflow.R;
import com.applyflow.util.Constants;
import com.applyflow.util.DateUtils;

public class EventReminderWorker extends Worker {

    public EventReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data input = getInputData();
        int eventId = input.getInt(Constants.KEY_EVENT_ID, -1);
        int applicationId = input.getInt(Constants.KEY_APPLICATION_ID, -1);
        String eventType = input.getString(Constants.KEY_EVENT_TYPE);
        String companyName = input.getString(Constants.KEY_COMPANY_NAME);
        String eventDateTime = input.getString(Constants.KEY_EVENT_DATE_TIME);

        Context context = getApplicationContext();

        String title = Constants.EVENT_TYPE_FOLLOW_UP.equals(eventType)
                ? context.getString(R.string.notification_title_follow_up)
                : context.getString(R.string.notification_title_interview);

        String when = DateUtils.displayDateTime(eventDateTime);
        StringBuilder text = new StringBuilder();
        if (companyName != null && !companyName.isEmpty()) {
            text.append(companyName);
        }
        if (when != null) {
            if (text.length() > 0) {
                text.append(" • ");
            }
            text.append(when);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, Constants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text.toString()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        PendingIntent contentIntent = buildDetailPendingIntent(context, applicationId);
        if (contentIntent != null) {
            builder.setContentIntent(contentIntent);
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (manager.areNotificationsEnabled()) {
            int notificationId = eventId >= 0 ? eventId : (int) System.currentTimeMillis();
            manager.notify(notificationId, builder.build());
        }

        return Result.success();
    }

    private PendingIntent buildDetailPendingIntent(Context context, int applicationId) {
        if (applicationId < 0) {
            return null;
        }
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_APPLICATION_ID, applicationId);
        return new NavDeepLinkBuilder(context)
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.applicationDetailFragment)
                .setArguments(args)
                .createPendingIntent();
    }
}
