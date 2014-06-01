package com.bearcat.remembear.remembear;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Alarm triggered according to reminder broadcast by {@link ReminderActivity}.
 */
public class ReminderAlarm extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get intent info
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence reminderName = intent.getStringExtra(ReminderActivity.REMINDER_NAME);
        CharSequence reminderDescription = intent.getStringExtra(ReminderActivity.REMINDER_DESCRIPTION);

        // Launch notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        notification = new Notification(R.drawable.ic_launcher, "Notification", System.currentTimeMillis());
        notification.setLatestEventInfo(context, reminderName, reminderDescription, contentIntent);
        mNotificationManager.notify(Integer.parseInt(intent.getExtras().get(ReminderActivity.REMINDER_NOTIFY_COUNT).toString()), notification);

        // Display reminder
        String reminderDisplay = reminderName + " (" + reminderDescription + ") ";
        Toast.makeText(context, reminderDisplay, Toast.LENGTH_LONG).show();
    }

}

