package com.example.watch_data_item;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

// This is the Main Activity
// Nothing is shown here because the fragments exist only in the this one activity, the main activity/
// essentially like a picture in a picture frame.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

}