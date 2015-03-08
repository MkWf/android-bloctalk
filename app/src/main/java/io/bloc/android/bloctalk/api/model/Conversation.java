package io.bloc.android.bloctalk.api.model;

/**
 * Created by Mark on 3/8/2015.
 */
public class Conversation {
    String name;

    public Conversation(String name){
        setName(name);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
