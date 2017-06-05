package com.epam.androidlab.androidtrainingtask8.serialization;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class AlarmsProvider extends ContentProvider {
    private static final String DB_NAME = "db";
    private static final int DB_VERSION = 1;

    private static final String ALARM_TABLE = "alarms";
    private static final String ALARM_ID = "id";
    private static final String ALARM_NAME = "name";
    private static final String ALARM_RINGTONE = "ringtone";
    private static final String ALARM_REPEATING = "repeating";
    private static final String ALARM_HOURS = "hours";
    private static final String ALARM_MINUTES = "minutes";
    private static final String ALARM_SWITCHED = "switched";

    private static final String DB_CREATE = "create table " + ALARM_TABLE + "("
            + ALARM_ID + " integer primary key autoincrement, "
            + ALARM_NAME + " text, "
            + ALARM_RINGTONE + " text, "
            + ALARM_REPEATING + " text, "
            + ALARM_HOURS + " integer, "
            + ALARM_MINUTES + " integer, "
            + ALARM_SWITCHED + " boolean" + ");";

    // Uri's
    private static final String AUTHORITY = "com.epam.androidlab.androidtrainingtask8.serialization";
    private static final String ALARMS_PATH = "alarms";
    public static final Uri ALARMS_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + ALARMS_PATH);
    private static final String ALARM_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + ALARMS_PATH;
    private static final String ALARM_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + ALARMS_PATH;
    private static final int URI_ALARMS = 1;
    private static final int URI_ALARMS_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, ALARMS_PATH, URI_ALARMS);
        uriMatcher.addURI(AUTHORITY, ALARMS_PATH + "/#", URI_ALARMS_ID);
    }

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    // replace switch with if statement
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
            switch (uriMatcher.match(uri)) {
                case URI_ALARMS:
                    if (TextUtils.isEmpty(sortOrder)) {
                        sortOrder = ALARM_NAME + " ASC";
                    }
                    break;
                case URI_ALARMS_ID:
                    String id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        selection = ALARM_ID + " = " + id;
                    } else {
                        selection = selection + " AND " + ALARM_ID + " = " + id;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Wrong URI: " + uri);
            }
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(ALARM_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(getContext().getContentResolver(),
                    ALARMS_CONTENT_URI);
            return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return uriMatcher.match(uri) == URI_ALARMS ?
                ALARM_CONTENT_TYPE : ALARM_CONTENT_ITEM_TYPE;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != URI_ALARMS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        sqLiteDatabase = dbHelper.getWritableDatabase();
        long rowID = sqLiteDatabase.insert(ALARM_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(ALARMS_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    //replace switch with if statement
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_ALARMS:
                //--------------
                break;
            case URI_ALARMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ALARM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + ALARM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = dbHelper.getWritableDatabase();
        int cnt = sqLiteDatabase.delete(ALARM_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    //replace switch with if statement
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_ALARMS:
                //----------------
                break;
            case URI_ALARMS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ALARM_ID + " = " + id;
                } else {
                    selection = selection + " AND " + ALARM_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        sqLiteDatabase = dbHelper.getWritableDatabase();
        int cnt = sqLiteDatabase.update(ALARM_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    private class DBHelper extends SQLiteOpenHelper {
        ContentValues cv;

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            cv = new ContentValues();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
