package com.example.interviewlogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Replay_Command extends AppCompatActivity {

    private Runnable runnable;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler handler = new Handler();
    private SeekBar seekBar;
    private FirebaseFirestore db;
    private String audio;
    private String record_id;
    String userName;
    private int comment_point;
    private ArrayList<Long> Time_List = new ArrayList<Long>();
    private ArrayList<Integer> Time_List_clip = new ArrayList<Integer>();
    private long clip_num;
    String TAG = "replay";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replay__command);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        record_id = intent.getStringExtra("record_id");
        ArrayList<String> clipNames = new ArrayList<>();
        ArrayList<String> clipTimes = new ArrayList<>();
        GridView clip_gridview = findViewById(R.id.clip_grid);

        db.collection("Recordings").document(record_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            audio = documentSnapshot.getString("storageRef");
                            String username = documentSnapshot.getString("partName");
                            clip_num = documentSnapshot.getLong("Total_Clip");
                            for (int i=0; i<clip_num; i++){
                                String tag = documentSnapshot.getString("Clip_Tag"+i);
                                int time_clip = documentSnapshot.getLong("clip"+i).intValue();
                                String time = convertFormat(time_clip);
                                clipNames.add(tag);
                                clipTimes.add(time);
                                Time_List_clip.add(time_clip);
                            }
                            //Log.d(TAG, "clip names are "+clipNames);
                            //Log.d(TAG, "clip times are "+clipTimes);
                            GridClipAdapter adapter = new GridClipAdapter(clipNames, clipTimes,3,Replay_Command.this);
                            clip_gridview.setAdapter(adapter);
                            setMediaPlayer();
                        } else {
                            Toast.makeText(Replay_Command.this, "Document does not exist", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        GridView gridview = findViewById(R.id.comment_grid);
        setgridview(gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long t = Time_List.get(position);
                int postion = t.intValue();
                //Log.d("Debugging", String.valueOf(t));
                if (mediaPlayer.isPlaying()) {
                    TextView PlayerPosition = findViewById(R.id.PlayerPosition);
                    PlayerPosition.setText(convertFormat(postion));
                    mediaPlayer.seekTo(postion);
                }
            }
        });

        clip_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int postion = Time_List_clip.get(position);
                //Log.d("Debugging", String.valueOf(t));
                if (mediaPlayer.isPlaying()) {
                    TextView PlayerPosition = findViewById(R.id.PlayerPosition);
                    PlayerPosition.setText(convertFormat(postion));
                    mediaPlayer.seekTo(postion);
                }
            }
        });
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
            if (mediaPlayer.isPlaying() && currentPosition > 5000) {
                currentPosition = currentPosition - 5000;
                TextView PlayerPosition = findViewById(R.id.PlayerPosition);
                PlayerPosition.setText(convertFormat(currentPosition));
                mediaPlayer.seekTo(currentPosition);

            }
        }else if(view.getId() == R.id.TextCommand){
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
            comment_point = mediaPlayer.getCurrentPosition();
        }
    }

    public void onCommentClick(View view) {
        EditText text_comment = findViewById(R.id.TextCommand);
        String comment = text_comment.getText().toString();
        Map<String, Object> note = new HashMap<>();
        note.put("time_stamp",comment_point);
        note.put("Comment",comment);
        //note.put("User",user);
        final String randomKey = UUID.randomUUID().toString();
        db.collection("Recordings").document(record_id).collection("Comments").document(randomKey).set(note);

        finish();
        overridePendingTransition(0, 0);
        getIntent().putExtra("researcherName",userName);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    public void onBackClick(View view) {
        Intent intent = new Intent(Replay_Command.this,RecordingPanel.class);
        intent.putExtra("researcherName",userName);
        startActivity(intent);
    }

    private void setgridview(GridView gridview) {
        Log.d("Debugging", "Setting Grid");
        ArrayList<String> Timestamp = new ArrayList<String>();
        ArrayList<String> UserNames = new ArrayList<String>();
        ArrayList<String> Comments = new ArrayList<String>();
        CollectionReference result = db.collection("Recordings").document(record_id).collection("Comments");
        //Log.d("Debugging", "Get databse");
        result.orderBy("time_stamp", Query.Direction.ASCENDING).get()
        //result.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    //Log.d("Debugging", "Task done");
                    if (task.isSuccessful()) {
                        //Log.d("Debugging", "Task is successful");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //Log.d("Debugging", "in the databse");
                            //String name = document.getString("Username");
                            String comment = document.getString("Comment");
                            //Log.d("Debugging", comment);
                            Long time = document.getLong("time_stamp");
                            String stamp = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time),
                                    TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
                            //Log.d("Debugging", stamp);
                            UserNames.add("Username");
                            Timestamp.add(stamp);
                            Comments.add(comment);
                            Time_List.add(time);
                        }
                    GridCommentAdapter adapter = new GridCommentAdapter(Timestamp, UserNames,Comments,3,Replay_Command.this);
                    gridview.setAdapter(adapter);
                    } else{
                        //This Toast will be displayed only when you'll have an error while getting documents.
                        Toast.makeText(Replay_Command.this, "Database Load Error", Toast.LENGTH_LONG).show();
                    }
              }
            });
    }

    private class GridCommentAdapter extends ArrayAdapter<String> {
        private ArrayList<String> Timestamp;
        private ArrayList<String> UserNames;
        private ArrayList<String> Comments;
        private Context context;
        private int layoutResource;
        private LayoutInflater layoutInflater;

        public GridCommentAdapter(ArrayList<String> Timestamp,ArrayList<String> UserNames,ArrayList<String> Comments,int layoutResource,Context context){
            super(context, layoutResource,Timestamp);
            this.Timestamp = Timestamp;
            this.UserNames = UserNames;
            this.Comments = Comments;
            this.context = context;
            this.layoutResource = layoutResource;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            //Log.d("Debugging", "Setting successfully");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Log.d("Debugging", "In the get View");
            if(layoutInflater == null){
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.comment_item,null);//locate in res/layout/row_item.html
            }

            TextView textview_time = convertView.findViewById(R.id.text_Timestamp);
            String time = this.Timestamp.get(position);

            TextView textview_user = convertView.findViewById(R.id.textViewUsername);
            String user_name = this.UserNames.get(position);

            TextView textview_comment = convertView.findViewById(R.id.textViewComment);
            String comment = this.Comments.get(position);
            Log.d("Debugging", "In the grid " + comment);

            textview_time.setText(time);
            textview_user.setText(user_name);
            textview_comment.setText(comment);
            return convertView;
        }
    }

    private class GridClipAdapter extends ArrayAdapter<String> {
        private ArrayList<String> clipNames;
        private ArrayList<String> clipTimes;
        private Context context;
        private int layoutResource;
        private LayoutInflater layoutInflater;

        public GridClipAdapter(ArrayList<String> clipNames,ArrayList<String> clipTimes,int layoutResource,Context context){
            super(context, layoutResource,clipNames);
            this.clipNames = clipNames;
            this.clipTimes = clipTimes;
            this.context = context;
            this.layoutResource = layoutResource;
            this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            Log.d("Replay", "Setting successfully");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("Replay", "In the get View");
            if(layoutInflater == null){
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.clip_list,null);//locate in res/layout/row_item.html
                //convertView = layoutInflater.inflate(R.layout.comment_item,null);//locate in res/layout/row_item.html
            }

            TextView textview_timeStamp = convertView.findViewById(R.id.timeStamp);
            //TextView textview_timeStamp = convertView.findViewById(R.id.textViewUsername);
            String timeStamp = this.clipTimes.get(position);

            TextView textview_clipName = convertView.findViewById(R.id.clipName);
            //TextView textview_clipName = convertView.findViewById(R.id.textViewComment);
            String clipName = this.clipNames.get(position);

            textview_timeStamp.setText(timeStamp);
            textview_clipName.setText(clipName);
            return convertView;
        }
    }
}