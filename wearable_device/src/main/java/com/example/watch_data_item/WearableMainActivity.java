package com.example.watch_data_item;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class WearableMainActivity extends Activity implements SensorEventListener {

    private static final String TAG = "SensorData";
    private static final String SENSOR_GYRO_DATA = "/sensor_gyro_data";
    private static final String SENSOR_ACCEL_DATA = "/sensor_accel_data";
    private TextView gyroDataTextView;
    private TextView accelDataTextView;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wearable);

        gyroDataTextView = findViewById(R.id.gyroData);
        accelDataTextView = findViewById(R.id.accelData);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(accelerometerSensor != null){
            sensorManager.registerListener(this, accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Log.d(TAG, "onSensorChanged: Changed");

            String G_values = "Gyroscope Data : \nX: " + x + "\nY: " + y + "\nZ: " + z;
            sendDataToPhone(G_values);
        }
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Log.d(TAG, "onSensorChanged: Changed");

            String A_values = "Accelerometer Data :\nX: " + x + "\nY: " + y + "\nZ: " + z;
            sendAccelDataToPhone(A_values);
        }

        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String gyroscopeData = "Gyroscope Data : \nX: " + x + "\nY: " + y + "\nZ: " + z;
            gyroDataTextView.setText(gyroscopeData);
        }
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String accelerometerData = "Gyroscope Data : \nX: " + x + "\nY: " + y + "\nZ: " + z;
            accelDataTextView.setText(accelerometerData);
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    private void sendDataToPhone(String G_data) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SENSOR_GYRO_DATA);
        putDataMapRequest.getDataMap().putString("sensor_data", G_data);
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(this).putDataItem(putDataRequest).addOnCompleteListener(new OnCompleteListener<DataItem>() {
            @Override
            public void onComplete(Task<DataItem> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Data sent to phone: " + G_data);
                } else {
                    Log.e(TAG, "Failed to send data to phone.");
                }
            }
        });
    }
    private void sendAccelDataToPhone(String A_data) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SENSOR_ACCEL_DATA);
        putDataMapRequest.getDataMap().putString("sensor_data", A_data);
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.getDataClient(this).putDataItem(putDataRequest).addOnCompleteListener(new OnCompleteListener<DataItem>() {
            @Override
            public void onComplete(Task<DataItem> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Successful depart" + A_data);
                } else {
                    Log.e(TAG, "Failed to arrive");
                }
            }
        });
    }
}
