package com.example.watch_data_item;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

// This is the fifth fragment which displays the data from the uniqueFileName
public class FifthFragment extends Fragment {
    private static final String TAG = "FifthFragment";
    private String csvFileName;

    // this inflater inflates the View with fragment_fifth
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fifth, container, false);

        // We are retrieving the file name from fragment_third which we set in bundle using key "csvFileName"
        csvFileName = getArguments() != null ? getArguments().getString("csvFileName") : null;

        // Displaying the file in this textview
        TextView csvDataTextView = view.findViewById(R.id.csvDataTextView);

        // Reads the file and sets the text from the file on the text view
        if (csvFileName != null) {
            String csvData = readCsvFile();
            csvDataTextView.setText(csvData);
        } else {
            csvDataTextView.setText("CSV file not found.");
        }

        return view;
    }

    // Read Csv file method from
    private String readCsvFile() {
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File csvFile = new File(storageDir, csvFileName);

        // uses string builder to write each character and buffer reader to not exceed string builder limit
        StringBuilder csvContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                csvContent.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to read CSV file: " + e.getMessage());
            return "Error reading CSV file.";
        }
        // returns the content of csvFileName
        return csvContent.toString();
    }
    // Path:Android Studio Device File Explorer:
    // /storage/emulated/0/Android/data/com.example.watch_data_item/files/Documents/
}
