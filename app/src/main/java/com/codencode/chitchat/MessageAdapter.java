package com.codencode.chitchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Messages> mMessageList;
    private Context context;
    private static final int SENT_MESSAGE = 1;
    private static final int REC_MESSAGE = 2;

    MessageAdapter(List<Messages> mMessageList , Context context)
    {
        this.mMessageList = mMessageList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(mMessageList.get(position).from.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            return SENT_MESSAGE;
        else
            return REC_MESSAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SENT_MESSAGE)
        {
            View v = LayoutInflater.from(context).inflate(R.layout.sent_message_layout , parent , false);
            return new SentMessage(v);
        }
        else
        {
            View v = LayoutInflater.from(context).inflate(R.layout.single_message_layout , parent , false);
            return new RecMessage(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == SENT_MESSAGE)
        {
            ((SentMessage)holder).messageText.setText(mMessageList.get(position).getMessage());
        }
        else
        {
            Messages messages = mMessageList.get(position);
            String msg = messages.getMessage();
            FirebaseDatabase.getInstance().getReference().child("Users").child(messages.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Picasso.get().load(dataSnapshot.child("thumb_ref").getValue()
                            .toString()).into(((RecMessage)holder).senderImg);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            ((RecMessage)holder).messageText.setText(msg);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class RecMessage extends RecyclerView.ViewHolder {
        TextView messageText;
        CircleImageView senderImg;

        public RecMessage(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.single_message_text);
            senderImg = itemView.findViewById(R.id.single_message_img);
        }
    }

    public class SentMessage extends RecyclerView.ViewHolder {
        TextView messageText;

        public SentMessage(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.sent_message_text);
        }
    }
}
