package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TeamMember extends AppCompatActivity {
    String TAG = "teamList";
    Button backtoTeam, addMember;
    EditText addMemberID;
    String userName, userTeam;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    RecyclerView teamMemberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member);
        db = FirebaseFirestore.getInstance();
        userName = getIntent().getStringExtra("researcherName");
        userTeam = getIntent().getStringExtra("userTeam");
        Log.d(TAG, "Researcher Name is  " + userName);

        backtoTeam= findViewById(R.id.backToTeam);
        addMember = findViewById(R.id.addMemberButton);
        teamMemberList = findViewById(R.id.teamMemberList);
        addMemberID = findViewById(R.id.addMemberID);

        Query query = db.collection("team").whereEqualTo("teamName", userTeam);
        FirestoreRecyclerOptions<teamMemberRetrieve> options = new FirestoreRecyclerOptions.Builder<teamMemberRetrieve>().setQuery(query, teamMemberRetrieve.class).build();
        //Test to see if query contains correct information
        /*query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("researcherName"));
                }
            }
        });*/
        adapter = new FirestoreRecyclerAdapter<teamMemberRetrieve, teamMemberViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull teamMemberViewHolder holder, int position, @NonNull teamMemberRetrieve model) {
                holder.teamMember.setText(model.getResearcherName());
                holder.deleteMemberButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection("team").whereEqualTo("researcherName",model.getResearcherName()).get().addOnCompleteListener(TeamMember.this, new OnCompleteListener<QuerySnapshot>() {
                            String docID;
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    docID = document.getString("teamNameID");
                                    Log.d(TAG, "Team name ID retrieve is  " + docID);
                                    db.collection("team").document(docID).delete();
                                    Log.d(TAG, "Deleted");
                                }
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public teamMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_list, parent, false);
                return new teamMemberViewHolder(view);
            }
        };
        teamMemberList.setLayoutManager(new LinearLayoutManager(this));
        teamMemberList.setAdapter(adapter);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newMember = addMemberID.getText().toString();
                if(TextUtils.isEmpty(newMember)){
                    addMemberID.setError("You can't add an empty participant's name!");
                    return;
                }
                Log.d(TAG, newMember);
                DocumentReference documentReference = db.collection("team").document();
                Map<String, String> addTeamMember = new HashMap<>();
                addTeamMember.put("researcherName", newMember);
                addTeamMember.put("teamName", userTeam);
                addTeamMember.put("teamNameID", documentReference.getId());
                documentReference.set(addTeamMember).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "New team member added is "+userName+" in the team of "+userTeam);
                        Toast.makeText(TeamMember.this,newMember + " is added!" , Toast.LENGTH_SHORT).show();
                        addMemberID.setText("");
                        Intent i = new Intent(getApplicationContext(), TeamMember.class);
                        i.putExtra("researcherName", userName);
                        i.putExtra("userTeam", userTeam);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                    }
                });
            }
        });
        backtoTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), team_panel.class);
                i.putExtra("researcherName", userName);
                i.putExtra("userTeam", userTeam);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
    }


    private class teamMemberViewHolder extends RecyclerView.ViewHolder {
        private TextView teamMember;
        private Button deleteMemberButton;
        public teamMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            teamMember = itemView.findViewById(R.id.teamMember);
            deleteMemberButton = itemView.findViewById(R.id.deleteMember);
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