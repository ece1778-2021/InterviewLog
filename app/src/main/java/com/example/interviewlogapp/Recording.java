package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Recording extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private String output_file;
    private boolean isRecording = false;
    private int clip_amount = 0;
    private Chronometer timer;
    private FirebaseStorage storage;
    private StorageReference reference;
    private FirebaseFirestore db;
    private Map<String, Object> note = new HashMap<>();
    //FirebaseAuth fAuth;
    String userName, part_id, partName, time, tag1, tag2;
    String TAG = "singleRecording";
    List<String> userTeams = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        timer = findViewById(R.id.time_count);
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        userName = intent.getStringExtra("researcherName");
        part_id = intent.getStringExtra("part_id");
        partName = intent.getStringExtra("partName");
        time = Calendar.getInstance().getTime().toString();
        tag1 = intent.getStringExtra("tag1");
        tag2 = intent.getStringExtra("tag2");
        note.put("researcherName", userName);
        note.put("partName", partName);
        note.put("time", time);
        note.put("tag1", tag1);
        note.put("tag2", tag2);
        db.collection("team").whereEqualTo("researcherName", userName).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userTeams.add(document.getString("teamName"));
                    }
                }
            }
        });
    }

    private String convertFormat(long duration){
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void onRecordingClick(View view){
        Log.d("Debugging", "Into Recordding!");
        if(view.getId() == R.id.record_button){
            //record the  audio
            if(checkPermissions() && !isRecording){
                isRecording = true;
                TextView txtUsername = findViewById(R.id.textView);
                txtUsername.setText("Start Recording!");
                Log.d("Debugging", "Go to record");
                record();
            }
        }else if(view.getId() == R.id.stop_button){
            //stop
            if(isRecording){
                TextView txtUsername = findViewById(R.id.textView);
                txtUsername.setText("Stop Recording!");
                isRecording = false;
                stopAudio();
            }
        }else if(view.getId() == R.id.make_clip_button){
            Log.d("Debugging", "Record");
            //push this time to list of makeclip
            long pauseOffset = SystemClock.elapsedRealtime() - timer.getBase();
            String offset = convertFormat(pauseOffset);
            String clip_name = "clip"+String.valueOf(clip_amount);
            note.put(clip_name,pauseOffset);

            EditText TextTag = findViewById(R.id.Input_tag);
            String Tag = TextTag.getText().toString();
            String tag_name = "Clip_Tag"+String.valueOf(clip_amount);
            note.put(tag_name,Tag);

            clip_amount++;

            TextView txtUsername = findViewById(R.id.textView);
            txtUsername.setText(Tag+" "+ offset);
        }
    }

    private boolean checkPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            return false;
        }
    }


    private void stopAudio() {
        timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void record() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        Log.d("Debugging", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("Debugging", "Get the path");
        String file = "Filename.3gp";
        output_file = path + File.separator + file;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(path + File.separator + file);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        Log.d("Debugging", "Prepare");
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Debugging", "Start Recording");
        mediaRecorder.start();
    }

    public void onSaveClick(View view){

        String randomKey = UUID.randomUUID().toString();
        StorageReference filepath = reference.child("Audio"+"/"+randomKey);
        Uri uri = Uri.fromFile(new File(output_file));
        filepath.putFile(uri).
                addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            //how to save to database
                            Uri StorageReference = task.getResult();
                            db.collection("participants").document(part_id).update("status", "Completed");
                            note.put("storageRef",StorageReference.toString());
                            note.put("Total_Clip",clip_amount);
                            note.put("documentID",randomKey);
                            db.collection("Recordings").document(randomKey).set(note);
                            Map<String, Object> sharedInfo = new HashMap<>();
                            for (int i = 0; i<userTeams.size();i++){
                                sharedInfo.put("documentID", randomKey);
                                sharedInfo.put("researcherName", userName);
                                sharedInfo.put("partName", partName);
                                sharedInfo.put("tag1",tag1);
                                sharedInfo.put("tag2",tag2);
                                sharedInfo.put("teamName",userTeams.get(i));
                                sharedInfo.put("time",time);
                                db.collection("sharedRecordings").document().set(sharedInfo);
                            }
                            Intent intent = new Intent(Recording.this,RecordingPanel.class);
                            intent.putExtra("record_id", randomKey);
                            intent.putExtra("researcherName",userName);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Recording.this, "Upload Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Recording.this, "Upload Error", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void onBackClick(View view) {
        Intent i = new Intent(getApplicationContext(), ResearcherPanel.class);
        i.putExtra("researcherName",userName);
        startActivity(i);
    }
}