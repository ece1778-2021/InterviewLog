package com.example.interviewlogapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class team_panel extends AppCompatActivity {
    String TAG= "team_panel";
    FirebaseFirestore db;
    String userID, userType, userName, userTeam, recordID;
    TextView teamName;
    FirestoreRecyclerAdapter adapter;
    RecyclerView sharedRecordingList;
    Button logoutButton, recordingButton, scheduleButton, teamListButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_panel);
        Intent intent = getIntent();
        teamName = findViewById(R.id.teamDisplay);
        logoutButton = findViewById(R.id.logoutButton);
        recordingButton = findViewById(R.id.allVoiceButton);
        scheduleButton = findViewById(R.id.scheduleButton);
        teamListButton = findViewById(R.id.teamListButton);

        db = FirebaseFirestore.getInstance();
        userName = intent.getStringExtra("researcherName");
        userTeam = intent.getStringExtra("userTeam");
        sharedRecordingList = findViewById(R.id.sharedList);
        teamName.setText("Team Name: "+userTeam);
        Query query = db.collection("sharedRecordings").whereEqualTo("teamName", userTeam);
        FirestoreRecyclerOptions<sharedListRetrieve> options = new FirestoreRecyclerOptions.Builder<sharedListRetrieve>().setQuery(query, sharedListRetrieve.class).build();
        // Test to see if query contains correct information
        /*query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("teamName"));
                }
            }
        });*/
        adapter = new FirestoreRecyclerAdapter<sharedListRetrieve, team_panel.sharedRecordListViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull team_panel.sharedRecordListViewHolder holder, int position, @NonNull sharedListRetrieve model) {
                Log.d(TAG, "1");
                holder.partName.setText(model.getPartName());
                holder.time.setText(model.getTime());
                holder.tag1.setText(model.getTag1());
                holder.researcherLabel.setText("by: "+model.getResearcherName());
                holder.shareRecordCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(team_panel.this,Replay_Command.class);
                        intent.putExtra("researcherName", userName);
                        intent.putExtra("record_id", model.getDocumentID());
                        startActivity(intent);
                    }
                });
                if(model.getTag2().isEmpty()){
                    holder.tag2.setBackgroundColor(Color.WHITE);
                }
                else{
                    holder.tag2.setText(model.getTag2());
                }
            }

            @NonNull
            @Override
            public team_panel.sharedRecordListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "2");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_list, parent, false);
                return new sharedRecordListViewHolder(view);
            }
        };
        sharedRecordingList.setLayoutManager(new LinearLayoutManager(this));
        sharedRecordingList.setAdapter(adapter);

        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResearcherPanel.class);
                i.putExtra("researcherName", userName);
                startActivity(i);
                overridePendingTransition(0, 0);
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

        teamListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TeamMember.class);
                i.putExtra("researcherName", userName);
                i.putExtra("userTeam", userTeam);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });
    }



    public class sharedRecordListViewHolder extends RecyclerView.ViewHolder{
        private TextView partName, tag1, tag2, time, researcherLabel;
        private CardView shareRecordCard;
        public sharedRecordListViewHolder(@NonNull View itemView) {
            super(itemView);
            partName = itemView.findViewById(R.id.sharePartName);
            tag1 = itemView.findViewById(R.id.shareTag1);
            tag2 = itemView.findViewById(R.id.shareTag2);
            time = itemView.findViewById(R.id.shareTimeDisplay);
            researcherLabel = itemView.findViewById(R.id.researcherLabel);
            shareRecordCard = itemView.findViewById(R.id.sharedRecordCard);
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