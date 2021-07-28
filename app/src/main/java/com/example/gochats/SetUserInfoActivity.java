package com.example.gochats;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

public class SetUserInfoActivity extends AppCompatActivity {

    EditText name;
    CircularImageView profile;
    Button saveinfo;

    private String currentuserID;
    private FirebaseAuth auth;
    private DatabaseReference rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_info);

        auth = FirebaseAuth.getInstance();
        currentuserID =auth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();

        name =(EditText)findViewById(R.id.ed_name1);
        saveinfo=(Button)findViewById(R.id.btn_next1);
        profile=(CircularImageView)findViewById(R.id.image_profile6);


        saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updating();

            }
        });

    }

    private void updating()
    {
        String uname = name.getText().toString();
        String uemail = auth.getCurrentUser().getEmail();
        String phone = auth.getCurrentUser().getPhoneNumber();
        if(TextUtils.isEmpty(uname)){
            Toast.makeText(this,"Filled the name",Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String,Object>profile = new HashMap<>();
            profile.put("uid",currentuserID);
            profile.put("name",uname);
            profile.put("email",uemail);
            profile.put("phone",phone);
            rootref.child("Users").child(currentuserID).setValue(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                String devicetoken = FirebaseInstanceId.getInstance().getToken();
                                rootref.child("Users").child(currentuserID).child("device_token").setValue(devicetoken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendUserToMainActivity();
                                        Toast.makeText(SetUserInfoActivity.this,"Registeration Successful",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(SetUserInfoActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetUserInfoActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

}