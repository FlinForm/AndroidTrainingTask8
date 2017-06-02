package com.epam.androidlab.androidtrainingtask8.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
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
import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.RepeatLoop;

import java.util.ArrayList;
import java.util.List;

public class StartAlarmFragment extends Fragment {
    private Spinner alarmRingtoneSpinner;
    private Spinner repeatSpinner;
    private Ringtone ringtone;
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

        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        editText = (EditText) view.findViewById(R.id.editText);

        View startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(event ->
        {
            stopPlayingRingtone();
            createAlarm();
        });

        View cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(event -> {
            stopPlayingRingtone();
            cancel();
        });

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
                stopPlayingRingtone();
                if (alarmRingtoneSpinner.getSelectedItemPosition() != ringtones.size() - 1) {
                     ringtone = MainActivity.getRingtones()
                            .get(alarmRingtoneSpinner.getSelectedItemPosition());
                    ringtone.play();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void stopPlayingRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private void createAlarm() {
        if ("".equals(editText.getText().toString())) {
            Snackbar.make(view, "Enter, please, alarm name!", 1000).show();
            return;
        }
        if ("".equals(alarmRingtoneSpinner.getSelectedItem().toString())) {
            Snackbar.make(view, "Select alarm ringtone!", 1000).show();
            return;
        }
        MyAlarm alarm = new MyAlarm(
                alarmRingtoneSpinner.getSelectedItem().toString(),
                editText.getText().toString(),
                ringtoneRepeatCount(),
                timePicker.getHour(),
                timePicker.getMinute(),
                true);
        MainActivity.getAlarms().add(alarm);
        MainActivity.getRecyclerView().getAdapter().notifyDataSetChanged();
        startAlarm(alarm.getTimeInMillis(), alarm.getAlarmName(), ringtoneRepeatCount());
        cancel();
    }

    private void cancel() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private List<String> getRingtonesNames() {
        List<String> ringtones = new ArrayList<>();
        for (int i = 0; i < MainActivity.getRingtones().size(); i++) {
            ringtones.add(MainActivity.getRingtones().get(i).getTitle(getContext()));
        }
        return ringtones;
    }

    private RepeatLoop ringtoneRepeatCount() {
        switch (repeatSpinner.getSelectedItem().toString()) {
            case "One time" :
                return RepeatLoop.ONE_TIME;
            case "Every day":
                return RepeatLoop.EVERY_DAY;
        }
        return null;
    }

    private void startAlarm(long timeInMillis, String name, RepeatLoop loop) {
        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), MyAlarmReceiver.class);
        intent.putExtra("ALARM_NAME", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        switch (loop) {
            case ONE_TIME:
                manager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                break;
            case EVERY_DAY:
                manager.setRepeating(AlarmManager.RTC_WAKEUP,
                        timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent);
                break;
        }
    }
}