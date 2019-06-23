package com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "record.db";
    /*public static final String TABLE_NAME = "record";
    public static final String COL1 = "ID";
    public static final String COL2 = "Date";
    public static final String COL3 = "StartTime";
    public static final String COL4 = "EndTime";
    public static final String COL5 = "Steps";
    public static final String COL6 = "Distance";
    public static final String COL7 = "Duration";
    public static final String COL8 = "AvgSpeed";*/

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

    /*public Cursor showData() {
        //DatabaseHelper recordDB = new DatabaseHelper(this);
        SQLiteDatabase db = db.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + RecordTab.Record.TABLE_NAME, null);
        return data;
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " Date TEXT)";//, StartTime TEXT, EndTime TEXT, Steps TEXT, Distance TEXT, Duration TEXT, AvgSpeed TEXT)";
        db.execSQL(createTable);*/
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

    /*public boolean addData(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL2, date);

        long result = db.insert(TABLE_NAME, null, cv);
        //db.close();

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }*/

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
        //db.close();

        if (result == -1) {
            return false;
        }
        else {
            return true;
        }
    }
}
