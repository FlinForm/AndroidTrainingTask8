package com.epam.androidlab.androidtrainingtask8.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.epam.androidlab.androidtrainingtask8.MainActivity;
import com.epam.androidlab.androidtrainingtask8.MyAlarmReceiver;
import com.epam.androidlab.androidtrainingtask8.R;
import com.epam.androidlab.androidtrainingtask8.WakeupActivity;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.RepeatLoop;

import java.util.ArrayList;
import java.util.List;

public class StartAlarmFragment extends Fragment {
    private Spinner alarmRingtoneSpinner;
    private MediaPlayer player;
    private Spinner repeatSpinner;
    private TimePicker timePicker;
    private EditText editText;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        player = new MediaPlayer();

        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        editText = (EditText) view.findViewById(R.id.editText);

        View startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(event -> createAlarm());

        View cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(event -> cancel());

        List<String> ringtones = getRingtonesNames();

        repeatSpinner = (Spinner) view.findViewById(R.id.repeatSpinner);

        alarmRingtoneSpinner = (Spinner) view.findViewById(R.id.alarmRingtoneSpinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item, ringtones);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataAdapter.add("");
        alarmRingtoneSpinner.setAdapter(dataAdapter);
        alarmRingtoneSpinner.setSelection(ringtones.size() - 1);
        alarmRingtoneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (alarmRingtoneSpinner.getSelectedItemPosition() != ringtones.size() - 1) {
                    if (player.isPlaying() == true) {
                        player.stop();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void playRingtone() {
        getRingtoneForName(alarmRingtoneSpinner.getSelectedItem().toString()).play();
    }

    private void createAlarm() {
        if ("".equals(editText.getText().toString())) {
            Snackbar.make(view, "Enter, please, alarm name!", 1000).show();
            return;
        }
        MyAlarm alarm = new MyAlarm(
                getRingtoneForName(alarmRingtoneSpinner.getSelectedItem().toString()),
                editText.getText().toString(),
                ringtoneRepeatCount(),
                timePicker.getHour(),
                timePicker.getMinute(),
                true);
        MainActivity.getAlarms().add(alarm);
        MainActivity.getRecyclerView().getAdapter().notifyDataSetChanged();
        startAlarm(alarm.getTimeInMillis());
        getContext().sendBroadcast(new Intent("myIntent").putExtra("RINGTONE",
                alarmRingtoneSpinner.getSelectedItem().toString()));
        cancel();
    }

    private void cancel() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private List<String> getRingtonesNames() {
        List<String> ringtones = new ArrayList<>();
        for (int i = 0; i < MainActivity.getRingtones().size(); i++) {
            ringtones.add(MainActivity.getRingtones().get(i).getTitle(view.getContext()));
        }
        return ringtones;
    }

    private Ringtone getRingtoneForName(String name) {
        Ringtone ringtone = null;
        for (int i = 0; i < MainActivity.getRingtones().size(); i++) {
            if (name.equals(MainActivity.getRingtones().get(i).getTitle(view.getContext()))) {
                ringtone = MainActivity.getRingtones().get(i);
            }
        }
        return ringtone;
    }

        private RepeatLoop ringtoneRepeatCount() {
        switch (repeatSpinner.getSelectedItem().toString()) {
            case "One time" :
                return RepeatLoop.ONE_TIME;
            case "Every day":
                return RepeatLoop.EVERY_DAY;
            case "Monday-Friday":
                return RepeatLoop.MONDAY_FRIDAY;
        }
        return null;
    }

    public void startAlarm(long timeInMillis) {
        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext().getApplicationContext(), MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }
}