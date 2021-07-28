package com.example.gochats.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.R;
import com.example.gochats.modes.UserStatus;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {

    private Context mContext;
    private List<UserStatus> mData;

    public StatusAdapter(Context mContext, List<UserStatus> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.cardview,parent,false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView stname;
        CircularImageView stimage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            stname = itemView.findViewById(R.id.stname);
            stimage = itemView.findViewById(R.id.statusset);
        }
    }
}
