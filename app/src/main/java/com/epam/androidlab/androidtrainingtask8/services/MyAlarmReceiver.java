package com.epam.androidlab.androidtrainingtask8.services;

import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.epam.androidlab.androidtrainingtask8.R;
import com.epam.androidlab.androidtrainingtask8.activities.MainActivity;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;

public class MyAlarmReceiver extends BroadcastReceiver {
    private static MyAlarm alarm;
    private static Ringtone ringtone;
    private static NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        getAlarmForName(intent.getStringExtra("ALARM_NAME"));
        ringtone =  getRingtoneForName(alarm.getAlarmRingtone(), context);
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if(myKM.inKeyguardRestrictedInputMode()) {
            turnOnScreen();
            showAlarm(context);
           ringtone.play();
        } else {
            sendNotification(context);
            ringtone.play();
        }
    }

    public static MyAlarm getAlarm() {
        return alarm;
    }

    private void getAlarmForName(String name) {
        for (int i = 0; i < MainActivity.getAlarms().size(); i++) {
            if (MainActivity.getAlarms().get(i).getAlarmName().equalsIgnoreCase(name)) {
                alarm = MainActivity.getAlarms().get(i);
            }
        }
    }

    private void showAlarm(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.epam.androidlab.androidtrainingtask8",
                "com.epam.androidlab.androidtrainingtask8.activities.WakeupActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void turnOnScreen() {
        PowerManager.WakeLock screenLock = ((PowerManager) MainActivity.activity
                .getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();
        screenLock.release();
    }

    private void sendNotification(Context context) {
        int id = 12354;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(context.getString(R.string.notifTitle))
                        .setContentText(context.getString(R.string.notifText));

        Intent intent = new Intent();
        intent.setClassName("com.epam.androidlab.androidtrainingtask8",
                "com.epam.androidlab.androidtrainingtask8.activities.WakeupActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);

        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    public static Ringtone getRingtone() {
        return ringtone;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private Ringtone getRingtoneForName(String name, Context context) {
        Ringtone ringtone = null;
        for (int i = 0; i < MainActivity.getRingtones().size(); i++) {
            if (name.equals(MainActivity.getRingtones().get(i).getTitle(context))) {
                ringtone = MainActivity.getRingtones().get(i);
            }
        }
        return ringtone;
    }
}
