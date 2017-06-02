package com.epam.androidlab.androidtrainingtask8.alarmmodel;

import android.media.Ringtone;

import com.epam.androidlab.androidtrainingtask8.MainActivity;

import java.io.Serializable;
import java.util.Calendar;

public class MyAlarm implements Serializable {
    private final String alarmRingtone;
    private final RepeatLoop repeatLoop;
    private final String alarmName;
    private final int hours;
    private final int minutes;
    private final boolean isSwitchedOn;


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
        return Integer.toString(hours) + ":" + Integer.toString(minutes);
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

}
