package com.epam.androidlab.androidtrainingtask8;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class WakeupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_wakeup);

        View stopAlarmButton = findViewById(R.id.stopButton);
        stopAlarmButton.setOnClickListener(event -> stopPlaying());

        View waitButton = findViewById(R.id.waitButton);
        waitButton.setOnClickListener(event -> waitForFiveMinutes());
    }

    private void waitForFiveMinutes() {
        MyAlarmReceiver.getAlarm().getAlarmRingtone().stop();
        SystemClock.sleep(300_000);
        turnOnScreen();
        MyAlarmReceiver.getAlarm().getAlarmRingtone().play();
    }

    private void stopPlaying() {
        MyAlarmReceiver.getAlarm().getAlarmRingtone().stop();
        finish();
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
