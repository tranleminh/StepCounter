package com.functionality;

import android.app.AlertDialog;
import android.database.Cursor;
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
import com.functionality.stepcounter.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    /**********Attributes and global variables are declared here***************/
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private static final int SPEED_THRESHOLD = 20;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;

    private int numSteps = 0;
    private int currentTick = 0;
    private static final int maxTick = 3;
    private int old_nb_step = 0;
    private double dist = 0;
    private int time = 0;
    private TextView TvSteps;
    private TextView Status;
    private TextView Distance;
    private TextView Log;
    private Button BtnStart;
    private Button BtnStop;
    private Chronometer Timing;
    private Chronometer CLK;
    private long pauseOffset = 0;
    private boolean running = false;
    private TextView Speed;
    private double speed = 0;
    private Date start;
    private Date end;
    private boolean dateLock = false;
    private boolean dbLock = true;
    private long timeBase = System.currentTimeMillis();
    private TextView Timer;
    private int time2;
    private Button BtnViewData;
    private DatabaseHelper recordDB;


    /******Methods relative to step counter's walking data management*******/

    /**
     * Deprecated since 1.01. No longer used
     * Start the chronometer that computes walking time
     */
    @Deprecated
    private void startChrono() {
        if (!running) {
            Timing.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            Timing.start();
            running = true;
        }
    }

    /**
     * Deprecated since 1.01. No longer used
     * Stop the chronometer that computes walking time
     */
    @Deprecated
    private void stopChrono() {
        if (running) {
            Timing.stop();
            pauseOffset = SystemClock.elapsedRealtime() - Timing.getBase();
            running = false;
        }
    }

    /**
     * Deprecated since 1.01. No longer used
     * Reset the chronometer that computes walking time
     */
    @Deprecated
    private void resetChrono() {
        Timing.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    /**
     * Reset to 0 all measuring data and reset chronometer
     */
    private void resetAll() {
        numSteps = 0;
        dist = 0;
        speed = 0;
        old_nb_step = 0;
        currentTick = 0;
        resetChrono();
        timeBase = System.currentTimeMillis();
    }


    /************************Private methods relative to unit conversion***************************/

    /**
     * Converts a given time in seconds into a string of hours, minutes, seconds.
     * @param time the time given in seconds
     * @return     a String containing time in hours, minutes, seconds
     */
    private String time_conv(int time) {
        String res = "";
        int s = time;
        int m = 0;
        int h = 0;
        /*if (s == 0) {
            res = "1s";
        }
        else {*/
        if (s < 60) {
            res += s + "s";
        } else {
            if (s >= 60 && s < 3600) {
                m = s / 60;
                s = s % 60;
                res += m + "m" + s + "s";
            } else {
                h = s / 3600;
                m = (s % 3600) / 60;
                s = (s % 3600) % 60;
                res += h + "h" + m + "m" + s + "s";
            }
        }
        //}
        return res;
    }

    /**
     * Converts a given distance in meters into kilometers if needed.
     * @param dist a distance in meters
     * @return     a String containing distance in meters or kilometers
     */
    private String dist_conv(double dist) {
        String res = "";
        if (dist < 1000) {
            res += dist + "m";
        }
        else {
            res += dist/1000f + "km";
        }
        return res;
    }

    /**
     * Round up or round down the given speed to the nearest integer value.
     * @param speed a given speed
     * @return      a String containing its value rounded up or down to the nearest integer value
     */
    private String speed_conv(double speed) {
        String res = "";
        if (speed - (int)speed >= 0.5) {
            res += ((int)speed + 1) + "km/h";
        }
        else {
            res += (int)speed + "km/h";
        }
        return res;
    }


    /****************Methods relative to database's manipulation*****************/

    /**
     * Display the database by pressing the "View Record" button
     */
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


    /**
     * an auxiliary method used in viewData().
     * This method creates an alert dialog to show database' stored data.
     * @param title the title of the alert dialog
     * @param message the message to be delivered
     */
    public void display(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }



    /****************Main Android methods' implementation*****************/

    /**
     * The overridden method of onCreate()
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiate the database
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

        Timer = findViewById(R.id.timer);
        Timing = (Chronometer) findViewById(R.id.timing);
        Timing.setFormat("Walked Time: %s");
        Timing.setBase(SystemClock.elapsedRealtime());

        //CLK is the app's common chronometer, used in computing walking time and number of steps every clock's tick.
        CLK = (Chronometer) findViewById(R.id.clk);
        CLK.setBase(SystemClock.elapsedRealtime());
        CLK.start();

        //"View Record" button function called here
        viewData();

        //Every clock's tick, start date, end date, walking time and walking speed are updated
        CLK.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                //Instantiate date format
                SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat date = new SimpleDateFormat("EEE, dd MM YYYY");

                //Compare between the current number of steps and the number of steps from previous clock's tick.
                //If they are different, continue to compute walking time and walking speed
                //Else, proceed to the next part to compute end time and insert new data to database
                if (old_nb_step != numSteps) {

                    //variable dateLock served as a pseudo-mutex to make sure only one instance of start time is computed at the beginning of each record
                    if (!dateLock) {
                        start = Calendar.getInstance().getTime();
                        dateLock = true;
                    }

                    //Computing walking time and walking speed
                    startChrono();
                    time = (int)((SystemClock.elapsedRealtime() - Timing.getBase())/1000f)+1;
                    time2 = (int)((System.currentTimeMillis() - timeBase)/1000f);
                    Timer.setText("Walking time : " + time_conv(time2));
                    //Speed = Distance / Time. Initially speed is calculated in meters/second, converted to kilometers/hour by multiplying with 3.6
                    speed = (dist / (float)time2) * 3.6;
                    if (speed <= SPEED_THRESHOLD)
                        Speed.setText("Walking speed : " + speed_conv(speed));
                    else
                        Speed.setText("No data on moving speed");

                    //Updating old number of steps
                    old_nb_step = numSteps;

                    dbLock = false;

                    //currentTick served as the number of seconds consecutive the person is not moving, i.e numSteps are not increasing. If numSteps are changing, currentTick is reset to 0.
                    currentTick = 0;

                }

                else {
                    //If the person is not moving, the app checks if the person has reached the maxTick value so it updates currentTick or proceeds to the next part.
                    if (currentTick < maxTick) {
                        currentTick++;
                    }

                    //If the person has already not moved for maxTick seconds, end time is computed and new data is inserted into the database
                    else {
                        //dateLock to make sure only one instance of end time is computed at the end of a record.
                        if (dateLock) {
                            end = Calendar.getInstance().getTime();
                            dateLock = false;
                        }
                        stopChrono();
                        if (start != null) {
                            //Log used for debugging, change its visibility on activity_main.xml if needed
                            Log.setText("Today is " + date.format(start) + ", User walks from " + df.format(start) + " to " + df.format(end) + " for " + Double.toString(speed) + " km/h");

                            //dbLock to make sure only one instance of data is computed during a record
                            if (!dbLock) {
                                if (speed <= SPEED_THRESHOLD) {
                                    boolean insertData = recordDB.addData(date.format(start), df.format(start), df.format(end), Integer.toString(numSteps), dist_conv(dist), time_conv(time2), speed_conv(speed));
                                    if (insertData) {
                                        Toast.makeText(MainActivity.this, "New Data Inserted!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to insert new data", Toast.LENGTH_LONG).show();
                                    }
                                    dbLock = true;
                                }
                                else {
                                    boolean insertData = recordDB.addData(date.format(start), df.format(start), df.format(end), Integer.toString(numSteps), dist_conv(dist), "Unknown duration", "Unknown speed");
                                    if (insertData) {
                                        Toast.makeText(MainActivity.this, "New Data Inserted!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to insert new data", Toast.LENGTH_LONG).show();
                                    }
                                    dbLock = true;
                                }
                            }

                            //reset all measurements at the end of the record
                            resetAll();
                        }

                        //reset the currentTick counter
                        currentTick = 0;
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
                    //Click again on start button to reset all measurements
                    Status.setText("Counter reset");
                    resetAll();
                }
                startChrono();
                TvSteps.setText(TEXT_NUM_STEPS + numSteps);
                Distance.setText("Walked distance : " + dist + "m");

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

    /**
     * Overridden method of onAccuracyChanged().
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Overriden method of onSensorChanged().
     * The timestamp and the values are updated here, during the step detection.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    /**
     * Overridden method declared in StepListener interface.
     * This method updates total number of steps, as well as
     * the walked distance.
     * @param timeNs the maximum delay of the step detector's
     *               processing.
     */
    @Override
    public void step(long timeNs){
        numSteps++;
        Status.setText("Counter started");

        //Walking distance calculation by multiplying a step with an average value of 75cm per step
        dist = numSteps * 0.75;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps);
        if (dist <= 1000) {
            Distance.setText("Walked distance : " + dist + "m");
        }
        else {
            Distance.setText("Walked distance : " + dist / 1000f + "km");
        }
    }



}

