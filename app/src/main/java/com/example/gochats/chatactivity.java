package com.example.gochats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.Adapter.MessageAdapter;
import com.example.gochats.modes.Messages;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chatactivity extends AppCompatActivity {

    private TextView userName,userLastSeen;
    private ImageView sendfileButton;
    private CircularImageView userprofile;
    private ImageButton sendMessagebutton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private boolean isActionShown = false;

    private String saveCurrentTime,saveCurrentDate;
    private Uri fileUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private ProgressDialog loadingbar;

    private CardView layoutaction;
    private DatabaseReference Rootref;
    private String messageReceiverID,MessageReceiverName,MessageReceiverImage,messagesendid;
    private final List<Messages> messagesList = new ArrayList<>();private LinearLayoutManager linearLayoutManager;private MessageAdapter messageAdapter;private RecyclerView userMessagesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatactivity);

        loadingbar = new ProgressDialog(this);


        mAuth =FirebaseAuth.getInstance();
        userLastSeen =(TextView)findViewById(R.id.tv_status26);

        layoutaction =findViewById(R.id.layout_actions);

        messagesendid =mAuth.getCurrentUser().getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();
        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        MessageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        MessageReceiverImage=getIntent().getExtras().get("visit_image").toString();

        userName = (TextView)findViewById(R.id.tv_username26);

        sendfileButton =(ImageView)findViewById(R.id.btn_file);

        userprofile =(CircularImageView)findViewById(R.id.image_profile26);
        sendMessagebutton =(ImageButton) findViewById(R.id.btn_send26);
        MessageInputText =(EditText)findViewById(R.id.ed_message26);


        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView)findViewById(R.id.recyclerView26);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);


        userMessagesList.setAdapter(messageAdapter);
        userName.setText(MessageReceiverName);
        Picasso.get().load(MessageReceiverImage).placeholder(R.drawable.icon_male_ph).into(userprofile);
//////////////////////////////////////////////////////////////////////////////////////////////////////////
        sendMessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());
/////////////////////////////////////////////////////////////////////////////////////////////////////
        sendfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionShown){
                    layoutaction.setVisibility(View.GONE);
                    isActionShown = false;
                } else {
                    layoutaction.setVisibility(View.VISIBLE);
                    isActionShown = true;
                }
            }
        });
        message();
