package com.example.gochats.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.R;
import com.example.gochats.modes.Contact;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> list;
    Context context;

    public ContactAdapter(Context context,List<Contact> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.conlayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Contact contact = list.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,phone;
        CircularImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.tv_name30);
            phone = itemView.findViewById(R.id.tv_desc30);
            image = itemView.findViewById(R.id.image_profile30);
        }
    }
}

