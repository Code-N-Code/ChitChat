package com.codencode.chitchat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Toolbar mToolbar;
    ViewPager mViewPager;
    MyPagerAdapter myPagerAdapter;
    TabLayout mTablayout;
    DatabaseReference mUserRef;

    boolean offline = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChitChat");

        //tabs
        mViewPager = findViewById(R.id.main_tabpager);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(myPagerAdapter);

        mTablayout = findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        offline = true;
        FirebaseUser mUser = mAuth.getCurrentUser();

        if(mUser == null)
        {
            sentToStart();
        }
        else
        {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mUserRef.child("token_id").setValue(FirebaseInstanceId.getInstance().getToken());
            mUserRef.child("online").setValue(true);
        }
    }


    //setting menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }


    //Item Selection Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn)
        {
            mUserRef.child("online").setValue(false);
            FirebaseAuth.getInstance().signOut();
            sentToStart();
        }

        else
        if(item.getItemId() == R.id.main_account_settings)
        {
            Intent settingsIntent = new Intent(MainActivity.this , SettingsActivity.class);
            offline = false;
            startActivity(settingsIntent);
        }

        else
        if(item.getItemId() == R.id.main_all_btn)
        {
            Intent profileIntent = new Intent(MainActivity.this , AllUsersActivity.class);
            offline = false;
            startActivity(profileIntent);
        }
        return true;
    }


    //Sending to start activity
    private void sentToStart() {
        startActivity(new Intent(this , StartActivity.class));
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(offline && mUserRef != null)
        {
            mUserRef.child("last_seen").setValue(ServerValue.TIMESTAMP);
            mUserRef.child("online").setValue(false);
        }
    }
}
