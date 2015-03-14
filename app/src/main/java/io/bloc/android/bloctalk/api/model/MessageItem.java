package io.bloc.android.bloctalk.api.model;

/**
 * Created by Mark on 3/13/2015.
 */
public class MessageItem {
    public static final int INCOMING_MSG = 1;
    public static final int OUTGOING_MSG = 2;

    int id;
    String body;
    int read;
    int sender;

    public MessageItem(String body, int read, int sender){
        setBody(body);
        setRead(read);
        setSender(sender);
    }

    public void setBody(String body) { this.body = body; }
    public void setId(int id) { this.id = id; }
    public void setRead(int read) { this.id = read; }
    public void setSender(int sender) { this.sender = sender;}

    public String getBody() { return body; }
    public int getId() { return id; }
    public int getRead() { return read; }
    public int getSender() { return sender; }
}
