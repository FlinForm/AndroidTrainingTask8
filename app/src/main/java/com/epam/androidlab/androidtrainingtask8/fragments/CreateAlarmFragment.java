package com.epam.androidlab.androidtrainingtask8.fragments;

import android.content.ContentValues;
import android.media.Ringtone;
import android.net.Uri;
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

import com.epam.androidlab.androidtrainingtask8.activities.MainActivity;
import com.epam.androidlab.androidtrainingtask8.R;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.RepeatLoop;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment displays UI window, where we can create new alarm
 * and add it to alarms array in Main activity.
 */

public class CreateAlarmFragment extends Fragment {
    private final Uri ALARM_URI = Uri
            .parse("content://com.epam.androidlab.androidtrainingtask8.serialization/alarms");
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
        dataAdapter.add("melody");
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

    // Stop playing chosen ringtone when alarm is created
    private void stopPlayingRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private void createAlarm() {
        if ("".equals(editText.getText().toString()) ||
                !isUniqueNameChoosen(editText.getText().toString())) {
            Snackbar.make(view, "Enter, please, unique name!", 1000).show();
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
        addAlarmToProvider(alarm);
        MainActivity.getAlarms().add(alarm);
        MainActivity.getRecyclerView().getAdapter().notifyDataSetChanged();
        alarm.startAlarm(getContext(), alarm.getTimeInMillis());
        cancel();
    }

    private void addAlarmToProvider(MyAlarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put("name", alarm.getAlarmName());
        cv.put("ringtone", alarm.getAlarmRingtone());
        cv.put("repeating", alarm.getRepeatLoop().toString());
        cv.put("hours", alarm.getHours());
        cv.put("minutes", alarm.getMinutes());
        cv.put("switched", alarm.isSwitchedOn());
        Uri uri = getActivity().getContentResolver().insert(ALARM_URI, cv);
    }

    // Cancels current fragment when alarm is created
    private void cancel() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    // Checks uniqueness alarm name
    private boolean isUniqueNameChoosen(String name) {
        for (MyAlarm alarm : MainActivity.getAlarms()) {
            if (alarm.getAlarmName().equals(name)) {
                System.out.println(false);
                return false;
            }
        }
        System.out.println(true);
        return true;
    }

    // Returns a String list of Ringtone names for inflating alarmRingtoneSpinner
    private List<String> getRingtonesNames() {
        List<String> ringtones = new ArrayList<>();
        for (int i = 0; i < MainActivity.getRingtones().size(); i++) {
            ringtones.add(MainActivity.getRingtones().get(i).getTitle(getContext()));
        }
        return ringtones;
    }

    private RepeatLoop ringtoneRepeatCount() {
        return repeatSpinner.getSelectedItem().toString().equals("One time") ?
                RepeatLoop.ONE_TIME : RepeatLoop.EVERY_DAY;
    }
}