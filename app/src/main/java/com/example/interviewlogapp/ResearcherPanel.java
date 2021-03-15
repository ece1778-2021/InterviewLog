package com.example.interviewlogapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ResearcherPanel extends AppCompatActivity {
    String TAG = "create_appointment";
    Button logoutButton, addAppointmentButton;
    String userID, userType, userName;
    FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_panel);

        db = FirebaseFirestore.getInstance();
        logoutButton = findViewById(R.id.logoutButton);
        addAppointmentButton = findViewById(R.id.addAppointment);
        FirebaseUser user = fAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userName = value.getString("name");

            }
        });

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
                Intent i = new Intent(getApplicationContext(), create_appointment.class);

                i.putExtra("researcherName",userName);
                startActivity(i);
            }
        });
    }

    public void onRecordClick(View view){
        Intent intent = new Intent(ResearcherPanel.this,Recording.class);
        startActivity(intent);
    }
}