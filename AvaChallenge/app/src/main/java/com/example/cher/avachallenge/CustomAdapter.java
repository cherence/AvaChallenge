package com.example.cher.avachallenge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leisforkokomo on 8/18/16.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.AvaMViewHolder>{
    private static final String TAG = "CustomAdapter";
    List<AvaMessage> data = new ArrayList<>();
    public CustomAdapter(List<AvaMessage> data) {
        this.data = data;
    }

    @Override
    public AvaMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        AvaMViewHolder avaVh = new AvaMViewHolder(v);
        return avaVh;
    }

    @Override
    public void onBindViewHolder(AvaMViewHolder holder, int position) {
        holder.messageTextView.setText(data.get(position).getTranscript());
        if(data.get(position).getSpeakerId() == MainActivity.CHANNEL){
            holder.senderImageView.setImageResource(R.drawable.dog_48);
            Log.i(TAG, "*****************onBindViewHolder: speaker id matches so dog");
        } else {
            holder.senderImageView.setImageResource(R.drawable.cat_48);
            Log.i(TAG, "*****************onBindViewHolder: speaker id doesn't match so cat " + data.get(position).getSpeakerId());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class AvaMViewHolder extends RecyclerView.ViewHolder {
        public ImageView senderImageView;
        public TextView messageTextView;

        public AvaMViewHolder(View v) {
            super(v);
            this.senderImageView = (ImageView) v.findViewById(R.id.imageView_id);
            this.messageTextView = (TextView) v.findViewById(R.id.textView_id);
        }
    }
}
