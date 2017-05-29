package com.epam.androidlab.androidtrainingtask8;

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

import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;

public class MyAlarmReceiver extends BroadcastReceiver {
    private static MyAlarm alarm;
    private static Thread playThread;

    @Override
    public void onReceive(Context context, Intent intent) {
        getAlarmForName(intent.getStringExtra("ALARM_NAME"));

        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if( myKM.inKeyguardRestrictedInputMode()) {
            System.out.println("isLocked");
            turnOnScreen();
            showAlarm(context);

            new Thread(() -> playAlarmRingtone()).start();

        } else {
            System.out.println("isUnlocked");
            sendNotification(context);
        }
    }

    public static Thread getPlayThread() {
        return playThread;
    }

    private void playAlarmRingtone() {
        while (true) {
            if (!alarm.getAlarmRingtone().isPlaying()) {
                alarm.getAlarmRingtone().play();
            }
        }
    }

    private void getAlarmForName(String name) {
        for (int i = 0; i < MainActivity.getAlarms().size(); i++) {
            if (MainActivity.getAlarms().get(i).getAlarmName().equalsIgnoreCase(name)) {
                alarm = MainActivity.getAlarms().get(i);
            }
        }
    }

    private void showAlarm(Context context) {
        Intent i = new Intent();
        i.setClassName("com.epam.androidlab.androidtrainingtask8",
                "com.epam.androidlab.androidtrainingtask8.WakeupActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
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

    /*private Ringtone getRingtoneForName(Context context, String name) {
        Ringtone ringtone = null;
        for (int i = 0; i < MainActivity.getRingtones().size(); i++) {
            if (name.equals(MainActivity.getRingtones().get(i).getTitle(context))) {
                ringtone = MainActivity.getRingtones().get(i);
            }
        }
        return ringtone;
    }*/
}
