package com.example.agenda.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.agenda.R;

//******Omega Team*****


public class MainActivity extends AppCompatActivity {
    Button start_now;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_now = findViewById(R.id.Start_Now_btn);
        start_now.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AllNotes.class);
            startActivity(intent);
        });

    }
}