////////////////////////////////////////////////////////////////////////////////////////////////////
        DisplayLastSeen();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void message()
    {
        Rootref.child("Messages").child(messagesendid).child(messageReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                Messages messages =snapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName){}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot){}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName){}
            @Override
            public void onCancelled(@NonNull DatabaseError error){}
        });
    }
    private void sendmessage()
    {
        String messageText = MessageInputText.getText().toString();
        long model1 = new Date().getTime();
        if(TextUtils.isEmpty(messageText)){}else {

            String messageSenderRef = "Messages/" + messagesendid + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messagesendid;
            DatabaseReference userMessagekeyRef = Rootref.child("Messages").child(messagesendid).child(messageReceiverID).push();
            String messagepushID = userMessagekeyRef.getKey();

            Map messageTextbody = new HashMap();
            messageTextbody.put("message",messageText);
            messageTextbody.put("type","text");
            messageTextbody.put("from",messagesendid);
            messageTextbody.put("to",messageReceiverID);
            messageTextbody.put("messageID",messagepushID);
            messageTextbody.put("time",saveCurrentTime);
            messageTextbody.put("date",saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagepushID,messageTextbody);
            messageBodyDetails.put(messageReceiverRef+"/"+messagepushID,messageTextbody);
            Rootref.updateChildren(messageBodyDetails);MessageInputText.setText("");
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void DisplayLastSeen()
    {
        Rootref.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child("userState").hasChild("state"))
                {
                    String state = snapshot.child("userState").child("state").getValue().toString();
                    String date = snapshot.child("userState").child("date").getValue().toString();
                    String time = snapshot.child("userState").child("time").getValue().toString();
                    if(state.equals("online"))
                    {
                        userLastSeen.setText("online");
                    }
                    else if(state.equals("offline"))
                    {
                        userLastSeen.setText("Last Seen: "+ time+" "+ date);
                    }
                }
                else{
                    userLastSeen.setText("offline");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void updateUserStatus(String state)
    {
        String saveCurrentTime,saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        HashMap<String,Object>onlineStateMap = new HashMap<>();
        onlineStateMap.put("time",saveCurrentTime);
        onlineStateMap.put("date",saveCurrentDate);
        onlineStateMap.put("state",state);

        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void backtomain(View view){startActivity(new Intent(this,MainActivity.class));
    finish();}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void sendimage(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Select Image"),438);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==438 && resultCode==RESULT_OK && data !=null && data.getData() !=null)
        {
            loadingbar.setTitle("Sending Image");
            loadingbar.setMessage("please wait, we are sending that Image......");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            fileUri =data.getData();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

            String messageSenderRef = "Messages/" + messagesendid + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messagesendid;
            DatabaseReference userMessagekeyRef = Rootref.child("Messages").child(messagesendid).child(messageReceiverID).push();
            final String messagepushID = userMessagekeyRef.getKey();

            StorageReference filePath = storageReference.child(messagepushID + "." + "jpg");
            uploadTask = filePath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        Map messageTextbody = new HashMap();
                        messageTextbody.put("message",myUrl);
                        messageTextbody.put("name",fileUri.getLastPathSegment());
                        messageTextbody.put("type","image");
                        messageTextbody.put("from",messagesendid);
                        messageTextbody.put("to",messageReceiverID);
                        messageTextbody.put("messageID",messagepushID);
                        messageTextbody.put("time",saveCurrentTime);
                        messageTextbody.put("date",saveCurrentDate);

                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messageSenderRef+"/"+messagepushID,messageTextbody);
                        messageBodyDetails.put(messageReceiverRef+"/"+messagepushID,messageTextbody);
                        Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    loadingbar.dismiss();
                                }else
                                {
                                    loadingbar.dismiss();
                                }
                            }
                        });
                        MessageInputText.setText("");
                    }
                }
            });
        }
        else if(requestCode==750 && resultCode==RESULT_OK && data !=null && data.getData() !=null)
        {
            loadingbar.setTitle("Sending Video");
            loadingbar.setMessage("please wait, we are sending that Video......");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            fileUri =data.getData();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Video");

            String messageSenderRef = "Messages/" + messagesendid + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messagesendid;
            DatabaseReference userMessagekeyRef = Rootref.child("Messages").child(messagesendid).child(messageReceiverID).push();
            final String messagepushID = userMessagekeyRef.getKey();

            StorageReference filePath = storageReference.child(messagepushID + "." + "mp4");
            uploadTask = filePath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        Map messageTextbody = new HashMap();
                        messageTextbody.put("message",myUrl);
                        messageTextbody.put("name",fileUri.getLastPathSegment());
                        messageTextbody.put("type","video");
                        messageTextbody.put("from",messagesendid);
                        messageTextbody.put("to",messageReceiverID);
                        messageTextbody.put("messageID",messagepushID);
                        messageTextbody.put("time",saveCurrentTime);
                        messageTextbody.put("date",saveCurrentDate);

                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messageSenderRef+"/"+messagepushID,messageTextbody);
                        messageBodyDetails.put(messageReceiverRef+"/"+messagepushID,messageTextbody);
                        Rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful())
                                {
                                    loadingbar.dismiss();
                                }else
                                {
                                    loadingbar.dismiss();
                                }
                            }
                        });
                        MessageInputText.setText("");
                    }
                }
            });
        }else
        {
            loadingbar.dismiss();
            Toast.makeText(this,"Something Wrong",Toast.LENGTH_SHORT).show();
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void sendvedio(View view)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent.createChooser(intent,"Select Video"),750);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            updateUserStatus("online");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            updateUserStatus("offline");
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}