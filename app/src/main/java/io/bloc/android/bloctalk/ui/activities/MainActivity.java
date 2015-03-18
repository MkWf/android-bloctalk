package io.bloc.android.bloctalk.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.adapters.ConversationItemAdapter;
import io.bloc.android.bloctalk.api.model.ConversationItem;

/**
 * Created by Mark on 3/8/2015.
 */
public class MainActivity extends ActionBarActivity implements ConversationItemAdapter.Delegate {

    ConversationItemAdapter convoItemAdapter;
    Toolbar toolbar;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlocTalkApplication.getSharedInstance().setCurrentActivity(this);

        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName())) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
            startActivity(intent);
        }

        BlocTalkApplication.getSharedDataSource().query(this);

        toolbar = (Toolbar) findViewById(R.id.tb_activity_main);
        toolbar.setLogo(R.mipmap.ic_app_logo);
        toolbar.setTitle("BlocTalk");
        setSupportActionBar(toolbar);

        convoItemAdapter = new ConversationItemAdapter();
        convoItemAdapter.setDelegate(this);

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

    @Override
    public void onItemClicked(ConversationItemAdapter itemAdapter, ConversationItem convoItem) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra("id", convoItem.getId());
        intent.putExtra("name", convoItem.getName());
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BlocTalkApplication.getSharedDataSource().getConvos().clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BlocTalkApplication.getSharedInstance().setCurrentActivity(this);
        BlocTalkApplication.getSharedDataSource().query(this);
        convoItemAdapter.notifyDataSetChanged();
    }

    public void notifyAdapter(){
        convoItemAdapter.notifyDataSetChanged();
    }
}
