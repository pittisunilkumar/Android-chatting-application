package com.example.gochats;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gochats.Adapter.ContactAdapter;
import com.example.gochats.modes.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class phonemainactivity extends AppCompatActivity {



    private RecyclerView mUserList;
    private List<Contact> userList, contactList;
    private ContactAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonemainactivity);

        mUserList= findViewById(R.id.userList);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

        contactList =new ArrayList<>();
        userList =new ArrayList<>();
        adapter = new ContactAdapter(this,contactList);
        mUserList.setAdapter(adapter);
        getContact();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getContact()
    {

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);
        while (phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phonenumbe = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phonenumbe = phonenumbe.replace(" ","");
            phonenumbe = phonenumbe.replace("-","");
            phonenumbe = phonenumbe.replace("(","");
            phonenumbe = phonenumbe.replace(")","");

            Contact contact = new Contact(name,phonenumbe);
            getUserDetails(contact);
        }
    }
    private void getUserDetails(Contact contact)
    {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = mUserDB.orderByChild("phone").equalTo(contact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String phonenumbe = "",
                           name= "";

                    for(DataSnapshot childSnapshot : snapshot.getChildren())
                    {
                        if(childSnapshot.child("phone").getValue()!=null && childSnapshot.child("name").getValue()!=null){
                            phonenumbe = childSnapshot.child("phone").getValue().toString();
                            name = childSnapshot.child("name").getValue().toString();
                            Contact contact = new Contact(name,phonenumbe);
                        contactList.add(contact);
                        adapter.notifyDataSetChanged();
                        return;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void backtomaingochat(View view){startActivity(new Intent(this, MainActivity.class));finish();}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(phonemainactivity.this,MainActivity.class));
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}