package com.example.gochats;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.gochats.Adapter.StatusAdapter;
import com.example.gochats.modes.Model;
import com.example.gochats.modes.Status;
import com.example.gochats.modes.UserStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StatusFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public StatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private DatabaseReference Rootref;
    private ProgressDialog loadingbar;
    Model user;
    CircularStatusView status_view;
    private CircularImageView statusimage;
    private FirebaseUser currentuser;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_status, container, false);

        CircularImageView image_profilest = view.findViewById(R.id.image_profilest);

        status_view = view.findViewById(R.id.circular_status_view22);
        statusimage = view.findViewById(R.id.statussetimage);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        Rootref = FirebaseDatabase.getInstance().getReference();
        loadingbar = new ProgressDialog(getContext());
        if(currentuser!=null){
            Rootref.child("Users").child(currentuser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(Model.class);
                    if((snapshot.exists() )&&( snapshot.hasChild("name"))&&(snapshot.hasChild("image"))){
                        String prof = snapshot.child("image").getValue().toString();
                        Picasso.get().load(prof).into(image_profilest);
                        Picasso.get().load(prof).into(statusimage);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Rootref.child("stories").child(FirebaseAuth.getInstance().getUid()).child("statuses").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                afterstaust();
            }
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }@Override public void onChildRemoved(@NonNull DataSnapshot snapshot) { }@Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}@Override public void onCancelled(@NonNull DatabaseError error) {}
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        honey();
        RecyclerView recyclerView = view.findViewById(R.id.statusrecy);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        List<UserStatus>lisstatus = new ArrayList<>();

        recyclerView.setAdapter(new StatusAdapter(getContext(), lisstatus));
        LinearLayout instatus = view.findViewById(R.id.ln_status);
        LinearLayout afterstatus = view.findViewById(R.id.afterstatus);
        instatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Select Imaage"),1000);
            }
        });
        afterstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Rootref.child("stories").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            UserStatus status = new UserStatus();
                            ArrayList<Status> statuses = new ArrayList<>();
                            for(DataSnapshot statusSnapshot : snapshot.child("statuses").getChildren()){
                                Status sampleStatus = statusSnapshot.getValue(Status.class);
                                statuses.add(sampleStatus);
                            }
                            status.setStatuses(statuses);

                            ArrayList<MyStory> myStories = new ArrayList<>();
                            for(Status statu : status.getStatuses()) {
                                myStories.add(new MyStory(statu.getImageUrl()));
                            }
                            new StoryView.Builder(((MainActivity)getContext()).getSupportFragmentManager())
                                    .setStoriesList(myStories) // Required
                                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                    .setTitleText(status.getName()) // Default is Hidden
                                    .setSubtitleText("") // Default is Hidden
                                    .setTitleLogoUrl(status.getImage()) // Default is Hidden
                                    .setStoryClickListeners(new StoryClickListeners() {
                                        @Override
                                        public void onDescriptionClickListener(int position) {
                                            //your action
                                        }

                                        @Override
                                        public void onTitleIconClickListener(int position) {
                                            //your action
                                        }
                                    }) // Optional Listeners
                                    .build() // Must be called before calling show method
                                    .show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        return view;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                                userStatus.setImage(user.getImage());
                                userStatus.setName(user.getName());
                                userStatus.setLastUpdated(date.getTime());
                                HashMap<String,Object> obj = new HashMap<>();
                                obj.put("Name",userStatus.getName());
                                obj.put("Image",userStatus.getImage());
                                obj.put("lastUpdated",userStatus.getLastUpdated());
                                String imageUri = uri.toString();
                                Status status = new Status(imageUri,userStatus.getLastUpdated());
                                Rootref.child("stories").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);
                                Rootref.child("stories").child(FirebaseAuth.getInstance().getUid()).child("statuses").push().setValue(status);
                                loadingbar.dismiss();
                            }
                        });
                    }
                }
            });
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void afterstaust()
    {
        LinearLayout instatus = getView().findViewById(R.id.ln_status);
        LinearLayout storyview = getView().findViewById(R.id.afterstatus);
        instatus.setVisibility(View.GONE);
        storyview.setVisibility(View.VISIBLE);
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private  void honey(){
        Rootref.child("stories").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserStatus status = new UserStatus();
                    ArrayList<Status> statuses = new ArrayList<>();
                    for(DataSnapshot statusSnapshot : snapshot.child("statuses").getChildren()){
                        Status sampleStatus = statusSnapshot.getValue(Status.class);
                        statuses.add(sampleStatus);
                    }
                    status.setStatuses(statuses);
                    status_view.setPortionsCount(status.getStatuses().size());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}