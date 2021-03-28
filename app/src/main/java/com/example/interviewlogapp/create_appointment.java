package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class create_appointment extends AppCompatActivity {
    String TAG = "create_appointment";
    String userName;
    TextView currentTimeDisplay;
    EditText tagEnter, partNameEnter;
    Button backButton, addTag, createAppointment, startRecordingButton;
    StorageReference storageRef;
    FirestoreRecyclerAdapter adapter;
    RecyclerView allTag;
    FirebaseFirestore db;
    HashMap<String, Boolean> checkedTag = new HashMap<String, Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        userName = getIntent().getStringExtra("researcherName");
        backButton = findViewById(R.id.backButton);
        createAppointment = findViewById(R.id.createAppointment);
        currentTimeDisplay = findViewById(R.id.currentTimeDisplay);
        startRecordingButton = findViewById(R.id.startRecording);
        currentTimeDisplay.setText(Calendar.getInstance().getTime().toString());
        tagEnter = findViewById(R.id.tagEnter);
        partNameEnter = findViewById(R.id.nameEnter);
        addTag = findViewById(R.id.addTag);
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        allTag = findViewById(R.id.allTag);

        Log.d(TAG, "Researcher Name is "+ userName );
        Query query = db.collection("tags");
        FirestoreRecyclerOptions<tagList> options = new FirestoreRecyclerOptions.Builder<tagList>().setQuery(query, tagList.class).build();
        // Test to see if query contains correct information
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Log.d(TAG, "Tag "+ documentSnapshot.getString("tag"));
                }
            }
        });

        adapter = new FirestoreRecyclerAdapter<tagList, tagViewHolder>(options) {
            @NonNull
            @Override
            public tagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_list, parent, false);
                return new tagViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull tagViewHolder holder, int position, @NonNull tagList model) {
                holder.tag_item.setText(model.getTag());
                holder.tag_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.tag_item.isChecked()==false){
                            checkedTag.remove(model.getTag());
                        }
                        else{
                            checkedTag.put(model.getTag(),holder.tag_item.isChecked());
                        }
                        Log.d(TAG, "The item "+ model.getTag()+" is "+holder.tag_item.isChecked());
                    }
                });
                Log.d(TAG, "The tag retrieved is "+ model.getTag());

            }
        };

        allTag.setLayoutManager(new LinearLayoutManager(this));
        allTag.setAdapter(adapter);

        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagInput = tagEnter.getText().toString();
                if(TextUtils.isEmpty(tagInput)){
                    tagEnter.setError("You can't add an empty tag!");
                    return;
                }
                uploadTagToFirebase(tagInput);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ResearcherPanel.class);
                i.putExtra("researcherName",userName);
                startActivity(i);
            }
        });

        createAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String partName = partNameEnter.getText().toString();
                if(TextUtils.isEmpty(partName)){
                    partNameEnter.setError("You can't add an empty participant's name!");
                    return;
                }

                int counter = 0;

                DocumentReference documentReference = db.collection("participants").document();
                Map<String, Object> addPartInfo = new HashMap<>();
                for (String key:checkedTag.keySet()){
                    counter++;
                    addPartInfo.put("tag"+counter, key);
                }
                if (counter==0){
                    Toast.makeText(create_appointment.this,"You forgot to add a Tag", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (counter==1){
                    addPartInfo.put("tag2", "");
                }
                addPartInfo.put("researcherName", userName);
                addPartInfo.put("partName", partName);
                addPartInfo.put("time", currentTimeDisplay.getText().toString());
                addPartInfo.put("status", "Not Started");
                addPartInfo.put("doc_id", documentReference.getId());
                documentReference.set(addPartInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"The part information is uploaded");
                        Toast.makeText(create_appointment.this,"Appointment Successfully Created", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), ResearcherPanel.class);
                        i.putExtra("researcherName",userName);
                        startActivity(i);
                    }
                });
            }
        });

        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String partName = partNameEnter.getText().toString();
                if(TextUtils.isEmpty(partName)){
                    partNameEnter.setError("You can't add an empty participant's name!");
                    return;
                }

                int counter = 0;

                DocumentReference documentReference = db.collection("participants").document();
                Map<String, Object> addPartInfo = new HashMap<>();
                for (String key:checkedTag.keySet()){
                    counter++;
                    addPartInfo.put("tag"+counter, key);
                }
                if (counter==0){
                    Toast.makeText(create_appointment.this,"You forgot to add a Tag", Toast.LENGTH_SHORT).show();
                    return;
                }
                addPartInfo.put("researcherName", userName);
                addPartInfo.put("partName", partName);
                addPartInfo.put("time", currentTimeDisplay.getText().toString());
                addPartInfo.put("status", "Not Started");
                documentReference.set(addPartInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"The part information is uploaded");
                        Toast.makeText(create_appointment.this,"Participant information Created, now recording", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), Recording.class);
                        i.putExtra("researcherName", userName);
                        i.putExtra("partName", partName);
                        i.putExtra("time", currentTimeDisplay.getText().toString());
                        i.putExtra("tag1", addPartInfo.get("tag1").toString());
                        i.putExtra("tag1", addPartInfo.get("tag2").toString());
                        startActivity(i);
                    }
                });
            }
        });
    }

    public void uploadTagToFirebase(String tag){
        DocumentReference documentReference = db.collection("tags").document();
        Map<String, Object> addNewTag = new HashMap<>();
        addNewTag.put("tag", tag);
        documentReference.set(addNewTag).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Tag added with ID: " + documentReference.getId());
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    private class tagViewHolder extends RecyclerView.ViewHolder{
        private CheckBox tag_item;
        public tagViewHolder(@Nonnull View itemView){
            super(itemView);
            tag_item = itemView.findViewById(R.id.tag_item);
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