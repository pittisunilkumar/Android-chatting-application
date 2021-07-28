package com.example.gochats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.modes.Model;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class searchfrd extends AppCompatActivity {

    ImageButton back;
    RecyclerView recyclerView;
    DatabaseReference Usersref;
    ArrayList<Model>list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchfrd);
        back = findViewById(R.id.btn_back);

        recyclerView = findViewById(R.id.recyclerView7);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Model>();

        Usersref = FirebaseDatabase.getInstance().getReference().child("Users");


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(searchfrd.this,MainActivity.class));
                finish();
            }
        });

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model>options=new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(Usersref,Model.class)
                .build();

        FirebaseRecyclerAdapter<Model,FindFriendViewHolder>adapter= new FirebaseRecyclerAdapter<Model, FindFriendViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Model model)
            {
                holder.userName.setText(model.getName());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.icon_male_ph).into(holder.prof);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_use_id = getRef(position).getKey();
                        Intent request = new Intent(searchfrd.this, requestsend.class);
                        request.putExtra("visit_use_id",visit_use_id);
                        startActivity(request);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allusers,parent,false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircularImageView prof;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_name23);
            prof = itemView.findViewById(R.id.image_profile23);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(searchfrd.this,MainActivity.class));
        finish();
    }

}