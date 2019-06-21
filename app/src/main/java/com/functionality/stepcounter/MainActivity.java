package com.functionality.stepcounter;

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
import com.functionality.stepdetector.*;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements SensorEventListener, com.listener.StepListener {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps = 0;
    private double dist = 0;
    private long time = 0;
    private TextView TvSteps;
    private TextView Status;
    private TextView Distance;
    //private TextView Timing;
    private Button BtnStart;
    private Button BtnStop;
    private Chronometer Timing;
    private long pauseOffset = 0;
    private boolean running = false;
    private TextView Speed;
    private int speed = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        Timing = (Chronometer) findViewById(R.id.timer);
        Timing.setFormat("Walked Time: %s");
        Timing.setBase(SystemClock.elapsedRealtime());

        Timing.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                time = SystemClock.elapsedRealtime() - Timing.getBase();
                speed = (int)(dist/((double)time/1000)*3.6);
                Speed.setText("Walking speed : " + speed + "km/h");
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

    /*private String timeConv (int time) {
        String res = "";
        int sec = 0;
        int min = 0;
        int hr = 0;
        if (time < 60) {
            res += time + "s";
        }
        else {
            if (60 <= time && time < 3600) {
                min = time/60;
                sec = time%60;
                res += min + "m" + sec + "s";
            }
            else {
                hr = time/3600;
                min = (time%3600)/60;
                sec = (time%3600)%60;
                res += hr + "h" + min + "m" + sec + "s";
            }
        }
        return res;
    }*/

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
    }



}

