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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class ResearcherPanel extends AppCompatActivity {
    String TAG = "researcher";
    Button logoutButton, addAppointmentButton, recordingButton, teamButton;
    String userID, userTeam, userName, part_id;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    RecyclerView partList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_panel);
        userName = getIntent().getStringExtra("researcherName");
        db = FirebaseFirestore.getInstance();
        logoutButton = findViewById(R.id.logoutButton);
        addAppointmentButton = findViewById(R.id.addAppointment);
        recordingButton = findViewById(R.id.allVoiceButton);
        teamButton = findViewById(R.id.teamButton);
        FirebaseUser user = fAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        partList = findViewById(R.id.partList);

        Log.d(TAG, "user name at schedule page is "+userName);
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

        Query query = db.collection("participants").whereEqualTo("researcherName", userName);
        FirestoreRecyclerOptions<partListRetrieve> options = new FirestoreRecyclerOptions.Builder<partListRetrieve>().setQuery(query, partListRetrieve.class).build();
        // Test to see if query contains correct information
        /*query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("partName"));
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("tag1"));
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("time"));
                }
            }
        });*/
        adapter = new FirestoreRecyclerAdapter<partListRetrieve, partListViewHolder>(options) {
            @NonNull
            @Override
            public partListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_list, parent, false);
                return new partListViewHolder(view);
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void onBindViewHolder(@NonNull partListViewHolder holder, int position, @NonNull partListRetrieve model) {
                holder.partName.setText(model.getPartName());
                holder.time.setText(model.getTime());
                holder.tag1.setText(model.getTag1());

                String currentStatus = model.getStatus();
                Log.d(TAG, currentStatus);
                holder.status.setText(currentStatus);
                if (currentStatus.equals("Not Started")){
                    holder.status.setBackgroundColor(Color.RED);
                    holder.partCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getApplicationContext(), Recording.class);
                            i.putExtra("researcherName", userName);
                            i.putExtra("partName", model.getPartName());
                            i.putExtra("time", model.getTime());
                            i.putExtra("tag1", model.getTag1());
                            i.putExtra("tag2", model.getTag2());
                            i.putExtra("part_id", model.getDoc_id());
                            startActivity(i);
                        }
                    });
                }
                else{
                    holder.status.setBackgroundColor(65280);
                    holder.status.setBackgroundColor(Color.GREEN);
                }
                if(model.getTag2().isEmpty()){
                    holder.tag2.setBackgroundColor(16777215);
                    Log.d(TAG,"equals");
                    return;
                }
                else{
                    holder.tag2.setText(model.getTag2());
                }
            }
        };


        partList.setLayoutManager(new LinearLayoutManager(this));
        partList.setAdapter(adapter);

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

        recordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RecordingPanel.class);
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
    public void test(View view){
        Intent i = new Intent(getApplicationContext(), testActivity.class);
        i.putExtra("researcherName",userName);
        i.putExtra("userTeam", userTeam);
        startActivity(i);
    }

    public void onRecordClick(View view){
        Intent intent = new Intent(ResearcherPanel.this,Recording.class);
        startActivity(intent);
    }

    private class partListViewHolder extends RecyclerView.ViewHolder{
        private TextView partName, tag1, tag2, time, status;
        private CardView partCard;
        public partListViewHolder(@Nonnull View itemView){
            super(itemView);
            partName = itemView.findViewById(R.id.partName);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            time = itemView.findViewById(R.id.timeDisplay);
            status = itemView.findViewById(R.id.status);
            partCard = itemView.findViewById(R.id.partCard);
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