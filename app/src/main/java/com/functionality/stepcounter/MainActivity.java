package com.functionality.stepcounter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.database.DatabaseHelper;
import com.database.RecordTab;
import com.functionality.stepdetector.*;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.database.RecordTab.Record.COL2;
import static com.database.RecordTab.Record.COL3;
import static com.database.RecordTab.Record.COL4;
import static com.database.RecordTab.Record.COL5;
import static com.database.RecordTab.Record.COL6;
import static com.database.RecordTab.Record.COL7;
import static com.database.RecordTab.Record.COL8;
import static com.database.RecordTab.Record.TABLE_NAME;
import static com.database.RecordTab.Record._ID;


public class MainActivity extends AppCompatActivity implements SensorEventListener, com.listener.StepListener {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps = 0;
    private int currentTick = 0;
    private double dist = 0;
    private long time = 0;
    private TextView TvSteps;
    private TextView Status;
    private TextView Distance;
    private TextView Log;
    //private TextView Timing;
    private Button BtnStart;
    private Button BtnStop;
    private Chronometer Timing;
    private Chronometer CLK;
    private long pauseOffset = 0;
    private boolean running = false;
    private TextView Speed;
    private int speed = 0;
    private Date start;
    private Date end;
    private boolean dateLock = true;
    private boolean dbLock = true;
    private Button BtnViewData;

    DatabaseHelper recordDB;// = new DatabaseHelper(this);

    //recordDB = new DatabaseHelper(this);

    /*public boolean addData(String date, String starttime, String endtime, String steps, String distance, String duration, String avgspeed) {
        DatabaseHelper recordDB = new DatabaseHelper(this);
        SQLiteDatabase db = recordDB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecordTab.Record.COL2, date);
        contentValues.put(RecordTab.Record.COL3, starttime);
        contentValues.put(RecordTab.Record.COL4, endtime);
        contentValues.put(RecordTab.Record.COL5, steps);
        contentValues.put(RecordTab.Record.COL6, distance);
        contentValues.put(RecordTab.Record.COL7, duration);
        contentValues.put(RecordTab.Record.COL8, avgspeed);

        long result = db.insert(TABLE_NAME, null, contentValues);
        //db.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }*/

    public Cursor showData() {
        DatabaseHelper recordDB = new DatabaseHelper(this);
        SQLiteDatabase db = recordDB.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

    public void viewData() {
        BtnViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor data = recordDB.showData();

                if (data.getCount() == 0) {
                    display("Error", "No Data Found!");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(data.moveToNext()) {
                    buffer.append("Date: " + data.getString(1) + "\n");
                    buffer.append("Start: " + data.getString(2) + "\n");
                    buffer.append("End: " + data.getString(3) + "\n");
                    buffer.append("Steps: " + data.getString(4) + "\n");
                    buffer.append("Distance: " + data.getString(5) + "\n");
                    buffer.append("Duration: " + data.getString(6) + "\n");
                    buffer.append("AvgSpeed: " + data.getString(7) + "\n");
                    buffer.append("--------------------------------------\n");
                }
                display("All Stored Data:", buffer.toString());
            }
        });
    }

    public void display(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordDB = new DatabaseHelper(this);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        Status = (TextView) findViewById(R.id.status);
        Distance = (TextView) findViewById(R.id.distance);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        
        BtnViewData = (Button) findViewById(R.id.btn_db);

        Log = (TextView) findViewById(R.id.log);

        Timing = (Chronometer) findViewById(R.id.timer);
        Timing.setFormat("Walked Time: %s");
        Timing.setBase(SystemClock.elapsedRealtime());

        CLK = (Chronometer) findViewById(R.id.clk);

        CLK.setBase(SystemClock.elapsedRealtime());
        CLK.start();

        viewData();

        CLK.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat date = new SimpleDateFormat("EEE, dd MM YYYY");

                //Date start = Calendar.getInstance().getTime();
                //Date end;
                if (currentTick < numSteps) {
                    if (!dateLock) {
                        start = Calendar.getInstance().getTime();
                        dateLock = true;
                    }
                    startChrono();
                    time = SystemClock.elapsedRealtime() - Timing.getBase();
                    speed = (int) (dist / ((double) time / 1000) * 3.6);
                    Speed.setText("Walking speed : " + speed + "km/h");
                    currentTick+= 2;
                    dbLock = false;
                }
                else {
                    stopChrono();
                    if (dateLock) {
                        end = Calendar.getInstance().getTime();
                        dateLock = false;
                    }
                    if (start != null) {
                        Log.setText("Today is " + date.format(start) +", User walks from " + df.format(start) + " to " + df.format(end) + " for " + Double.toString(speed) + " km/h");
                        if (!dbLock) {
                            boolean insertData = recordDB.addData(date.format(start), df.format(start), df.format(end), Integer.toString(numSteps), Double.toString(dist) + "m", Double.toString(time/1000f) + "s", Double.toString(speed) + "km/h");
                            if (insertData) {
                                Toast.makeText(MainActivity.this, "New Data Inserted!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Failed to insert new data", Toast.LENGTH_LONG).show();
                            }
                            dbLock = true;
                        }
                    }

                }
            }
        });

        Speed = (TextView) findViewById(R.id.speed);

        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (numSteps == 0 && SystemClock.elapsedRealtime() - Timing.getBase() == 0) {
                    Status.setText("Counter started");
                }
                else {
                    Status.setText("Counter reset");
                    numSteps = 0;
                    dist = 0;
                    speed = 0;
                    currentTick = 0;
                    resetChrono();
                }
                //chrono.start();
                startChrono();
                TvSteps.setText(TEXT_NUM_STEPS + numSteps);
                Distance.setText("Walked distance : " + dist + "m");
                //Timing.setText(chrono.getFormat());

                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(MainActivity.this);
                stopChrono();
                Status.setText("Counter stopped");


            }
        });

    }

    /*private void viewData() {
        SQLiteDatabase db = recordDB.getReadableDatabase();

        String[] projection = {
                _ID,
                COL2,
                COL3,
                COL4,
                COL5,
                COL6,
                COL7,
                COL8
        };

        Cursor cursor = db.query(TABLE_NAME, projection, "*", null, null, null, null);



    }*/

    public void startChrono() {
        if (!running) {
            Timing.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            Timing.start();
            running = true;
        }
    }

    public void stopChrono() {
        if (running) {
            Timing.stop();
            pauseOffset = SystemClock.elapsedRealtime() - Timing.getBase();
            running = false;
        }
    }

    public void resetChrono() {
        Timing.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs){
        long t = 0;
        numSteps++;
        Status.setText("Counter started");
        dist = numSteps * 0.75;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        if (dist <= 1000) {
            Distance.setText("Walked distance : " + dist + "m");
        }
        else {
            Distance.setText("Walked distance : " + dist / 1000f + "km");
        }
        //+startChrono();
    }



}

