package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ResearcherPanel extends AppCompatActivity {
    String TAG = "researcher";
    Button logoutButton, addAppointmentButton;
    String userID, userType, userName;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter adapter;
    RecyclerView partList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_panel);

        db = FirebaseFirestore.getInstance();
        logoutButton = findViewById(R.id.logoutButton);
        addAppointmentButton = findViewById(R.id.addAppointment);
        FirebaseUser user = fAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        partList = findViewById(R.id.partList);

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userName = value.getString("name");
                Log.d(TAG, "The name is "+userName);

            }
        });
        //.whereEqualTo("researcherName", userName)
        Query query = db.collection("participants");
        FirestoreRecyclerOptions<partListRetrieve> options = new FirestoreRecyclerOptions.Builder<partListRetrieve>().setQuery(query, partListRetrieve.class).build();
        // Test to see if query contains correct information
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("partName"));
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("tag1"));
                    Log.d(TAG, "retrieved "+ documentSnapshot.getString("time"));
                }
            }
        });
        adapter = new FirestoreRecyclerAdapter<partListRetrieve, partListViewHolder>(options) {
            @NonNull
            @Override
            public partListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_list, parent, false);
                return new partListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull partListViewHolder holder, int position, @NonNull partListRetrieve model) {
                holder.partName.setText(model.getPartName());
                holder.time.setText(model.getTime());
                holder.tag1.setText(model.getTag1());
                holder.tag2.setText(model.getTag2());
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
    }

    public void onRecordClick(View view){
        Intent intent = new Intent(ResearcherPanel.this,Recording.class);
        startActivity(intent);
    }

    private class partListViewHolder extends RecyclerView.ViewHolder{
        private TextView partName, tag1, tag2, time;
        public partListViewHolder(@Nonnull View itemView){
            super(itemView);
            partName = itemView.findViewById(R.id.partName);
            tag1 = itemView.findViewById(R.id.tag1);
            tag2 = itemView.findViewById(R.id.tag2);
            time = itemView.findViewById(R.id.timeDisplay);
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