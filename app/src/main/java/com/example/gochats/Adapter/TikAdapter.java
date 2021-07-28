package com.example.gochats.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.R;
import com.example.gochats.modes.tikmodals;

import java.util.List;

public class TikAdapter extends RecyclerView.Adapter<TikAdapter.TikViewHolder>{

    List<tikmodals> mediaObjectList;
    Context context;

    public TikAdapter(List<tikmodals> mediaObjectList, Context context) {
        this.mediaObjectList = mediaObjectList;
        this.context = context;
    }

    @NonNull
    @Override
    public TikViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tiktokincluder,parent,false);
        return new TikViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TikViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mediaObjectList.size();
    }

    public class TikViewHolder extends RecyclerView.ViewHolder {
        public TikViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}



