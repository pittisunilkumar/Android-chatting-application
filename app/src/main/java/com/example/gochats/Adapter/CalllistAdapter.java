package com.example.gochats.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gochats.R;
import com.example.gochats.modes.CallList;

import java.util.List;

public class CalllistAdapter extends RecyclerView.Adapter<CalllistAdapter.ViewHolder> {

    private List<CallList>listIteams;

    public CalllistAdapter(List<CallList> listIteams, Context context) {
        this.listIteams = listIteams;
        this.context = context;
    }

    private Context context;



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_call_list,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CallList callList =listIteams.get(position);

        holder.username.setText(callList.getUserName());
        holder.date.setText(callList.getDate());

        if (callList.getCallType().equals("missed")){
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_red_dark));
        } else if (callList.getCallType().equals("income"))
        {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_downward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.arrow.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_upward_black_24dp));
            holder.arrow.getDrawable().setTint(context.getResources().getColor(android.R.color.holo_green_dark));
        }


        Glide.with(context).load(callList.getUrlProfile()).into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return listIteams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date,username;
        private ImageView profile,arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.tv_name1);
            date=itemView.findViewById(R.id.tv_date1);
            profile=itemView.findViewById(R.id.image_profile5);
            arrow=itemView.findViewById(R.id.img_arrow);
        }
    }
}
