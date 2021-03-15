package com.example.interviewlogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResearcherPanel extends AppCompatActivity {
    Button logoutButton, addAppointmentButton;
    String userID, userType;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_panel);

        logoutButton = findViewById(R.id.logoutButton);
        addAppointmentButton = findViewById(R.id.addAppointment);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });

        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), create_appointment.class));
            }
        });

    }

    public void onRecordClick(View view){
        Intent intent = new Intent(ResearcherPanel.this,Recording.class);
        startActivity(intent);
    }
}