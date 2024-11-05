package com.example.watch_data_item;

import android.app.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WearableMainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private static final String TAG = "SensorData";
    private static final String SENSOR_GYRO_DATA = "/sensor_gyro_data";
    private Button ToggleSensorButton;
    private boolean SensorAccess = false;
    boolean isColor = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wearable);

        ToggleSensorButton = findViewById(R.id.start_button);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        ToggleSensorButton.setOnClickListener(v -> SensorButton());

    }

    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];

            Log.d(TAG, "onSensorChanged: Changed");

            sendDataToPhone(x,y);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    private void sendDataToPhone(float x, float y) {
        long timestamp = System.currentTimeMillis(); // Capture current timestamp
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(new Date(timestamp));
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SENSOR_GYRO_DATA);

        putDataMapRequest.getDataMap().putFloat("X", x);
        putDataMapRequest.getDataMap().putFloat("Y", y);
        putDataMapRequest.getDataMap().putString("FormattedDate", formattedDate);

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(this).putDataItem(putDataRequest).addOnCompleteListener(new OnCompleteListener<DataItem>() {
            @Override
            public void onComplete(Task<DataItem> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Data sent to phone: Gyroscope" +
                            "\nX: "+ x +
                            "\nY: "+ y +
                            "\nFormattedDate: " + formattedDate);
                } else {
                    Log.e(TAG, "Failed to send data to phone.");
                }
            }
        });
    }

    private void SensorButton(){
        if (SensorAccess){
            stopSensor();
        }else {
            startSensor();
        }
    }

    private void startSensor() {
        if (!SensorAccess) {
            SensorAccess = true;
            if (gyroscopeSensor != null) {
                sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        ToggleSensorButton.setText("DONE");
    }
    private void stopSensor() {
        if (SensorAccess) {
            SensorAccess = false;
            sensorManager.unregisterListener(this);
        }
        ToggleSensorButton.setText("BEGIN");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SensorAccess) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (SensorAccess) {
            sensorManager.unregisterListener(this);
        }
    }
}