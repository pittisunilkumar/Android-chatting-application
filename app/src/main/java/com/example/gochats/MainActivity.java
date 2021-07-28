package com.example.gochats;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.gochats.Adapter.TabsAccessorAdapter;
import com.example.gochats.modes.Model;
import com.example.gochats.modes.Status;
import com.example.gochats.modes.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    FloatingActionButton fabAction;
    ImageButton btnAddStatus;


    private Toolbar mtoolbar;
    private ViewPager myviewpager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter mytabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private DatabaseReference rootref;
    private ProgressDialog loadingbar;
    private Model user;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        fabAction =(FloatingActionButton)findViewById(R.id.fab_action);
        btnAddStatus = findViewById(R.id.btn_add_status);

        auth=FirebaseAuth.getInstance();
        currentUser=auth.getCurrentUser();
        rootref = FirebaseDatabase.getInstance().getReference();

        mtoolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);

        drawerLayout=findViewById(R.id.drawer);
        navigationView=findViewById(R.id.navdrawer);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,mtoolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        myviewpager=(ViewPager)findViewById(R.id.main_tabs_pager);
        mytabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myviewpager.setAdapter(mytabsAccessorAdapter);

        myTabLayout=(TabLayout)findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myviewpager);
        loadingbar = new ProgressDialog(this);
        getPermissions();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        myviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                changeFabICon(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////
        if(currentUser!=null)
        {
            rootref.child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(Model.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart()
    {
        super.onStart();
        if(currentUser == null){
            SendUserToLogActivity();
        }else{
            verifiyeduser();
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void verifiyeduser()
    {
        String currenuserID = auth.getCurrentUser().getUid();
        rootref.child("Users").child(currenuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child("name").exists()){
                }else {
                    Setuserinfo();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        if(currentUser!=null){
            updateUserStatus("online");
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(currentUser!=null){
            updateUserStatus("offline");
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.friendtofind:
                startActivity(new Intent(this,searchfrd.class));
                finish();
                Toast.makeText(this,"find your friend",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_setting_options:
                SendUsersettingActivity();
                break;
            case R.id.search_bar:
                Toast.makeText(this,"Search",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Group_created:
                Toast.makeText(this,"find friend",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////
    private void SendUserToLogActivity()
    {
        Intent loginIntents = new Intent(MainActivity.this,LoginActivity.class);
        loginIntents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntents);
        finish();

    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    private void SendUsersettingActivity()
    {
        Intent setting = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(setting);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
    public void onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) { drawerLayout.closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
//////////////////////////////////////////////////navigation iteams selected code
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.Friendsofapp:
                startActivity(new Intent(this,usersfriends.class) );
                finish();
                break;
            case R.id.phone:
                startActivity(new Intent(this, phonemainactivity.class));
                finish();
                Toast.makeText(this,"phone",Toast.LENGTH_SHORT).show();
                break;
            case R.id.shortvedio:
                startActivity(new Intent(this,vediosapp.class));
                finish();
                Toast.makeText(this,"shortvedio",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Friendrequest:
                startActivity(new Intent(this,all_friendrequest.class));
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////
private void Setuserinfo()
{
    Intent info = new Intent(MainActivity.this,SetUserInfoActivity.class);
    info.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(info);
    finish();
}
////////////////////////////////////////////////////////////////////////////////////////////////////
private void getPermissions() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
    }
}
//////////////////////////////////////////////////////////////////////
    private void changeFabICon(final int index){
        fabAction.hide();
        btnAddStatus.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (index){

                    case 0 :
                        fabAction.hide(); break;
                    case 1 :
                        fabAction.show();
                        fabAction.setImageDrawable(getDrawable(R.drawable.ic_camera_alt_black_24dp));
                        btnAddStatus.setVisibility(View.VISIBLE);
                        break;
                    case 2 :
                        fabAction.show();
                        fabAction.setImageDrawable(getDrawable(R.drawable.ic_call_black_24dp));
                        break;
                }
            }
        },400);
        performOnClick(index);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void performOnClick(final int index){
        fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index==1){
                    Toast.makeText(getApplicationContext(),"Camera..",Toast.LENGTH_LONG).show();
                }else if (index==2){
                    Toast.makeText(getApplicationContext(),"Call..",Toast.LENGTH_LONG).show();
                } else {
                }
            }
        });
        btnAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Select Imaage"),1000);
            }
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        String currentUserID = auth.getCurrentUser().getUid();
        rootref.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000 && resultCode==RESULT_OK && data !=null && data.getData() !=null) {
            loadingbar.setTitle("Sending Status");
            loadingbar.setMessage("please wait, we are sending that Status......");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            Date date = new Date();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("status").child(date.getTime() + "");
            storageReference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(user.getName());
                                userStatus.setImage(user.getImage());
                                userStatus.setLastUpdated(date.getTime());

                                HashMap<String,Object> obj = new HashMap<>();
                                obj.put("Name",userStatus.getName());
                                obj.put("Image",userStatus.getImage());
                                obj.put("lastUpdated",userStatus.getLastUpdated());
                                String imageUri = uri.toString();

                                Status status = new Status(imageUri,userStatus.getLastUpdated());
                                rootref.child("stories").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);
                                rootref.child("stories").child(FirebaseAuth.getInstance().getUid()).child("statuses").push().setValue(status);
                                loadingbar.dismiss();
                            }
                        });
                    }
                }
            });
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}