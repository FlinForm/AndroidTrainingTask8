package com.epam.androidlab.androidtrainingtask8.alarmmodel;

import android.media.Ringtone;

import java.io.Serializable;
import java.util.Calendar;

public class MyAlarm implements Serializable {
    private Ringtone alarmRingtone;
    private RepeatLoop repeatLoop;
    private String alarmName;
    private int hours;
    private int minutes;
    private boolean isSwitchedOn;


    public MyAlarm(Ringtone alarmRingtone,
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

    public Ringtone getAlarmRingtone() {
        return alarmRingtone;
    }
}
