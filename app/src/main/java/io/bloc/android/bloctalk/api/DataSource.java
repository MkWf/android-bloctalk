package io.bloc.android.bloctalk.api;

import java.util.ArrayList;
import java.util.List;

import io.bloc.android.bloctalk.api.model.Conversation;

/**
 * Created by Mark on 3/8/2015.
 */
public class DataSource {

    List<Conversation> convos;

    public DataSource() {
        convos = new ArrayList<>();
        createFakeData();
    }

    public List<Conversation> getConvos(){
        return convos;
    }

    public void createFakeData(){
        for(int i = 0; i < 6; i++){
            convos.add(new Conversation("Mark W."));
        }
    }
}
