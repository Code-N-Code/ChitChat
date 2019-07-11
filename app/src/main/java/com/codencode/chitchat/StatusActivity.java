package com.codencode.chitchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Button statusUpdateButton;
    TextInputLayout inputLayout;
    DatabaseReference mRef;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        //Firebse
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        //Toolbar section
        mToolbar = findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Set Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TextInputLayout and Status fetch area
        inputLayout = findViewById(R.id.status_input_text);
        inputLayout.getEditText().setText(getIntent().getStringExtra("status_value"));


        //Status update button and update function
        statusUpdateButton = findViewById(R.id.status_update_btn);
        statusUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = new ProgressDialog(StatusActivity.this);
                mProgressDialog.setTitle("Updating Status...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                
                
                final String status = inputLayout.getEditText().getText().toString();
                mRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mProgressDialog.dismiss();
                            Toast.makeText(StatusActivity.this, "Status Changed Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(StatusActivity.this, "Something went wrong , try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
