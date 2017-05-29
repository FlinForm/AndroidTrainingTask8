package com.epam.androidlab.androidtrainingtask8;

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
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    private void stopPlaying() {
        MyAlarmReceiver.getPlayThread().interrupt();
    }
}
