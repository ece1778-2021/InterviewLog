package com.example.interviewlogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Replay extends AppCompatActivity {
    private Runnable runnable;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private FirebaseFirestore db;
    private String audio;
    private String record_id;
    private long clip_num;
    String TAG = "replay";
    String tag;
    String username, userName;
    RecyclerView clipList;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<String> clipNames;
    ArrayList<String> clipTimes;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay);
        db = FirebaseFirestore.getInstance();
        clipList = findViewById(R.id.clipList);
        Intent intent = getIntent();
        userName = intent.getStringExtra("researcherName");
        clipNames = new ArrayList<>();
        clipTimes = new ArrayList<>();
        FirebaseUser user = fAuth.getInstance().getCurrentUser();

        record_id = intent.getStringExtra("record_id");
        db.collection("Recordings").document(record_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            audio = documentSnapshot.getString("storageRef");
                            //Log.d("Debugging", audio);
                            username = documentSnapshot.getString("partName");
                            TextView Researcher_name = findViewById(R.id.Researcher_name);
                            clip_num = documentSnapshot.getLong("Total_Clip");
                            //clip_num = Integer.valueOf(documentSnapshot.getString("Total_Clip"));
                            Researcher_name.setText(username);
                            setMediaPlayer();



                            for (int i=0; i<clip_num; i++){
                                tag = documentSnapshot.getString("Clip_Tag"+i);
                                String time = convertFormat(documentSnapshot.getLong("clip"+i).intValue());

                                clipNames.add(tag);
                                clipTimes.add(time);
                                //Log.d(TAG, "Timestamp "+i + " at " +timeStamp);
                            }
                            Log.d(TAG, "clip names are "+clipNames);
                            mAdapter = new clipAdapter(clipNames, clipTimes);
                            mLayoutManager = new LinearLayoutManager(Replay.this);
                            clipList.setLayoutManager(mLayoutManager);
                            clipList.setAdapter(mAdapter);

                        } else {
                            Toast.makeText(Replay.this, "Document does not exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //audio = "https://firebasestorage.googleapis.com/v0/b/interviewlogapp.appspot.com/o/Audio%2Ftest.3gp?alt=media&token=ec4b473f-c2e9-4784-8558-d9d20a882a66";
    }
    private void setMediaPlayer(){
        //mediaPlayer.
        try {
            mediaPlayer.setDataSource(audio);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        seekBar = findViewById(R.id.seekBar);

        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        };

        int d = mediaPlayer.getDuration();
        String duration = convertFormat(d);
        TextView PlayerDuration = findViewById(R.id.PlayerDuration);
        PlayerDuration.setText(duration);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
                int currentPosition = mediaPlayer.getCurrentPosition();
                TextView PlayerPosition = findViewById(R.id.PlayerPosition);
                PlayerPosition.setText(convertFormat(currentPosition));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void onReplayClick(View view) {
        if (view.getId() == R.id.music_play_button) {
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());
            handler.postDelayed(runnable, 0);
        } else if (view.getId() == R.id.music_stop_button) {
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
        } else if (view.getId() == R.id.play_forward_button) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (mediaPlayer.isPlaying() && duration != currentPosition) {
                currentPosition = currentPosition + 5000;
                TextView PlayerPosition = findViewById(R.id.PlayerPosition);
                PlayerPosition.setText(convertFormat(currentPosition));
                mediaPlayer.seekTo(currentPosition);
            }
        } else if (view.getId() == R.id.play_back_button) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            //int duration = mediaPlayer.getDuration();
            if (mediaPlayer.isPlaying() && currentPosition > 5000) {
                currentPosition = currentPosition - 5000;
                TextView PlayerPosition = findViewById(R.id.PlayerPosition);
                PlayerPosition.setText(convertFormat(currentPosition));
                mediaPlayer.seekTo(currentPosition);

            }
        }else if(view.getId() == R.id.button_make_clip){
            int currentPosition = mediaPlayer.getCurrentPosition();
            EditText text_tag = findViewById(R.id.Text_Tag_Replay);
            String tag = text_tag.getText().toString();
            TextView txtUsername = findViewById(R.id.Text_tag);
            txtUsername.setText(tag+" "+ String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                    TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))));

            Map<String, Object> note = new HashMap<>();
            String tag_name = "Clip_Tag"+String.valueOf(clip_num);
            note.put(tag_name,tag);
            String clip_name = "clip"+String.valueOf(clip_num);
            note.put(clip_name,currentPosition);
            clip_num++;
            note.put("Total_Clip",clip_num);
            db.collection("Recordings").document(record_id).update(note);
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }
    }

    public void onBackClick(View view) {
        Intent intent = new Intent(Replay.this,RecordingPanel.class);
        intent.putExtra("researcherName",userName);
        startActivity(intent);

    }
}