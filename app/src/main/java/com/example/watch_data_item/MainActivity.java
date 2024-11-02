package com.example.watch_data_item;


import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {


    private static final String TAG = "SensorData";
    private static final String SENSOR_GYRO_DATA = "/sensor_gyro_data";
    private static final String SENSOR_ACCEL_DATA = "/sensor_accel_data";
    private TextView SensorTextView;
    private TextView SensorAccelTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorTextView = findViewById(R.id.gyroData);
        SensorAccelTextView = findViewById(R.id.accelData);

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = event.getDataItem();
                if (SENSOR_GYRO_DATA.equals(dataItem.getUri().getPath())) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    String sensorData = dataMap.getString("sensor_data");
                    Log.d(TAG, "Data received from watch: " + sensorData);

                    SensorTextView.setText(sensorData);

                }
                if (SENSOR_ACCEL_DATA.equals(dataItem.getUri().getPath())) {
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    String sensorData = dataMap.getString("sensor_data");
                    Log.d(TAG, "Data received from watch: " + sensorData);

                    SensorAccelTextView.setText(sensorData);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

}