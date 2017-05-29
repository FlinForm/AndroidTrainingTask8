package com.epam.androidlab.androidtrainingtask8;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.fragments.StartAlarmFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static List<Ringtone> ringtones;
    private static List<MyAlarm> alarms;
    private static RecyclerView recyclerView;
    public static MainActivity activity;
    private static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        alarms = new ArrayList<>();

        mediaPlayer = new MediaPlayer();

        ringtones = getSystemRingtones(this);

        View floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(event -> startNewAlarm());

        RecycleViewAdapter adapter = new RecycleViewAdapter(alarms);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private List<Ringtone> getSystemRingtones(Context context) {
        List<Ringtone> ringtoneList = new ArrayList<>();

        new Thread(() -> {
            RingtoneManager manager = new RingtoneManager(context);
            manager.setType(RingtoneManager.TYPE_RINGTONE);

            for (int i = 0; i < manager.getCursor().getCount(); i++) {
                ringtoneList.add(manager.getRingtone(i));
            }
        }).start();

        return ringtoneList;
    }

    private void startNewAlarm() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rLayout, new StartAlarmFragment());
        transaction.commit();
    }

    public static List<Ringtone> getRingtones() {
        return ringtones;
    }

    public static List<MyAlarm> getAlarms() {
        return alarms;
    }

    public static RecyclerView getRecyclerView() {
        return recyclerView;
    }

}
