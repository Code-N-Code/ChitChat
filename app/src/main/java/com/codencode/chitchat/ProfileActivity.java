package com.codencode.chitchat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView mProfileImg;
    TextView mProfileName , mProfileStatus , mProfileFriendCnt;
    Button mProfileFriendReqBtn , mProfileDeclineBtn;
    String mUid , mCurr_id , mCurrUserName = "";
    DatabaseReference userDatabseReference , mFriendList , mFriendReq , mNotificationDatabase , mCurrUserReference , mFriendsRef;

    int req_status = 0;
    //0 = not friend , 1 = friend , 2 = friend reques sent , 3 = friend request received
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        initFirebase();

        mFriendList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mUid))
                {
                    req_status = Integer.parseInt(dataSnapshot.child(mUid).getValue().toString());
                    setRequestType(req_status);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFriendsRef.child(mCurr_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mUid))
                {
                    req_status = 1;
                    setRequestType(req_status);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //-------------- Loading User Profile-----------------//
        userDatabseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.child("name").getValue().toString();
                String user_status = dataSnapshot.child("status").getValue().toString();
                String img_url = dataSnapshot.child("img_ref").getValue().toString();

                mProfileName.setText(user_name);
                mProfileStatus.setText(user_status);
                Picasso.get().load(img_url).placeholder(R.drawable.default_avatar).into(mProfileImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error : Profile Could not be loaded", Toast.LENGTH_SHORT).show();
            }
        });


        //---------------Handeling Friend request button------------------//
        mProfileFriendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileFriendReqBtn.setEnabled(false);

                if(req_status == 0)
                {
                    mFriendReq.child(mCurr_id).child(mUid).setValue("2").
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mFriendReq.child(mUid).child(mCurr_id).setValue("3");
                                HashMap<String , String> map = new HashMap<>();
                                map.put("from" ,mCurrUserName);
                                map.put("type" , "1");
                                mNotificationDatabase.child(mUid).child(mCurr_id).setValue(map);
                                setRequestType(2);
                                req_status = 2;
                            }
                            else
                            {
                                Toast.makeText(ProfileActivity.this, "Error : In sending request , try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                if(req_status == 1)
                {
                    deleteFriendReq();
                    req_status = 0;
                    setRequestType(req_status);
                }

                if(req_status == 2)
                {
                    mFriendReq.child(mCurr_id).child(mUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                mFriendReq.child(mUid).child(mCurr_id).removeValue();
                                mNotificationDatabase.child(mUid).child(mCurr_id).removeValue();
                                mNotificationDatabase.child(mCurr_id).child(mUid).removeValue();
                                setRequestType(0);
                                req_status = 0;
                            }
                        }
                    });
                }

                if(req_status == 3)
                {
                    Map mp = new HashMap();
                    mp.put(mUid + "/" + mCurr_id + "/date" , ServerValue.TIMESTAMP);
                    mp.put(mCurr_id + "/" + mUid + "/date" , ServerValue.TIMESTAMP);
                    mFriendsRef.updateChildren(mp).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ProfileActivity.this, "Request accepted succesfully", Toast.LENGTH_SHORT).show();
                                req_status = 1;
                                setRequestType(req_status);
                                deleteFriendReq();
                                mNotificationDatabase.child(mUid).child(mCurr_id).removeValue();
                                mNotificationDatabase.child(mCurr_id).child(mUid).removeValue();
                            }
                            else
                                Toast.makeText(ProfileActivity.this, "Error : Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                mProfileFriendReqBtn.setEnabled(true);
            }
        });

        //--------handeling decline button click -------//
        mProfileDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileDeclineBtn.setEnabled(false);
                if(req_status == 3)
                {
                    mNotificationDatabase.child(mCurr_id).child(mUid).removeValue();
                    req_status = 0;
                    setRequestType(req_status);
                    deleteFriendReq();
                    mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                }
                mProfileDeclineBtn.setEnabled(true);

            }
        });
        //-----------onCreate() ends here-----------------//
    }


    //---------initializing firebase instances-----------//
    private void initFirebase() {
        mUid = getIntent().getStringExtra("user_uid");
        mCurr_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


        userDatabseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUid);
        mCurrUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurr_id);
        mCurrUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrUserName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFriendReq = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mFriendList = mFriendReq.child(mCurr_id);
    }


    //-------initializing views----------//
    private void initViews() {
        mProfileImg = findViewById(R.id.profile_dp);
        mProfileName = findViewById(R.id.profile_name);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileFriendCnt = findViewById(R.id.profile_friend_cnt);
        mProfileFriendReqBtn = findViewById(R.id.profile_friend_req_btn);
        mProfileDeclineBtn = findViewById(R.id.profile_decline_friend_btn);
        mProfileDeclineBtn.setVisibility(View.INVISIBLE);

    }


    //---------setting button status------------//
    private void setRequestType(int type)
    {
        if(type == 0)
            mProfileFriendReqBtn.setText("SEND FRIEND REQUEST");
        else
        if(type == 1)
        {
            mProfileFriendReqBtn.setText("REMOVE FRIEND");
            mProfileDeclineBtn.setVisibility(View.INVISIBLE);
        }
        else
        if(type == 2)
            mProfileFriendReqBtn.setText("CANCLE FRIEND REQUEST");
        else
        if(type == 3)
        {
            mProfileFriendReqBtn.setText("ACCEPT REQUEST");
            mProfileDeclineBtn.setVisibility(View.VISIBLE);
            mProfileDeclineBtn.setText("DECLINE REQUEST");
        }
    }

    void deleteFriendReq()
    {
        mFriendReq.child(mUid).child(mCurr_id).removeValue();
        mFriendReq.child(mCurr_id).child(mUid).removeValue();
    }

}
