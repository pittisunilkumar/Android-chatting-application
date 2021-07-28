package com.example.gochats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView username;
    private  DatabaseReference rootref;
    private String currentuserID;
    private FirebaseUser us;
    private CircularImageView profil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        username = findViewById(R.id.tv_username);
        profil = findViewById(R.id.image_profile21);
        auth = FirebaseAuth.getInstance();
        currentuserID = auth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();
        us = auth.getCurrentUser();

        if(us!=null){
        retrivingdata();}
    }


    private void retrivingdata()
    {
        rootref.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists() && snapshot.hasChild("name") && snapshot.hasChild("image"))){
                    String settingname = snapshot.child("name").getValue().toString();
                    String profielpic = snapshot.child("image").getValue().toString();
                    username.setText(settingname);
                    Picasso.get().load(profielpic).into(profil);

                }else if((snapshot.exists() && snapshot.hasChild("name")))
                {
                    String settingname = snapshot.child("name").getValue().toString();
                    username.setText(settingname);
                }else{
                    Toast.makeText(SettingsActivity.this,"Something wrong",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void gotoprofile(View view)
    {
        startActivity(new Intent(this, profileactivity.class));
        finish();
    }

}
