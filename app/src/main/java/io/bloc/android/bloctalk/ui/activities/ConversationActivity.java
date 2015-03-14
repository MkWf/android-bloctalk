package io.bloc.android.bloctalk.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.adapters.ConversationMessageItemAdapter;
import io.bloc.android.bloctalk.adapters.ConversationNavigationAdapter;

/**
 * Created by Mark on 3/10/2015.
 */
public class ConversationActivity extends ActionBarActivity {
    ConversationMessageItemAdapter convoMsgItemAdapter;
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerView navigationRecyclerView;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ConversationNavigationAdapter convoNavigationAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        BlocTalkApplication.getSharedDataSource().queryForMessages(this, id);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_conversation);
        toolbar.setLogo(R.mipmap.ic_app_logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.dl_activity_conversation);
        //drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        //drawerLayout.setDrawerListener(drawerToggle);

        convoMsgItemAdapter = new ConversationMessageItemAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_conversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());  //may set to null later on
        recyclerView.setAdapter(convoMsgItemAdapter);

        convoNavigationAdapter = new ConversationNavigationAdapter();
        navigationRecyclerView = (RecyclerView) findViewById(R.id.rv_nav_activity_conversation);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navigationRecyclerView.setAdapter(convoNavigationAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
       // drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if (drawerToggle.onOptionsItemSelected(item)) {
        //    return true;
        //}
        return super.onOptionsItemSelected(item);
    }
}
