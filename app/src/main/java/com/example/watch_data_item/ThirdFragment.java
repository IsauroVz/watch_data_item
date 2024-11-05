package com.example.watch_data_item;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// This is my third Fragment which handles the input data from the patient and moves to the next fourth fragment
public class ThirdFragment extends Fragment {
    // Initialized
    private String uniqueFileName;
    private EditText firstNameEditText, lastNameEditText, ageEditText, weightEditText, heightEditText, genderEditText;

    // this inflater inflates the View with fragment_third
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        // sets variable values from user input
        firstNameEditText = view.findViewById(R.id.Firstname);
        lastNameEditText = view.findViewById(R.id.Lastname);
        ageEditText = view.findViewById(R.id.Age);
        weightEditText = view.findViewById(R.id.Weight);
        heightEditText = view.findViewById(R.id.Height);
        genderEditText = view.findViewById(R.id.Gender);

        // calls the method generateUniqueCsvFileName to create a new csv. file
        generateUniqueCsvFileName();

        // uses the on click listener for my button called toFragment4 to write the input data.
        // It also calls object navigation to find the next view to inflate from nav_graph with the id of action_thirdFragment_to_fourthFragment
        view.findViewById(R.id.toFragment4).setOnClickListener(v -> {
            if (writeDataToCsv()) {
                Bundle bundle = new Bundle();
                bundle.putString("csvFileName", uniqueFileName);
                Navigation.findNavController(view).navigate(R.id.action_thirdFragment_to_fourthFragment, bundle);
            } else {
                Toast.makeText(getContext(), "Failed to save data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Generates the unique file name using the date and timestamp format
    private void generateUniqueCsvFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        uniqueFileName = "PatientData_" + timeStamp + ".csv";

        // creating a file directory for the file if it doesn't exist
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        // Assign the uniqueFilename as the new file in the StorageDir
        File csvFile = new File(storageDir, uniqueFileName);

        // Starts appending the header when a new file is created
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("First,Last,Age,Weight,Height,Gender\n");
            writer.flush();
            Toast.makeText(getContext(), "File created at: " + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("ThirdFragment", "Failed to create CSV file: " + e.getMessage());
        }
    }

    // writes my data to csv method
    private boolean writeDataToCsv() {
        // converting to string from edit texts and trimming space characters
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();
        String height = heightEditText.getText().toString().trim();
        String gender = genderEditText.getText().toString().trim();

        // this is a validation statement so that all inputs are required to fill in
        if (firstName.isEmpty() || lastName.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty() || gender.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // calls the path and context of this fragment from its file directory
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File csvFile = new File(storageDir, uniqueFileName);

        // for header if method is called before the file is created
        boolean isNewFile = !csvFile.exists();

        // Path:Android Studio Device File Explorer:
        // /storage/emulated/0/Android/data/com.example.watch_data_item/files/Documents/

        // this writes to the uniqueFileName using comma separated values
        try (FileWriter writer = new FileWriter(csvFile, true)) {
            // Write header if a new file is created
            if (isNewFile) {
                writer.append("First,Last,Age,Weight,Height,Gender\n");
            }
            // Append user data
            writer.append(firstName).append(",")
                    .append(lastName).append(",")
                    .append(age).append(",")
                    .append(weight).append(",")
                    .append(height).append(",")
                    .append(gender).append("\n\n");
            writer.flush();
            return true;
        } catch (IOException e) {
            Log.e("ThirdFragment", "Failed to write to CSV file: " + e.getMessage());
            return false;
        }
    }
}