package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.speech.v1beta1.Speech;
import com.google.api.services.speech.v1beta1.SpeechRequestInitializer;
import com.google.api.services.speech.v1beta1.model.RecognitionAudio;
import com.google.api.services.speech.v1beta1.model.RecognitionConfig;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class testActivity extends AppCompatActivity {
    String TAG= "testactivity";
    FirebaseFirestore db;
    String userID, userType, userName, userTeam, ans;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    RecyclerView sharedRecordingList;
    private final String CLOUD_API_KEY = "AIzaSyAQXXRS7Z3VG4PVKhpfh0lnig09zGf3PQc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        Button browseButton = (Button)findViewById(R.id.browse_button);
        db = FirebaseFirestore.getInstance();
        userName = intent.getStringExtra("researcherName");
        userTeam = intent.getStringExtra("userTeam");





        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
                filePicker.setType("audio/flac");
                startActivityForResult(filePicker, 1);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            final Uri soundUri = data.getData();
            // More code here

        }
    }

}