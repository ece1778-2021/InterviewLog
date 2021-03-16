package com.example.interviewlogapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class clipAdapter extends RecyclerView.Adapter<clipAdapter.ViewHolder> {
    String TAG ="testactivity";
    ArrayList <String> clipName;
    ArrayList <String> clipTime;
    public clipAdapter(ArrayList<String> clipNames, ArrayList<String> clipTimes) {
        clipName = clipNames;
        clipTime = clipTimes;
    }

    @NonNull
    @Override
    public clipAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.clip_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull clipAdapter.ViewHolder holder, int position) {
        holder.textClipName.setText(clipName.get(position));
        Log.d(TAG, "Tag "+ position + " is " +clipName.get(position));
        holder.textClipTime.setText(clipTime.get(position));
    }

    @Override
    public int getItemCount() {
        return clipName.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textClipName, textClipTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textClipName=itemView.findViewById(R.id.clipName);
            textClipTime = itemView.findViewById(R.id.timeStamp);
        }
    }
}
