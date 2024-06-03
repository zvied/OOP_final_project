package com.example.carrental_1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close the activity when the back button is pressed
        return true;
    }
}
