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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class usersfriends extends AppCompatActivity {

    private ImageButton backtomain;
    private RecyclerView myContactLst;
    private DatabaseReference totalref,usersref;
    private FirebaseAuth mauth;
    private String currentuserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersfriends);

        backtomain = findViewById(R.id.backtomain);
        myContactLst = (RecyclerView)findViewById(R.id.totalfrdrecy);
        myContactLst.setLayoutManager(new LinearLayoutManager(this));
        mauth = FirebaseAuth.getInstance();
        currentuserid = mauth.getCurrentUser().getUid();
        totalref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserid);
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");

        backtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(usersfriends.this,MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(totalref,Model.class).build();
        FirebaseRecyclerAdapter<Model,ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Model, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Model model)
            {
                String userIds = getRef(position).getKey();
                usersref.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.hasChild("image")){
                            String profileimage = snapshot.child("image").getValue().toString();
                            String profilename = snapshot.child("name").getValue().toString();
                            holder.userName.setText(profilename);
                            Picasso.get().load(profileimage).placeholder(R.drawable.icon_male_ph).into(holder.profilImage);
                        }else {
                            String profilename = snapshot.child("name").getValue().toString();
                            holder.userName.setText(profilename);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allusers,parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        myContactLst.setAdapter(adapter);
        adapter.startListening();
    }
    
    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName;
        CircularImageView profilImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_name23);
            profilImage = itemView.findViewById(R.id.image_profile23);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(usersfriends.this,MainActivity.class));
        finish();
    }

}