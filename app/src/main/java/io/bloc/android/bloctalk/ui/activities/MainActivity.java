package io.bloc.android.bloctalk.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.adapters.ConversationItemAdapter;

/**
 * Created by Mark on 3/8/2015.
 */
public class MainActivity extends ActionBarActivity {

    ConversationItemAdapter convoItemAdapter;
    Toolbar toolbar;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_main);
        toolbar.setLogo(R.mipmap.ic_app_logo);
        toolbar.setTitle("BlocTalk");
        setSupportActionBar(toolbar);

        convoItemAdapter = new ConversationItemAdapter();

        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_main);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());  //may set to null later on
        recyclerView.setAdapter(convoItemAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
