package com.codencode.chitchat;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.text.DateFormat.getDateTimeInstance;

public class ChatActivity extends AppCompatActivity {

    private String userId;
    private String userName;
    private String currUserId;
    private static final int ITEMS_TO_LOAD = 15;
    private int mCurrPage = 1;
    private int lastPos = 0;
    private String lastMessageKey="";

    private RecyclerView mRecyclerView;
    MessageAdapter adapter;
    private List<Messages> mMessageList = new ArrayList<>();
    Toolbar mToolbar;
    SwipeRefreshLayout mRefreshLayout;

    TextView userNameView , lastSeenView;
    CircleImageView userImgView;

    ImageButton mSendBtn , mAddBtn;
    EditText mMessageText;

    DatabaseReference mUserRef , rootRef;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getStringExtra("user_uid");
        mToolbar = findViewById(R.id.chat_appbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View custom_appbar_view = inflater.inflate(R.layout.chat_appbar_layout , null);
        actionBar.setCustomView(custom_appbar_view);

        initViews(custom_appbar_view);
        setUserDetails(custom_appbar_view);
        loadMessages();

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                lastPos = 0;
                mCurrPage++;
                loadMoreMessages();
            }
        });
    }

    private void loadMoreMessages() {
        Query messageQuery = rootRef.child("Messages").child(currUserId).child(userId)
                .orderByKey().endAt(lastMessageKey)
                .limitToLast(ITEMS_TO_LOAD);
        final List<Messages> tmpList = new ArrayList<>(mMessageList);
        mMessageList.clear();

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                mMessageList.add(lastPos++ , messages);
                if (lastPos == 1)
                    lastMessageKey = dataSnapshot.getKey();

                if(lastPos == ITEMS_TO_LOAD)
                    mMessageList.remove(lastPos-1);
                adapter.notifyDataSetChanged();

                mRecyclerView.smoothScrollToPosition(lastPos-1);
                mRefreshLayout.setRefreshing(false);
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


        for(int i=0;i<tmpList.size();i++)
        {
            mMessageList.add(tmpList.get(i));
        }
        adapter.notifyDataSetChanged();
        tmpList.clear();
    }

    private void loadMessages() {

        Query messageQuery = rootRef.child("Messages").child(currUserId).child(userId).limitToLast(mCurrPage*ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                lastPos++;
                if(lastPos == 1)
                    lastMessageKey = dataSnapshot.getKey();
                Messages messages = dataSnapshot.getValue(Messages.class);
                mMessageList.add(messages);
                adapter.notifyDataSetChanged();

                mRecyclerView.scrollToPosition(mMessageList.size()-1);
                mRefreshLayout.setRefreshing(false);
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

    private void initViews(View view)
    {
        userNameView = view.findViewById(R.id.chat_appbar_name);
        lastSeenView = view.findViewById(R.id.chat_appbar_lastseen);
        userImgView = view.findViewById(R.id.chat_appbar_img);

        mRecyclerView = findViewById(R.id.chat_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        adapter = new MessageAdapter(mMessageList, this);
        mRecyclerView.setAdapter(adapter);
        mRefreshLayout = findViewById(R.id.chat_swipe_refresh);


        mSendBtn = findViewById(R.id.chat_send_btn);
        mAddBtn = findViewById(R.id.chat_add_btn);
        mMessageText = findViewById(R.id.chat_messge_text);

        mAuth = FirebaseAuth.getInstance();
        currUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("Chat").child(currUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(userId))
                {
                    Map message = new HashMap();
                    message.put("seen" , false);
                    message.put("time_stamp" , ServerValue.TIMESTAMP);

                    Map chatUser = new HashMap();
                    chatUser.put("Chat/"+currUserId+"/"+userId , message);
                    chatUser.put("Chat/"+userId + "/" + currUserId , message);

                    rootRef.updateChildren(chatUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //--------- handeling message sending here------------//
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    public void setUserDetails(View view)
    {
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean online = (boolean) dataSnapshot.child("online").getValue();
                userName = dataSnapshot.child("name").getValue().toString();
                final String thumb = dataSnapshot.child("thumb_ref").getValue().toString();
                userNameView.setText(userName);
                if(online)
                    lastSeenView.setText("Online");
                else
                lastSeenView.setText(getTimeDate((Long) dataSnapshot.child("last_seen").getValue()));

                Picasso.get().load(thumb).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(userImgView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(thumb).into(userImgView);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String getTimeDate(long timestamp){
        GetTimeAgo gta = new GetTimeAgo();
        return gta.getTimeAgo(timestamp , getApplicationContext());
    }

    public void sendMessage()
    {
        String message = mMessageText.getText().toString();
        mMessageText.setText("");
        if(!TextUtils.isEmpty(message))
        {
            String curr_user_ref = "Messages/" + currUserId + "/" + userId;
            String chat_user_ref = "Messages/" + userId + "/" + currUserId;

            DatabaseReference message_push = rootRef.child("Messages").child(currUserId).child(userId).push();
            String push_key = message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message" , message);
            messageMap.put("seen" , false);
            messageMap.put("type" , "text");
            messageMap.put("time" , ServerValue.TIMESTAMP);
            messageMap.put("from" , currUserId);

            Map userMap = new HashMap();
            userMap.put(curr_user_ref + "/" + push_key , messageMap);
            userMap.put(chat_user_ref + "/" + push_key , messageMap);

            rootRef.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    Log.d("UID" , "Bas aise hi");
                }
            });
        }
    }

}
