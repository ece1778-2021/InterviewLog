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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Recording extends AppCompatActivity {
    private MediaRecorder mediaRecorder;
    private String output_file;
    private boolean isRecording = false;
    private int test = 0;
    private Chronometer timer;
    private FirebaseStorage storage;
    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        timer = findViewById(R.id.time_count);
        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
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
            test++;
            TextView txtUsername = findViewById(R.id.textView);
            txtUsername.setText("Make clip! "+ test);
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
        upload();
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

    private void upload(){
        final String randomKey = UUID.randomUUID().toString();
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

    public void onSaveClick(View view){
        Intent intent = new Intent(Recording.this,Replay.class);
        startActivity(intent);
    }

    //    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
    //        android:requestLegacyExternalStorage="true"
}