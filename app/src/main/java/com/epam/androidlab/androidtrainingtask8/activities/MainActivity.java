package com.epam.androidlab.androidtrainingtask8.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.epam.androidlab.androidtrainingtask8.R;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.RepeatLoop;
import com.epam.androidlab.androidtrainingtask8.fragments.CreateAlarmFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final Uri ALARMS_URI =
            Uri.parse("content://com.epam.androidlab.androidtrainingtask8.serialization/alarms");
    private static List<Ringtone> ringtones;
    private static List<MyAlarm> alarms;
    private static RecyclerView recyclerView;
    public static MainActivity activity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        ringtones = initRingtones(this);
        fillAlarmsArray();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(event -> createNewAlarm());

        RecycleViewAdapter adapter = new RecycleViewAdapter(alarms);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    // This method is used to handle delete option when long tapping on alarm in RecucleView
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String[] title = item.getTitle().toString().split(" ");
        removeAlarm(title[1], 0);
        return true;
    }

    private List<Ringtone> initRingtones(Context context) {
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

    private void createNewAlarm() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rLayout, new CreateAlarmFragment());
        transaction.commit();
    }

    // Fills alarms array with saved data from SQLite database using ContentProvider
    private void fillAlarmsArray() {
        Cursor cursor = getContentResolver().query(ALARMS_URI, null, null, null, null);
        alarms = new ArrayList<>();
            while (cursor.moveToNext()) {
                MyAlarm alarm = new MyAlarm(
                        cursor.getString(cursor.getColumnIndex("ringtone")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        RepeatLoop.valueOf(cursor.getString(cursor.getColumnIndex("repeating"))),
                        cursor.getInt(cursor.getColumnIndex("hours")),
                        cursor.getInt(cursor.getColumnIndex("minutes")),
                        Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("switched"))));
                alarms.add(alarm);
                alarm.startAlarm(getApplicationContext(), alarm.getTimeInMillis());
            }
        cursor.close();
    }

    private void removeAlarm(String name, int position) {
        // Here is used recursion because of ConcurrentModificationException
        if (position >= alarms.size()) {
            return;
        }
        if (alarms.get(position).getAlarmName().equals(name)) {
            alarms.remove(alarms.get(position));
            removeAlarmFromProvider(name);
            recyclerView.removeAllViews();
            recyclerView.getAdapter().notifyDataSetChanged();
            return;
        } else {
            removeAlarm(name, ++position);
        }
    }

    private void removeAlarmFromProvider(String name) {
        String selection = "name = " + name + ";";
        getContentResolver().delete(ALARMS_URI, selection, null);
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
