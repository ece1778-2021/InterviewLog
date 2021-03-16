package com.example.interviewlogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class testActivity extends AppCompatActivity {
    String TAG= "testactivity";
    FirebaseFirestore db;
    String tag;
    int timeStamp;
    RecyclerView clipList;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<String> clipNames;
    ArrayList<Integer> clipTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        clipList = findViewById(R.id.clipListTest);
        db = FirebaseFirestore.getInstance();
        String record_id = "d5b638ff-58d6-481c-b2a2-a8c9e6555179";
        clipNames = new ArrayList<>();
        clipTimes = new ArrayList<>();
        db.collection("Recordings").document(record_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            int clips = documentSnapshot.getLong("Total_Clip").intValue();
                            for (int i=0; i<clips; i++){
                                tag = documentSnapshot.getString("Clip_Tag"+i);
                                timeStamp = documentSnapshot.getLong("clip"+i).intValue();
                                clipNames.add(tag);
                                clipTimes.add(timeStamp);

                                //Log.d(TAG, "Timestamp "+i + " at " +timeStamp);
                            }
                            Log.d(TAG, "clip names are "+clipNames);
                            mAdapter = new clipAdapter(clipNames, clipTimes);
                            mLayoutManager = new LinearLayoutManager(testActivity.this);
                            clipList.setLayoutManager(mLayoutManager);
                            clipList.setAdapter(mAdapter);
                        }
                    }
                });
    }
}