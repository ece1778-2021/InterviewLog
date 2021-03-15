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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class create_appointment extends AppCompatActivity {
    String TAG = "create_appointment";
    TextView currentTimeDisplay;
    EditText tagEnter;
    Button backButton, addTag;
    StorageReference storageRef;
    FirestoreRecyclerAdapter adapter;
    RecyclerView allTag;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        backButton = findViewById(R.id.backButton);
        currentTimeDisplay = findViewById(R.id.currentTimeDisplay);
        currentTimeDisplay.setText(Calendar.getInstance().getTime().toString());
        tagEnter = findViewById(R.id.tagEnter);
        addTag = findViewById(R.id.addTag);
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        allTag = findViewById(R.id.allTag);

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
                Log.d(TAG, "We are in");
                return new tagViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull tagViewHolder holder, int position, @NonNull tagList model) {
                holder.tag_item.setText(model.getTag());
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
                startActivity(new Intent(getApplicationContext(), ResearcherPanel.class));
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