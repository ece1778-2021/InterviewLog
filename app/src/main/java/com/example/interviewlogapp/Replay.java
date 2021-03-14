package com.example.interviewlogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.io.IOException;

public class Replay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
    }

    public void onReplayClick(View view){
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            String audio = "https://firebasestorage.googleapis.com/v0/b/interviewlogapp.appspot.com/o/Audio%2Ftest.3gp?alt=media&token=ec4b473f-c2e9-4784-8558-d9d20a882a66";
            mediaPlayer.setDataSource(audio);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mp){
                    mp.start();
                }
            });
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
}