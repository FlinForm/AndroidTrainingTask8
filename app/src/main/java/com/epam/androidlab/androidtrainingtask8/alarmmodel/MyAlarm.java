package com.epam.androidlab.androidtrainingtask8.alarmmodel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.epam.androidlab.androidtrainingtask8.MyAlarmReceiver;

import java.io.Serializable;
import java.util.Calendar;

public class MyAlarm implements Serializable {
    private final String alarmRingtone;
    private final RepeatLoop repeatLoop;
    private final String alarmName;
    private final int hours;
    private final int minutes;
    private boolean isSwitchedOn;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public MyAlarm(String alarmRingtone,
                   String alarmName,
                   RepeatLoop repeatLoop,
                   int hours,
                   int minutes,
                   boolean isSwitchedOn) {
        this.alarmRingtone = alarmRingtone;
        this.alarmName = alarmName;
        this.repeatLoop = repeatLoop;
        this.hours = hours;
        this.minutes = minutes;
        this.isSwitchedOn = isSwitchedOn;
    }

    public String getTime() {
        String formattedHours = Integer.toString(hours);
        String formattedMinutes = Integer.toString(minutes);
        if (hours < 10) {
            formattedHours = "0" + Integer.toString(hours);
        }
        if (minutes < 10) {
            formattedMinutes = "0" + Integer.toString(minutes);
        }
        return formattedHours + ":" + formattedMinutes;
    }

    public long getTimeInMillis() {
        Calendar calendar; calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTimeInMillis();
    }

    public String getAlarmName() {
        return alarmName;
    }

    public String getAlarmRingtone() {
        return alarmRingtone;
    }

    public RepeatLoop getRepeatLoop() {
        return repeatLoop;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public boolean isSwitchedOn() {
        return isSwitchedOn;
    }

    public void setSwitchedOn(boolean switchedOn) {
        isSwitchedOn = switchedOn;
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void startAlarm(Context context, long time) {
        if (!isSwitchedOn) {
            return;
        }
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyAlarmReceiver.class);
        intent.putExtra("ALARM_NAME", alarmName);
        pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        switch (repeatLoop) {
            case ONE_TIME:
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                break;
            case EVERY_DAY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        time,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent);
                break;
        }
    }

    public void sleepForFiveMinutes(Context context) {
        alarmManager.cancel(pendingIntent);
        startAlarm(context, System.currentTimeMillis() + 300_000);
    }
}