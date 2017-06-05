package com.epam.androidlab.androidtrainingtask8;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ItemViewHolder> {

    private final List<MyAlarm> alarmList;

    public RecycleViewAdapter(List<MyAlarm> alarmList) {
        this.alarmList = alarmList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent
                .getContext())
                .inflate(R.layout.alarm_card, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.name.setText(alarmList.get(position).getAlarmName());
        holder.time.setText(alarmList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
    implements View.OnCreateContextMenuListener {
        private final int MENU_ITEM_DELETE = 1;
        private final TextView name;
        private final TextView time;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.alarm_name);
            time = (TextView) itemView.findViewById(R.id.alarm_time);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, MENU_ITEM_DELETE, 0, "delete" + " " + name.getText().toString());
        }
    }
}