package com.epam.androidlab.androidtrainingtask8.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.epam.androidlab.androidtrainingtask8.R;
import com.epam.androidlab.androidtrainingtask8.services.MyAlarmReceiver;

/**
 * This activity will be launched, when AlarmManager fired
 */

public class WakeupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_wakeup);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View stopAlarmButton = findViewById(R.id.stopButton);
        stopAlarmButton.setOnClickListener(event -> stopPlaying());

        View waitButton = findViewById(R.id.waitButton);
        waitButton.setOnClickListener(event -> waitForFiveMinutes());
    }

    private void waitForFiveMinutes() {
        if (MyAlarmReceiver.getRingtone() == null) {
            return;
        }
        turnOnScreen();
        MyAlarmReceiver.getRingtone().stop();
        MyAlarmReceiver.getAlarm().sleepForFiveMinutes(getApplicationContext());
        finish();
    }

    private void stopPlaying() {
        if (MyAlarmReceiver.getRingtone() == null) {
            return;
        }
        MyAlarmReceiver.getRingtone().stop();
        finish();
        MyAlarmReceiver.getNotificationManager().cancelAll();
    }

    private void turnOnScreen() {
        PowerManager.WakeLock screenLock = ((PowerManager) MainActivity.activity
                .getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();
        screenLock.release();
    }
}
