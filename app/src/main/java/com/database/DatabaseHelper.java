package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "record.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + RecordTab.Record.TABLE_NAME + " (" +
                    RecordTab.Record._ID + " INTEGER PRIMARY KEY," +
                    RecordTab.Record.COL2 + " TEXT," +
                    RecordTab.Record.COL3 + " TEXT," +
                    RecordTab.Record.COL4 + " TEXT," +
                    RecordTab.Record.COL5 + " TEXT," +
                    RecordTab.Record.COL6 + " TEXT," +
                    RecordTab.Record.COL7 + " TEXT," +
                    RecordTab.Record.COL8 + " TEXT);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RecordTab.Record.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor showData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + RecordTab.Record.TABLE_NAME, null);
        return data;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addData(String date, String starttime, String endtime, String steps, String distance, String duration, String avgspeed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecordTab.Record.COL2, date);
        contentValues.put(RecordTab.Record.COL3, starttime);
        contentValues.put(RecordTab.Record.COL4, endtime);
        contentValues.put(RecordTab.Record.COL5, steps);
        contentValues.put(RecordTab.Record.COL6, distance);
        contentValues.put(RecordTab.Record.COL7, duration);
        contentValues.put(RecordTab.Record.COL8, avgspeed);

        long result = db.insert(RecordTab.Record.TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }


}
