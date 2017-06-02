package com.epam.androidlab.androidtrainingtask8.serialization;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.epam.androidlab.androidtrainingtask8.MainActivity;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.MyAlarm;
import com.epam.androidlab.androidtrainingtask8.alarmmodel.RepeatLoop;

import java.util.List;

public class SqlLiteParser {
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    Context context;

    public SqlLiteParser(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    public void loadAlarms(List<MyAlarm> alarms) {
        Cursor c = sqLiteDatabase.query("alarm", null, null, null, null, null, null);

            int ringtoneColIndex = c.getColumnIndex("melody");
            int nameColIndex = c.getColumnIndex("name");
            int repeatingColIndex = c.getColumnIndex("repeating");
            int hoursColIndex = c.getColumnIndex("hours");
            int minutesColIndex = c.getColumnIndex("minutes");

            if (c.moveToFirst()) {
                while (c.moveToNext()) {
                    alarms.add(new MyAlarm(
                           c.getString(ringtoneColIndex),
                            c.getString(nameColIndex),
                            RepeatLoop.valueOf(c.getString(repeatingColIndex)),
                            c.getInt(hoursColIndex),
                            c.getInt(minutesColIndex),
                            false
                    ));
                }
            }
        System.out.println(sqLiteDatabase.delete("alarm", null, null));
        c.close();
    }

    public void saveAlarms(List<MyAlarm> alarms) {
        if (alarms == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        for (MyAlarm alarm : alarms) {
            contentValues.put("melody", alarm.getAlarmRingtone());
            contentValues.put("name", alarm.getAlarmName());
            contentValues.put("repeating", alarm.getRepeatLoop().toString());
            contentValues.put("hours", alarm.getHours());
            contentValues.put("minutes", alarm.getMinutes());
            sqLiteDatabase.insert("alarm", null, contentValues);
        }
        Log.e("TAG", "Saved");
        dbHelper.close();
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "alarms", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL("create table alarm ("
                    + "id integer primary key autoincrement,"
                    + "melody text,"
                    + "name text,"
                    + "repeating text,"
                    + "hours integer,"
                    + "minutes integer" + ");");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
