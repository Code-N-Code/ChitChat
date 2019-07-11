package com.codencode.chitchat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    View mView;
    ArrayList<UserProfileDataPacket> friendsProfileData;
    RecyclerView mRecyclerView;
    DatabaseReference mFriendsDatabase , mUsersRef;
    String user_id = "";
    FirebaseAuth mAuth;
    MyAdapter adapter;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsProfileData = new ArrayList<>();
        adapter = new MyAdapter(friendsProfileData , getContext() , 1);
        mRecyclerView = mView.findViewById(R.id.friends_fragment_recyclerView);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFriendsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendsProfileData.clear();
                adapter.notifyDataSetChanged();
                for(DataSnapshot ds : dataSnapshot.getChildren())
                {
                    final String user_uid = ds.getKey();
                    long timeDate = (long) ds.child("date").getValue();
                    final String user_date = getTimeDate(timeDate);

                    mUsersRef.child(ds.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String user_img = dataSnapshot.child("thumb_ref").getValue().toString();
                            String user_name = dataSnapshot.child("name").getValue().toString();
                            boolean online = (boolean) dataSnapshot.child("online").getValue();
                            UserProfileDataPacket packet = new UserProfileDataPacket(user_img , user_name , user_date);
                            packet.setOnline(online);
                            packet.setUid(user_uid);
                            updateAdapter(packet);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateAdapter(UserProfileDataPacket packet) {
        for(int i=0;i<friendsProfileData.size();i++)
        {
            String uid = friendsProfileData.get(i).getUid();
            if(uid.equals(packet.getUid()))
            {
                friendsProfileData.remove(i);
                break;
            }
        }
        friendsProfileData.add(packet);
        adapter.notifyDataSetChanged();
    }

    public static String getTimeDate(long timestamp){
        try{
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timestamp));
            return dateFormat.format(netDate);
        } catch(Exception e) {
            return "date";
        }
    }
}
