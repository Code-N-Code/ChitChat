package com.codencode.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout mDisplayName , mEMail , mPassword;
    Button mCreatebtn;
    private FirebaseAuth mAuth;
    Toolbar mToolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Toolbar
        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);

        mDisplayName = findViewById(R.id.reg_display_name);
        mEMail       = findViewById(R.id.reg_email);
        mPassword    = findViewById(R.id.reg_password);

        mCreatebtn = findViewById(R.id.reg_create_btn);
        mCreatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEMail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {
                    progressDialog.setTitle("Registering...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    registerUser(display_name , email , password , FirebaseInstanceId.getInstance().getToken());
                }
                else
                    Toast.makeText(RegisterActivity.this, "Please Enter all details", Toast.LENGTH_SHORT).show();
            }
        });
    }



    //Registration Procedure : To register User and his details
    private void registerUser(final String display_name, String email, String password , final String token_id) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            final DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            //setting User Values
                            HashMap<String , String> values = new HashMap<>();
                            values.put("name" , display_name);
                            values.put("status" , "Hi there , I'm Using ChitChat");
                            values.put("img_ref" , "default");
                            values.put("thumb_ref" , "default");
                            values.put("token_id" , token_id);


                            mref.setValue(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        mref.child("online").setValue(true);
                                        Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }

                                    else
                                        Toast.makeText(RegisterActivity.this, "Data Can't be saved , try again!!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            progressDialog.hide();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
