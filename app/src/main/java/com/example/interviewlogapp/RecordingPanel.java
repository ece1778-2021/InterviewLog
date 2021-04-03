package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;

public class RecordingPanel extends AppCompatActivity {
    String TAG = "recording";
    Button logoutButton, scheduleButton, teamButton;
    String userID, userType, userName, userTeam;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    RecyclerView recordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_panel);

        db = FirebaseFirestore.getInstance();
        userName = getIntent().getStringExtra("researcherName");
        logoutButton = findViewById(R.id.logoutButton);
        scheduleButton = findViewById(R.id.scheduleButton);
        teamButton = findViewById(R.id.teamButton);
        FirebaseUser user = fAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        recordList = findViewById(R.id.recordList);
        Log.d(TAG, "Researcher Name is  " + userName);
        db.collection("team").whereEqualTo("researcherName", userName).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userTeam = document.getString("teamName");
                        Log.d(TAG, "Researcher Team is " + userTeam);
                    }
                }
            }
        });

        //.whereEqualTo("researcherName", userName)
        Query query = db.collection("Recordings").whereEqualTo("researcherName", userName);
        FirestoreRecyclerOptions<recordListRetrieve> options = new FirestoreRecyclerOptions.Builder<recordListRetrieve>().setQuery(query, recordListRetrieve.class).build();
        //Test to see if query contains correct information
        /*query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("documentID"));
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("tag1"));
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("time"));
                }
            }
        });*/
        adapter = new FirestoreRecyclerAdapter<recordListRetrieve, recordListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull recordListViewHolder holder, int position, @NonNull recordListRetrieve model) {
                holder.partName.setText(model.getPartName());
                holder.time.setText(model.getTime());
                holder.tag1.setText(model.getTag1());
                if(TextUtils.isEmpty(model.getTag2())){
                    holder.tag2.setBackgroundColor(Color.WHITE);
                }
                else{
                    holder.tag2.setText(model.getTag2());
                }

                holder.recordCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RecordingPanel.this,Replay.class);
                        intent.putExtra("record_id", model.getDocumentID());
                        intent.putExtra("partName", model.getPartName());
                        intent.putExtra("researcherName", userName);
                        intent.putExtra("audio_location", model.getStorageRef());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public recordListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_list, parent, false);
                return new recordListViewHolder(view);
            }
        };
        recordList.setLayoutManager(new LinearLayoutManager(this));
        recordList.setAdapter(adapter);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResearcherPanel.class);
                i.putExtra("researcherName", userName);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

        teamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), team_panel.class);
                i.putExtra("researcherName",userName);
                i.putExtra("userTeam", userTeam);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });

    }
    private class recordListViewHolder extends RecyclerView.ViewHolder{
        private TextView partName, tag1, tag2, time;
        private CardView recordCard;
        public recordListViewHolder(@Nonnull View itemView){
            super(itemView);
            partName = itemView.findViewById(R.id.partName);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            time = itemView.findViewById(R.id.timeDisplay);
            recordCard = itemView.findViewById(R.id.recordCard);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}