package com.codencode.chitchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public ArrayList<UserProfileDataPacket> mList;
    public Context context;
    int fromWhere = 0;
    MyAdapter(ArrayList<UserProfileDataPacket> mList , Context context , int fromWhere)
    {
        this.mList = mList;
        this.context = context;
        this.fromWhere = fromWhere;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user_layout , parent , false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserProfileDataPacket packet = mList.get(position);
        holder.userName.setText(packet.getName());
        holder.userStatus.setText(packet.getStatus());
        if(packet.isOnline())
            holder.onlineImg.setVisibility(View.VISIBLE);
        else
            holder.onlineImg.setVisibility(View.INVISIBLE);

//        Picasso.get().load(packet.getImg_ref()).placeholder(R.drawable.default_avatar).into(holder.userProfileImg);
                Picasso.get().load(packet.getImg_ref()).networkPolicy(NetworkPolicy.OFFLINE).
                placeholder(R.drawable.default_avatar).into(holder.userProfileImg, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(packet.getImg_ref()).placeholder(R.drawable.default_avatar).into(holder.userProfileImg);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fromWhere == 1)
                {
                    CharSequence option[] = new CharSequence[]{"Open Profile" ,"Open Chat"};
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                    mBuilder.setTitle("Select Options");
                    mBuilder.setItems(option, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i == 0)
                            {
                                Intent profileIntent = new Intent(context , ProfileActivity.class);
                                profileIntent.putExtra("user_uid" , packet.getUid());
                                context.startActivity(profileIntent);
                            }

                            if(i == 1)
                            {
                                Intent chatIntent = new Intent(context , ChatActivity.class);
                                chatIntent.putExtra("user_uid" , packet.getUid());
                                context.startActivity(chatIntent);
                            }
                        }
                    });
                    mBuilder.show();
                }
                else
                {
                    Intent profileIntent = new Intent(context , ProfileActivity.class);
                    profileIntent.putExtra("user_uid" , packet.getUid());
                    context.startActivity(profileIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImg , onlineImg;
        TextView userName , userStatus;
        public ViewHolder(View itemView) {
            super(itemView);

            userProfileImg = itemView.findViewById(R.id.singleuser_profile_img);
            userName = itemView.findViewById(R.id.singleuser_name);
            userStatus = itemView.findViewById(R.id.singleuser_status);
            onlineImg = itemView.findViewById(R.id.single_user_layout_green_dot);
        }
    }
}
