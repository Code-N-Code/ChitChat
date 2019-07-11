package com.codencode.chitchat;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    View mView;
    ArrayList<ChatFragmentDataPacket> friendsChatData = new ArrayList<>();
    RecyclerView mRecyclerView;
    DatabaseReference mChatRef , mUserRef , mMessageRef;
    FirebaseAuth mAuth;
    ChatFragmentAdapter adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chats , container , false);

        mAuth = FirebaseAuth.getInstance();
        mChatRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(mAuth.getCurrentUser().getUid());
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageRef = FirebaseDatabase.getInstance().getReference().child("Messages");

        mRecyclerView = mView.findViewById(R.id.chat_fragment_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        adapter = new ChatFragmentAdapter(friendsChatData , getContext());
        mRecyclerView.setAdapter(adapter);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        friendsChatData.clear();
        mChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    ChatFragmentDataPacket packet = ds.getValue(ChatFragmentDataPacket.class);
                    packet.setUid(ds.getKey());
                    getLastMessage(ds.getKey() , packet);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLastMessage(String key , final ChatFragmentDataPacket packet) {

        Query q = mMessageRef.child(mAuth.getCurrentUser().getUid()).child(key).limitToLast(1);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                packet.setLastMessage(dataSnapshot.child("message").getValue().toString());
                friendsChatData.add(packet);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
