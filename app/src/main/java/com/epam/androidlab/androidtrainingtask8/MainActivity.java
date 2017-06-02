package com.epam.androidlab.androidtrainingtask8;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.fragments.StartAlarmFragment;
import com.epam.androidlab.androidtrainingtask8.serialization.SqlLiteParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static List<Ringtone> ringtones;
    private static List<MyAlarm> alarms;
    private static RecyclerView recyclerView;
    public static MainActivity activity;
    SqlLiteParser sqlLiteParser;
    private CardView cardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        alarms = new ArrayList<>();
        ringtones = initAlarmsAndRingtones(this);
        sqlLiteParser = new SqlLiteParser(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sqlLiteParser.loadAlarms(alarms);
        System.out.println("alarms size " + alarms.size());
        for (int i = 0; i < alarms.size(); i++) {
            System.out.println(alarms.get(i).getAlarmName() + " "
                    + alarms.get(i).getAlarmRingtone());
        }

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(event -> startNewAlarm());
        floatingActionButton.setImageResource(R.drawable.plus);

        RecycleViewAdapter adapter = new RecycleViewAdapter(alarms);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        cardView = (CardView) findViewById(R.id.cv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarms != null) {
            sqlLiteParser.saveAlarms(alarms);
        }
    }

    private List<Ringtone> initAlarmsAndRingtones(Context context) {
        List<Ringtone> ringtoneList = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                RingtoneManager manager = new RingtoneManager(context);
                manager.setType(RingtoneManager.TYPE_RINGTONE);

                for (int i = 0; i < manager.getCursor().getCount(); i++) {
                    ringtoneList.add(manager.getRingtone(i));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }.execute();
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
