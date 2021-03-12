package com.example.interviewlogapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserPanel extends AppCompatActivity {
    TextView displayMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    String userID, userName;
    String TAG = "UserPanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);
        displayMsg = findViewById(R.id.displayMsg);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        displayUserName();

    }

    public void displayUserName(){
        FirebaseUser user = fAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userName=value.getString("name");
                Log.d(TAG, "The user name is "+ userName);
                displayMsg.setText("Welcome back "+userName);
                return;
            }
        });
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginPage.class));
        finish();
    }
}