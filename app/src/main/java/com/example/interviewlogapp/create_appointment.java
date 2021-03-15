package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nonnull;

public class create_appointment extends AppCompatActivity {
    String TAG = "create_appointment";
    Button backButton;
    StorageReference storageRef;
    FirestoreRecyclerAdapter adapter;
    RecyclerView allTag;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);
        backButton = findViewById(R.id.backButton);
/*
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        allTag = findViewById(R.id.allTag);
        Query query = db.collection("tags");
        FirestoreRecyclerOptions<tagList> options = new FirestoreRecyclerOptions.Builder<tagList>().setQuery(query, tagList.class).build();
        adapter = new FirestoreRecyclerAdapter<tagList, tagViewHolder>(options) {
            @NonNull
            @Override
            public tagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_list, parent, false);
                return new tagViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull tagViewHolder holder, int position, @NonNull tagList model) {
                holder.tag_list.setText(model.getTag());
                Log.d(TAG, "The comment retrieved is "+ model.getTag());
            }
        };*/

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResearcherPanel.class));
            }
        });
    }
    private class tagViewHolder extends RecyclerView.ViewHolder{
        private CheckBox tag_list;
        public tagViewHolder(@Nonnull View itemView){
            super(itemView);
            tag_list = itemView.findViewById(R.id.tag_list);
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