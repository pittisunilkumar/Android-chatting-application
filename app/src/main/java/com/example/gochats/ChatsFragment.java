package com.example.gochats;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.modes.Messages;
import com.example.gochats.modes.Model;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ChatsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";private static final String ARG_PARAM2 = "param2";private String mParam1;private String mParam2;public ChatsFragment() {}public static ChatsFragment newInstance(String param1, String param2) { ChatsFragment fragment = new ChatsFragment();Bundle args = new Bundle();args.putString(ARG_PARAM1, param1);args.putString(ARG_PARAM2, param2);fragment.setArguments(args);return fragment;}@Override

    public void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);if (getArguments() != null) { mParam1 = getArguments().getString(ARG_PARAM1);mParam2 = getArguments().getString(ARG_PARAM2); }}
    private RecyclerView recyclerView;
    private DatabaseReference ChatRef,UserRef;
    private FirebaseAuth mAuth;
    public String currentID;
    private DatabaseReference Rootref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) { View view=inflater.inflate(R.layout.fragment_chats, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentID = mAuth.getCurrentUser().getUid();
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentID);
        UserRef =FirebaseDatabase.getInstance().getReference().child("Users");
        Rootref = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recyclerView25);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Model>options = new  FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(ChatRef,Model.class)
                .build();
        FirebaseRecyclerAdapter<Model,ChatsViewHolder>adapter = new FirebaseRecyclerAdapter<Model, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Model model)
            {
                final String userID = getRef(position).getKey();
                final String[] retImage = {"default_image"};
                Rootref.child("Messages").child(currentID).child(userID).addChildEventListener(
                        new ChildEventListener()
                        {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                            {
                                Messages messages =snapshot.getValue(Messages.class);
                                String fromMessageType = messages.getType();
                                if(fromMessageType.equals("image"))
                                {
                                    holder.lastmeassage.setText("photo");
                                }else if(fromMessageType.equals("text"))
                                {
                                    holder.lastmeassage.setText(messages.getMessage());
                                }else if(fromMessageType.equals("video"))
                                {
                                    holder.lastmeassage.setText("Video");
                                }
                            }
                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName){}
                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot){}
                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName){}
                        @Override
                        public void onCancelled(@NonNull DatabaseError error){}});


                UserRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                       if(snapshot.exists())
                       {
                           if(snapshot.hasChild("image"))
                           {
                               retImage[0] = snapshot.child("image").getValue().toString();
                               Picasso.get().load(retImage[0]).into(holder.profileImage);
                           }
                           final String retName = snapshot.child("name").getValue().toString();
                           holder.userName.setText(retName);
                           holder.itemView.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(getContext(),chatactivity.class);
                                   intent.putExtra("visit_user_id",userID);
                                   intent.putExtra("visit_user_name",retName);
                                   intent.putExtra("visit_image", retImage[0]);
                                   startActivity(intent);
                               }
                           });
                       }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.list_chats,parent,false);
            return new ChatsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircularImageView profileImage;
        TextView userName,lastmeassage;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.image_profile21);
            userName=itemView.findViewById(R.id.tv_name21);
            lastmeassage = itemView.findViewById(R.id.tv_desc21);
        }
    }
}