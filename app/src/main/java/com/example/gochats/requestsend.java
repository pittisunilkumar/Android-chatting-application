package com.example.gochats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;

public class requestsend extends AppCompatActivity {

    private String receiveruserID,senduserid,current_stats;
    private DatabaseReference userRef,chatrequestRef,totalfrds,NotificationRef;
    private CircularImageView userprof;
    private TextView uname;
    private Button sendrequest,cancelreq;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requestsend);
        mauth = FirebaseAuth.getInstance();
        senduserid =mauth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatrequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        totalfrds = FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");


        receiveruserID = getIntent().getExtras().get("visit_use_id").toString();

        userprof = (CircularImageView) findViewById(R.id.image_profile24);
        uname = (TextView) findViewById(R.id.usernames24);
        sendrequest =(Button) findViewById(R.id.sendreq);
        cancelreq =(Button) findViewById(R.id.cancelreq);
        current_stats = "new";

        retiverinfo();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void retiverinfo()
    {
        userRef.child(receiveruserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if((snapshot.exists()) && (snapshot.hasChild("image")))
                {
                    String userImage = snapshot.child("image").getValue().toString();
                    String userName = snapshot.child("name").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.icon_male_ph).into(userprof);
                    uname.setText(userName);

                    managechatreq();
                }else {
                    String userName = snapshot.child("name").getValue().toString();
                    uname.setText(userName);

                    managechatreq();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void managechatreq()
    {
        chatrequestRef.child(senduserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.hasChild(receiveruserID)){
                    String request_type = snapshot.child(receiveruserID).child("request_type").getValue().toString();
                    if(request_type.equals("sent")){
                        current_stats = "request_sent";
                        sendrequest.setText("Cancel Request");
                    }else if(request_type.equals("received"))
                    {
                        current_stats = "request_received";
                        sendrequest.setText("Acccept Request");
                        cancelreq.setVisibility(View.VISIBLE);
                        cancelreq.setEnabled(true);
                        cancelreq.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelchatrequt();
                            }
                        });

                    }
                }else {
                    totalfrds.child(senduserid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            if(snapshot.hasChild(receiveruserID)){
                                current_stats = "friends";
                                sendrequest.setText("Remove this Contact");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!senduserid.equals(receiveruserID))
        {
            sendrequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendrequest.setEnabled(false);

                    if(current_stats.equals("new"))
                    {
                         sendchatrequest();
                    }if(current_stats.equals("request_sent")){
                        cancelchatrequt();
                    }if(current_stats.equals("request_received")){
                        acceptrequest();
                    }if(current_stats.equals("friends")){
                        removingcontact();
                    }
                }
            });

        }else{
            sendrequest.setVisibility(View.INVISIBLE);
        }

    }

    private void removingcontact()
    {
        totalfrds.child(senduserid).child(receiveruserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    totalfrds.child(receiveruserID).child(senduserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful()){
                                sendrequest.setEnabled(true);
                                current_stats = "new";
                                sendrequest.setText("Send message");
                                cancelreq.setVisibility(View.INVISIBLE);
                                cancelreq.setEnabled(false);
                            }

                        }
                    });
                }

            }
        });

    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void acceptrequest()
    {
        totalfrds.child(senduserid).child(receiveruserID)
                .child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    totalfrds.child(receiveruserID).child(senduserid).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                chatrequestRef.child(senduserid).child(receiveruserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            chatrequestRef.child(receiveruserID).child(senduserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        sendrequest.setEnabled(true);
                                                        current_stats = "friends";
                                                        sendrequest.setText("Remove this Contact");
                                                        cancelreq.setVisibility(View.INVISIBLE);
                                                        cancelreq.setEnabled(false);
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void cancelchatrequt()
    {
        chatrequestRef.child(senduserid).child(receiveruserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    chatrequestRef.child(receiveruserID).child(senduserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful()){
                                sendrequest.setEnabled(true);
                                current_stats = "new";
                                sendrequest.setText("Send message");
                                cancelreq.setVisibility(View.INVISIBLE);
                                cancelreq.setEnabled(false);
                            }

                        }
                    });
                }

            }
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void sendchatrequest()
    {
        chatrequestRef.child(senduserid).child(receiveruserID).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful()){
                    chatrequestRef.child(receiveruserID).child(senduserid).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                HashMap<String, String >chatnotificationMap = new HashMap<>();
                                chatnotificationMap.put("from",senduserid);
                                chatnotificationMap.put("type","request");

                                NotificationRef.child(receiveruserID).push()
                                        .setValue(chatnotificationMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    sendrequest.setEnabled(true);
                                                    current_stats="request_sent";
                                                    sendrequest.setText("Cancel Request");
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
    public void gotofriend(View view) {
        startActivity(new Intent(this,searchfrd.class));
        finish();
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}