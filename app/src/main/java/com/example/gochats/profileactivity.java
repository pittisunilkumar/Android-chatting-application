package com.example.gochats;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

public class profileactivity extends AppCompatActivity {

    TextView name,phonetext,t;
    FloatingActionButton fabCamera;
    FirebaseAuth auth;
    DatabaseReference rootref;
    private FirebaseUser us;
    private String currentuserID;
    private BottomSheetDialog bottomSheetDialog,bsDialogEditName;
    private CircularImageView pofileimage22;
    private static final int gallyerpick = 1;
    private StorageReference profilepicset;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);

        pofileimage22 = findViewById(R.id.image_profile);
        fabCamera = findViewById(R.id.fab_camera);

        name = findViewById(R.id.tv_username);
        t=findViewById(R.id.t);
        phonetext = findViewById(R.id.tv_phone);
        auth = FirebaseAuth.getInstance();
        currentuserID = auth.getCurrentUser().getUid();
        rootref = FirebaseDatabase.getInstance().getReference();
        us = auth.getCurrentUser();
        profilepicset = FirebaseStorage.getInstance().getReference().child("Profile Images");

        loadingbar = new ProgressDialog(this);


        if(us!=null){
            retrivingdata();
            settingidentity();
        }

////////////////////////////////////////////////////////////////////////////////////////////////////
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetPickPhoto();
            }
        });
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void showBottomSheetPickPhoto() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gallary();

                bottomSheetDialog.dismiss();
            }
        });
        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo Open Camera
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });
        bottomSheetDialog.show();
    }
/////////////////////////////////////////////////////////////////////////////////////////////
    private void gallary()
    {
        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,gallyerpick);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == gallyerpick && resultCode == RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                 loadingbar.setTitle("Set profile image");
                 loadingbar.setMessage("please wait your profile is setting......");
                 loadingbar.setCanceledOnTouchOutside(false);
                 loadingbar.show();
                Uri resulturi = result.getUri();

                StorageReference filepath = profilepicset.child(currentuserID +".jpg");

                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(profileactivity.this,"succesfully",Toast.LENGTH_SHORT).show();
                            profilepicset.child(currentuserID+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();

                                    rootref.child("Users").child(currentuserID).child("image")
                                            .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(profileactivity.this,"Your profile is upadated",Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }else {
                                                String message = task.getException().toString();
                                                Toast.makeText(profileactivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });

                                }
                            });

                        }else{
                            String message = task.getException().toString();
                            Toast.makeText(profileactivity.this,"Error" + message,Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        }
                    }
                });
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    private void settingidentity()
    {
        rootref.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists())&&(snapshot.hasChild("email"))){
                    String settingname = snapshot.child("email").getValue().toString();
                    phonetext.setText(settingname);
                    t.setText("Email");
                }else{
                    String settingname = snapshot.child("phone").getValue().toString();
                    phonetext.setText(settingname);
                    t.setText("Phone");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////
    private void retrivingdata()
    {
        rootref.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if((snapshot.exists() )&&( snapshot.hasChild("name"))&&(snapshot.hasChild("image"))){
                    String settingname = snapshot.child("name").getValue().toString();
                    String prof = snapshot.child("image").getValue().toString();
                    name.setText(settingname);
                    Picasso.get().load(prof).into(pofileimage22);
                }else if((snapshot.exists() )&&( snapshot.hasChild("name"))){
                    String settingname = snapshot.child("name").getValue().toString();
                    name.setText(settingname);
                }else{
                    Toast.makeText(profileactivity.this,"Something wrong",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    public void backtogsetting(View view)
    {
        startActivity(new Intent(this,SettingsActivity.class));
        finish();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,SettingsActivity.class));
        finish();
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void showBottomSheetEditName(){
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_name,null);

        ((View) view.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bsDialogEditName.dismiss();
            }
        });

        final EditText edUserName = view.findViewById(R.id.ed_username);

        ((View) view.findViewById(R.id.btn_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edUserName.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Name can'e be empty",Toast.LENGTH_SHORT).show();
                } else
                    {
                        String edittextname =edUserName.getText().toString();
                        HashMap<String,Object>profile = new HashMap<>();
                        profile.put("uid",currentuserID);
                        profile.put("name",edittextname);
                        rootref.child("Users").child(currentuserID).updateChildren(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(profileactivity.this,"User name edit Successfull",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    bsDialogEditName.dismiss();
                }
            }
        });

        bsDialogEditName = new BottomSheetDialog(this);
        bsDialogEditName.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(bsDialogEditName.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        bsDialogEditName.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bsDialogEditName = null;
            }
        });
        bsDialogEditName.show();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    public void editname(View view)
    {
        showBottomSheetEditName();
    }

    public void logoutfun(View view)
    {
        auth.signOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}