package com.codencode.chitchat;

public class UserProfileDataPacket {
    public String img_ref;
    public String name;
    public String status;
    public String uid;
    public boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserProfileDataPacket(){};

    public UserProfileDataPacket(String img_ref , String name , String status)
    {
        this.img_ref = img_ref;
        this.name = name;
        this.status = status;
    }

    public String getImg_ref() {
        return img_ref;
    }

    public void setImg_ref(String img_ref) {
        this.img_ref = img_ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
