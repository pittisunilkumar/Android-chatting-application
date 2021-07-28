package com.example.gochats;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.modes.Model;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class all_friendrequest extends AppCompatActivity {

    private RecyclerView myrequests;
    private DatabaseReference ChatRequestRef,UserRef,ContactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friendrequest);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        myrequests = findViewById(R.id.friendreqlist33);
        myrequests.setLayoutManager(new LinearLayoutManager(this));
    }
 //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Model>options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(ChatRequestRef.child(currentUserID),Model.class)
                .build();

        FirebaseRecyclerAdapter<Model,RequestsViewHolder>adapter=new FirebaseRecyclerAdapter<Model, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Model model)
            {
                holder.itemView.findViewById(R.id.requestacceptbtn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.requestcancelbtn).setVisibility(View.VISIBLE);
                final String list_user_id = getRef(position).getKey();
                DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists()){
                            String type = snapshot.getValue().toString();
                            if(type.equals("received"))
                            {
                                UserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(snapshot.hasChild("image")) {
                                            final String requestprofileImage = snapshot.child("image").getValue().toString();
                                            Picasso.get().load(requestprofileImage).into(holder.profileImage);

                                        }
                                        final String requestUserName = snapshot.child("name").getValue().toString();
                                        holder.userName.setText(requestUserName);
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                             "Accept",
                                                             "Cancel"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(all_friendrequest.this);
                                                builder.setTitle(requestUserName + " Chat Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {
                                                        if(which == 0)
                                                        {
                                                            ContactsRef.child(currentUserID).child(list_user_id).child("Contacts")
                                                                    .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful())
                                                                    {
                                                                        ContactsRef.child(list_user_id).child(currentUserID).child("Contacts")
                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    ChatRequestRef.child(currentUserID).child(list_user_id)
                                                                                            .removeValue()
                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                {

                                                                                                    if(task.isSuccessful())
                                                                                                    {
                                                                                                        ChatRequestRef.child(list_user_id).child(currentUserID)
                                                                                                                .removeValue()
                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                                    {
                                                                                                                        if(task.isSuccessful())
                                                                                                                        {
                                                                                                                            Toast.makeText(all_friendrequest.this,"New Contact Saved",Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    }
                                                                                                                });

                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            });
                                                        }
                                                        if(which == 1)
                                                        {

                                                            ChatRequestRef.child(currentUserID).child(list_user_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {

                                                                            if(task.isSuccessful())
                                                                            {
                                                                                ChatRequestRef.child(list_user_id).child(currentUserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(all_friendrequest.this,"Contact Deleted",Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });

                                                                            }
                                                                        }
                                                                    });

                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }else if(type.equals("sent"))
                            {
                                Button request_sent_btn = holder.itemView.findViewById(R.id.requestacceptbtn);
                                request_sent_btn.setText("Request sent");
                                holder.itemView.findViewById(R.id.requestcancelbtn).setVisibility(View.INVISIBLE);

                                UserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        if(snapshot.hasChild("image")) {
                                            final String requestprofileImage = snapshot.child("image").getValue().toString();
                                            Picasso.get().load(requestprofileImage).into(holder.profileImage);

                                        }
                                        final String requestUserName = snapshot.child("name").getValue().toString();
                                        holder.userName.setText(requestUserName);
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Cancel Chat Request"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(all_friendrequest.this);
                                                builder.setTitle("Already Sent Request");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which)
                                                    {

                                                        if(which == 0)
                                                        {

                                                            ChatRequestRef.child(currentUserID).child(list_user_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {

                                                                            if(task.isSuccessful())
                                                                            {
                                                                                ChatRequestRef.child(list_user_id).child(currentUserID)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(all_friendrequest.this,"You Have Canceled chat Request",Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });

                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allusers,parent,false);
                RequestsViewHolder holder = new RequestsViewHolder(view);
                return holder;
            }
        };
        myrequests.setAdapter(adapter);
        adapter.startListening();
    }

    public void backtomain(View view){startActivity(new Intent(this,MainActivity.class));finish();}

    public static  class RequestsViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        CircularImageView profileImage;
        Button AcceptButton,CancelButton;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_name23);
            profileImage = itemView.findViewById(R.id.image_profile23);
            AcceptButton = itemView.findViewById(R.id.requestacceptbtn);
            CancelButton = itemView.findViewById(R.id.requestcancelbtn);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void updateUserStatus(String state) {
        String saveCurrentTime,saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentDate.format(calendar.getTime());

        HashMap<String,Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("time",state);

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}