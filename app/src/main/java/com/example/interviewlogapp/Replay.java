package com.example.interviewlogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Replay extends AppCompatActivity {
    private Runnable runnable;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String record_id = intent.getStringExtra("record_id");
        //String audio_copy;
        db.collection("Photos").document(record_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String audio = documentSnapshot.getString("storageRef");
                            set_media_player(audio);
                        }else{
                            Toast.makeText(Replay.this, "Document does not exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        seekBar = findViewById(R.id.seekBar);

        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this,500);
            }
        };

        int d = mediaPlayer.getDuration();
        String duration = convertFormat(d);

        TextView PlayerDuration = findViewById(R.id.PlayerDuration);
        PlayerDuration.setText(duration);
    }

    private void set_media_player(String audio){
        //mediaPlayer.
        try {
            mediaPlayer.setDataSource(audio);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String convertFormat(int duration){
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void onReplayClick(View view){
        if(view.getId() == R.id.music_play_button){
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            handler.postDelayed(runnable,0);
        }else if(view.getId() == R.id.music_stop_button){
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
        }

    }
}