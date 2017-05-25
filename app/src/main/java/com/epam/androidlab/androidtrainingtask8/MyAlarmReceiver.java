package com.epam.androidlab.androidtrainingtask8;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class MyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    private void sendNotification(Context context) {
        int id = 12354;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(context.getString(R.string.notifTitle))
                        .setContentText(context.getString(R.string.notifText));

        Intent intent = new Intent();
        intent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    private Ringtone getRingtoneForName(Context context, String name) {
        Ringtone ringtone = null;
        for (int i = 0; i < MainActivity.getRingtones().size(); i++) {
            if (name.equals(MainActivity.getRingtones().get(i).getTitle(context))) {
                ringtone = MainActivity.getRingtones().get(i);
            }
        }
        return ringtone;
    }
}
