package com.epam.androidlab.androidtrainingtask8.activities;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.epam.androidlab.androidtrainingtask8.R;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.RepeatLoop;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ItemViewHolder> {

    private final List<MyAlarm> alarmList;
    private static View view;

    public RecycleViewAdapter(List<MyAlarm> alarmList) {
        this.alarmList = alarmList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent
                .getContext())
                .inflate(R.layout.alarm_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.name.setText(alarmList.get(position).getAlarmName());
        holder.time.setText(alarmList.get(position).getTime());
        if (alarmList.get(position).getRepeatLoop().equals(RepeatLoop.ONE_TIME)) {
            holder.repeating.setText(R.string.one_day_repeat);
        } else holder.repeating.setText(R.string.every_day_repeat);
        holder.alarm = alarmList.get(position);
        setSwitch(holder, position);
    }

    private void setSwitch(ItemViewHolder holder, int position) {
        if (alarmList.get(position).isSwitchedOn()) {
            holder.aSwitch.setChecked(true);
        } else {
            holder.aSwitch.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    implements View.OnCreateContextMenuListener, CompoundButton.OnCheckedChangeListener {
        private final int MENU_ITEM_DELETE = 1;
        private final TextView name;
        private final TextView time;
        private final TextView repeating;
        private final Switch aSwitch;
        private MyAlarm alarm;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.alarm_name);
            time = (TextView) itemView.findViewById(R.id.alarm_time);
            repeating = (TextView) itemView.findViewById(R.id.repeating);
            aSwitch = (Switch) itemView.findViewById(R.id.alarm_switch);

            itemView.setOnCreateContextMenuListener(this);
            aSwitch.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, MENU_ITEM_DELETE, 0, "delete" + " " + name.getText().toString());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) {
                alarm.setSwitchedOn(false);
                alarm.getAlarmManager().cancel(alarm.getPendingIntent());
            } else {
                alarm.setSwitchedOn(true);
                alarm.startAlarm(view.getContext(), alarm.getTimeInMillis());
            }
        }
    }
}