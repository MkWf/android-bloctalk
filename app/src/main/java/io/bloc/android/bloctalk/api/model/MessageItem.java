package io.bloc.android.bloctalk.api.model;

/**
 * Created by Mark on 3/13/2015.
 */
public class MessageItem {
    int id;
    String body;
    int read;

    public MessageItem(String body, int read){
        setBody(body);
        setRead(read);
    }

    public void setBody(String body) { this.body = body; }
    public void setId(int id) { this.id = id; }
    public void setRead(int read) { this.id = read; }

    public String getBody() { return body; }
    public int getId() { return id; }
    public int getRead() { return read; }
}
