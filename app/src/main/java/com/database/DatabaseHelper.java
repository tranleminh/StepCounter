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


    /***********************CONSTRUCTOR**************************/

    /**
     * The DatabaseHelper constructor.
     * Database name and version are provided in MainActivity
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /************************METHODS****************************/

    /**
     * Execute a SQL query then store the results in a cursor
     * @return a Cursor containing data to be displayed
     */
    public Cursor showData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + RecordTab.Record.TABLE_NAME, null);
        return data;
    }

    /**
     * Overridden method of onCreate().
     * The CREATE TABLE command, stored in a constant String variable,
     * is executed here.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    /**
     * Overridden method of onUpgrade()
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    /**
     * Overridden method of onDowngrade
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Method used to add data into the database
     * @param date a String containing the current date
     * @param starttime a String containing the start time
     * @param endtime a String containing the end time
     * @param steps a String containing the total number of steps walked during a period
     * @param distance a String containing the walked distance
     * @param duration a String containing the walked duration
     * @param avgspeed a String containing the average speed calculated
     * @return a boolean indicating the data insert status
     */
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
