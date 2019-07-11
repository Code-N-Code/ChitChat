package com.codencode.chitchat;

public class ChatFragmentDataPacket {
    private long time_stamp;
    private boolean seen;
    private String uid;
    private String lastMessage;

    ChatFragmentDataPacket(){}

    public ChatFragmentDataPacket(long time_stamp, boolean seen, String uid) {
        this.time_stamp = time_stamp;
        this.seen = seen;
        this.uid = uid;
    }


    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
