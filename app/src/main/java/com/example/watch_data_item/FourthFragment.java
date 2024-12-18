package com.example.watch_data_item;

import static android.util.Half.EPSILON;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// This is my Fourth Fragment which is the main test for using the smartwatch data and saves it to the UniqueFileName created
public class FourthFragment extends Fragment implements DataClient.OnDataChangedListener {
    // Initialization
    private static final String TAG = "FourthFragment";
    private static final String SENSOR_GYRO_DATA = "/sensor_gyro_data";
    private FileWriter csvWriter;
    private LineChart lineChart;
    private LineDataSet lineDataSet;
    private LineDataSet predefinedPathDataSet;
    private LineData lineData;
    private List<Entry> dotEntries;
    private List<Entry> predefinedPathEntries;
    private float currentX = 0f;
    private float currentY = 0f;
    private String csvFileName;
    private TextView accuracyErrorText;

    // this inflater inflates the View with fragment_fourth
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);

        // We are retrieving the file name from fragment_third which we set in bundle using key "csvFileName"
        csvFileName = getArguments() != null ? getArguments().getString("csvFileName") : null;

        // Sets the view chart
        lineChart = view.findViewById(R.id.lineChart);
        // Show the accuracy of the  path followed
        accuracyErrorText = view.findViewById(R.id.accuracyErrorText);

        // Calls the method setupChart with predefined path
        setupChart();
        generatePredefinedPath();

        // calls the file and path for the csvFileName which was given from ThirdFragment to write in
        if (csvFileName != null) {
            try {
                File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File csvFile = new File(storageDir, csvFileName);
                csvWriter = new FileWriter(csvFile, true);
            } catch (IOException e) {
                Log.e(TAG, "Failed to initialize CSV writer: " + e.getMessage());
            }
        }
        // This sets onclick listener button toFragment5 to call Navigation nav_graph and find action_fourthFragment_to_fifthFragment to change fragment
        view.findViewById(R.id.toFragment5).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("csvFileName", csvFileName);
            Navigation.findNavController(view).navigate(R.id.action_fourthFragment_to_fifthFragment, bundle);
        });

        return view;
    }

    // Set up chart method for our in real time tracking
    private void setupChart() {
        // sets the array list for dot entries
        dotEntries = new ArrayList<>();
        lineDataSet = new LineDataSet(dotEntries, "Gyroscope Data");
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleColor(getResources().getColor(android.R.color.holo_red_dark));
        lineDataSet.setColor(getResources().getColor(android.R.color.holo_red_dark));

        // sets new data set to send to lineChart X and Y
        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);

        // X axis setup
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(1f);
        xAxis.setAxisMinimum(-10f);
        xAxis.setAxisMaximum(10f);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Y axis setup
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setGridLineWidth(1f);
        yAxisLeft.setAxisMinimum(-10f);
        yAxisLeft.setAxisMaximum(10f);
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setGranularityEnabled(true);

        // removes the right side graph Y axis markers and numbers
        lineChart.getAxisRight().setEnabled(false);
    }

    // On Data changed interface that uses a listener for when the smartwatch sends data through the data layer api
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                if (SENSOR_GYRO_DATA.equals(event.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    // only receiving gyroscope x,y data and timestamp of change
                    float X = dataMapItem.getDataMap().getFloat("X");
                    float Y = dataMapItem.getDataMap().getFloat("Y");
                    String formattedDate = dataMapItem.getDataMap().getString("FormattedDate");

                    // sending x,y value to the improveAccuracy method
                    improveAccuracy(X, Y);
                    // sending x,y value to the calculate error method
                    calculateAndDisplayError(X, Y);

                    //appending the data from the onDataChanged Listener to the csv. file
                    try {
                        if (csvWriter != null) {
                            csvWriter.append(formattedDate).append(",\nX: ").append(String.valueOf(X)).append(", Y: ")
                                    .append(String.valueOf(Y)).append("\n");
                            csvWriter.flush();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to write to CSV: " + e.getMessage());
                    }
                }
            }
        }
    }

    // This method compensates for the sensitivity that can occur with the sensor
    private void improveAccuracy(float X, float Y) {
        currentX += X * 0.09f;
        currentY += Y * 0.09f;
        positionTracking(currentX, currentY);
    }

    // positionTracking method get the same entries from improveAccuracy method and sets them as set values in an array
    private void positionTracking(float x, float y) {
        if (dotEntries.size() > 1) {
            dotEntries.clear();
        }

        // Sends to lineDataSet for the graph which notifies and clears the previous set from lineChart
        dotEntries.add(new Entry(x, y));
        lineDataSet.notifyDataSetChanged();
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }
    private void generatePredefinedPath() {
        predefinedPathEntries = new ArrayList<>();


        for (int i = 0; i <= 300; i++) {
            float x = i * 0.1f;
            float y = (float) Math.sin(x);
            predefinedPathEntries.add(new Entry(x, y));
        }


        predefinedPathDataSet = new LineDataSet(predefinedPathEntries, "Predefined Path");
        predefinedPathDataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        predefinedPathDataSet.setDrawCircles(false);
        predefinedPathDataSet.setLineWidth(1.5f);


        // Add the predefined path dataset to the line data
        lineData.addDataSet(predefinedPathDataSet);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    // displays the error percent to the textView
    private void calculateAndDisplayError(float measuredX, float measuredY) {
        float acceptedX = (float) Math.sin(currentX); // Expected predefined path value
        float acceptedY = (float) Math.sin(currentY); // Using the predefined path


        float errorX = calculatePercentError(acceptedX, measuredX);
        float errorY = calculatePercentError(acceptedY, measuredY);


        String errorMessage = String.format("Accuracy Error: X: %.2f%%, Y: %.2f%%", errorX, errorY);
        accuracyErrorText.setText(errorMessage);
    }

    // To calculate the percent error of your path vs the predefined path
    private float calculatePercentError(float acceptedValue, float measuredValue) {
        if (Math.abs(acceptedValue) < EPSILON) {
            return 0f; // Avoid division by zero if accepted value is very small
        }
        float error = Math.abs((acceptedValue - measuredValue) / acceptedValue) * 100f;
        return Math.min(error, 100f);
    }


    // Uses the wearable client to listen for OnDataChanged from the Wearable
    @Override
    public void onResume() {
        super.onResume();
        Wearable.getDataClient(requireContext()).addListener(this);
    }

    // stays idle for when data does not get received
    @Override
    public void onPause() {
        super.onPause();
        Wearable.getDataClient(requireContext()).removeListener(this);

        // once the listener is stopped it closes the csv. file
        try {
            if (csvWriter != null) {
                csvWriter.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to close CSV writer: " + e.getMessage());
        }
    }
}

