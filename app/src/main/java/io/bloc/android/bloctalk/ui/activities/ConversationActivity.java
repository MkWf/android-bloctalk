package io.bloc.android.bloctalk.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.adapters.ConversationMessageItemAdapter;

/**
 * Created by Mark on 3/10/2015.
 */
public class ConversationActivity extends ActionBarActivity {
    ConversationMessageItemAdapter convoMsgItemAdapter;
    Toolbar toolbar;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_conversation);
        toolbar.setLogo(R.mipmap.ic_app_logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        convoMsgItemAdapter = new ConversationMessageItemAdapter();

        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_conversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());  //may set to null later on
        recyclerView.setAdapter(convoMsgItemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
