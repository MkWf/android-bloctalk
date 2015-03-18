package io.bloc.android.bloctalk.api.model;

import android.net.Uri;

/**
 * Created by Mark on 3/8/2015.
 */
public class ConversationItem {
    int id;
    String address;
    String name;
    Uri photo;
    int unreadMsgCount;

    public ConversationItem(int id, String name, Uri photo, String address, int unreadMsgCount){
        setId(id);
        setName(name);
        setPhoto(photo);
        setAddress(address);
        setUnreadMsgCount(unreadMsgCount);
    }

    public int getId() { return id; }
    public String getName(){
        return name;
    }
    public Uri getPhoto() { return photo; }
    public int getUnreadMsgCount() { return unreadMsgCount;}
    public String getAddress(){
        return address;
    }

    public void setAddress(String Address){ this.address = address; }
    public void setId(int id) { this.id = id; }
    public void setName(String name){
        this.name = name;
    }
    public void setPhoto(Uri photo) {this.photo = photo;}
    public void setUnreadMsgCount(int unreadMsgCount) {this.unreadMsgCount = unreadMsgCount;}
}
