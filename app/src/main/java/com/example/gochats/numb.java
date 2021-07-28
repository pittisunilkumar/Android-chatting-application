package com.example.gochats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class numb extends AppCompatActivity {

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    PhoneAuthProvider.ForceResendingToken token;

    public Button continuebutton;
    public EditText phone_input;
    String number;


    FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numb);

        continuebutton=findViewById(R.id.next5);
        phone_input=findViewById(R.id.phone);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(numb.this,otpScreen.class);
                number =phone_input.getText().toString();
                intent.putExtra("number",number);
                startActivity(intent);

            }
        });
    }

    public void backlog(View view) {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}

