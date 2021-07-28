package com.example.gochats;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private EditText email,password;
    private TextView register;
    private Button login;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        loadingBar=new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginaccount();
            }

        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to RegisterActivity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
//////////////////////////////////////////////////////////////////////////////////////////////
    private void loginaccount()
    {
        String umail = email.getText().toString();
        String upass=password.getText().toString();
        if(umail.isEmpty()|upass.isEmpty()){
            Toast.makeText(this,"Fill all the details",Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Sign in");
            loadingBar.setMessage("Please wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            auth.signInWithEmailAndPassword(umail,upass).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String currentUserId = auth.getCurrentUser().getUid();
                                String devicetoken = FirebaseInstanceId.getInstance().getToken();

                                UserRef.child(currentUserId).child("device_token").setValue(devicetoken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            SendUserToMainActivity();
                                            Toast.makeText(LoginActivity.this,"Successfully Logged in",Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });


                            }else{
                                Toast.makeText(LoginActivity.this,"Failed to login",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void rest(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.reset,null);
        builder.setView(v);
        final EditText email = v.findViewById(R.id.umail);
        builder.setCancelable(false);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String mail = email.getText().toString();
                auth.sendPasswordResetEmail(mail).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Reset mail sent",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(LoginActivity.this,"Failure",Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        }
    });
    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    });
    builder.show();
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void lognum(View view) {
        startActivity(new Intent(this,numb.class));
    }

}