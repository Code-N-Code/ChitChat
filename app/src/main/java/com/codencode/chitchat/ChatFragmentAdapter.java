package com.codencode.chitchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragmentAdapter extends RecyclerView.Adapter<ChatFragmentAdapter.ViewHolder> {


    ArrayList<ChatFragmentDataPacket> mChatList;
    Context context;
    ChatFragmentAdapter(ArrayList<ChatFragmentDataPacket> mChatList , Context context)
    {
        this.mChatList = mChatList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChatFragmentDataPacket packet = mChatList.get(position);

        FirebaseDatabase.getInstance().getReference().child("Users").child(packet.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.child("thumb_ref").getValue().toString()).into(holder.mUserImg);
                holder.userName.setText(dataSnapshot.child("name").getValue().toString());
                if((boolean)dataSnapshot.child("online").getValue())
                    holder.onlineImg.setVisibility(View.VISIBLE);
                else
                    holder.onlineImg.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.userStatus.setText(packet.getLastMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(context , ChatActivity.class);
                chatIntent.putExtra("user_uid" , packet.getUid());
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView mUserImg;
        ImageView onlineImg;
        TextView userName , userStatus;
        public ViewHolder(View itemView) {
            super(itemView);

            mUserImg = itemView.findViewById(R.id.singleuser_profile_img);
            userName = itemView.findViewById(R.id.singleuser_name);
            userStatus = itemView.findViewById(R.id.singleuser_status);
            onlineImg = itemView.findViewById(R.id.single_user_layout_green_dot);
        }
    }

}
