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

import org.w3c.dom.Text;

public class Login extends AppCompatActivity {
    Toolbar mToolbar;
    TextInputLayout email , password;
    Button mLoginButton;
    private FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        //toolbar
        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase connection & value updation
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        mLoginButton = findViewById(R.id.login_btn);
        mProgressDialog = new ProgressDialog(Login.this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String EMail = email.getEditText().getText().toString();
                String Pasword = password.getEditText().getText().toString();
                if(!TextUtils.isEmpty(EMail) && !TextUtils.isEmpty(Pasword))
                {
                    mProgressDialog.setTitle("Login");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    loginUser(EMail , Pasword);
                }
            }
        });

    }

    //User Login Procedure
    private void loginUser(String EMail , String Password)
    {
        mAuth.signInWithEmailAndPassword(EMail , Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    mProgressDialog.hide();
                    Intent intent = new Intent(Login.this , MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    mProgressDialog.hide();
                    Toast.makeText(Login.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
