package io.bloc.android.bloctalk.api.model;

/**
 * Created by Mark on 3/13/2015.
 */
public class MessageItem {
    public static final int INCOMING_MSG = 1;
    public static final int OUTGOING_MSG = 2;

    public static final int UNREAD_MSG = 0;
    public static final int READ_MSG = 1;

    String id;
    String body;
    int read;
    int type;
    String time;

    public MessageItem(String body, int read, int type, String time){
        //setId(id);
        setBody(body);
        setRead(read);
        setType(type);
        setTime(time);
    }

    public void setBody(String body) { this.body = body; }
    public void setId(String id) { this.id = id; }
    public void setRead(int read) { this.read = read; }
    public void setType(int type) { this.type = type;}
    public void setTime(String time) { this.time = time;}

    public String getBody() { return body; }
    public String getId() { return id; }
    public int getRead() { return read; }
    public int getType() { return type; }
    public String getTime() { return time; }
}
