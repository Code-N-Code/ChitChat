package com.codencode.chitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    DatabaseReference mDatabaseReference;
    ArrayList<UserProfileDataPacket> mList;
    Toolbar mToolbar;
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mList = new ArrayList<>();
        mRecyclerView = findViewById(R.id.allusers_recyclerview);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(mList , this , 0);
        mRecyclerView.setAdapter(adapter);

        mToolbar = findViewById(R.id.allusers_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren())
                if(!ds.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    UserProfileDataPacket packet = ds.getValue(UserProfileDataPacket.class);
                    packet.setUid(ds.getKey());
                    updateAdapter(packet);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateAdapter(UserProfileDataPacket packet)
    {
        for(int i=0;i<mList.size();i++)
        {
            String uid = mList.get(i).getUid();
            if(uid.equals(packet.getUid()))
            {
                mList.remove(i);
                break;
            }
        }

        mList.add(packet);
        adapter.notifyDataSetChanged();
    }
}
