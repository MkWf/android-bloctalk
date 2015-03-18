package io.bloc.android.bloctalk.ui.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.adapters.ConversationMessageItemAdapter;
import io.bloc.android.bloctalk.adapters.ConversationNavigationAdapter;
import io.bloc.android.bloctalk.api.model.MessageItem;

/**
 * Created by Mark on 3/10/2015.
 */
public class ConversationActivity extends ActionBarActivity implements View.OnClickListener{
    private ConversationMessageItemAdapter convoMsgItemAdapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView navigationRecyclerView;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private ConversationNavigationAdapter convoNavigationAdapter;
    private EditText message;
    private Button sendButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        BlocTalkApplication.getSharedInstance().setCurrentActivity(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String name = intent.getStringExtra("name");

        BlocTalkApplication.getSharedDataSource().queryForMessages(this, id);


        toolbar = (Toolbar) findViewById(R.id.tb_activity_conversation);
        toolbar.setLogo(R.mipmap.ic_app_logo);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.dl_activity_conversation);
        //drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);
        //drawerLayout.setDrawerListener(drawerToggle);

        convoMsgItemAdapter = new ConversationMessageItemAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.rv_activity_conversation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setVerticalScrollBarEnabled(true);
       // recyclerView.setScrollbarFadingEnabled(false);
        //recyclerView.setScrollBarSize(5);
        recyclerView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        recyclerView.setAdapter(convoMsgItemAdapter);

        recyclerView.scrollToPosition(BlocTalkApplication.getSharedDataSource().getMsgs().size() - 1);

        convoNavigationAdapter = new ConversationNavigationAdapter();
        navigationRecyclerView = (RecyclerView) findViewById(R.id.rv_nav_activity_conversation);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navigationRecyclerView.setAdapter(convoNavigationAdapter);

        message = (EditText) findViewById(R.id.activity_conversation_message_field);
        sendButton = (Button) findViewById(R.id.activity_conversation_message_send_button);

        sendButton.setOnClickListener(this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //BlocTalkApplication.getSharedDataSource().query(this);

        String recipient = BlocTalkApplication.getSharedDataSource().getCurrentRecipient();
        BlocTalkApplication.getSharedDataSource().setCurrentRecipient(null);

        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, MessageItem.READ_MSG);

        Uri conversationUri = Uri.parse("content://sms//");
        getContentResolver().update(conversationUri, values, Telephony.Sms.ADDRESS + "= ?", new String[]{recipient});
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.activity_conversation_message_send_button:
                final String userMsg = message.getText().toString();
                message.setText("");

                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                formatter.setLenient(false);

                Date curDate = new Date();
                final long curMillis = curDate.getTime();
                String curTime = formatter.format(curDate);

                BlocTalkApplication.getSharedDataSource().getMsgs().add(new MessageItem(userMsg, MessageItem.READ_MSG, MessageItem.OUTGOING_MSG, "Sending..."));
                convoMsgItemAdapter.notifyDataSetChanged();

                recyclerView.smoothScrollToPosition(BlocTalkApplication.getSharedDataSource().getMsgs().size() - 1);

                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);

                //---when the SMS has been sent---
                registerReceiver(new BroadcastReceiver(){
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode())
                        {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS sent",
                                        Toast.LENGTH_SHORT).show();

                                ContentValues values = new ContentValues();

                                values.put(Telephony.Sms.ADDRESS, BlocTalkApplication.getSharedDataSource().getCurrentRecipient());
                                values.put(Telephony.Sms.TYPE, MessageItem.OUTGOING_MSG);
                                values.put(Telephony.Sms.BODY, userMsg);
                                values.put(Telephony.Sms.DATE_SENT, curMillis);
                                getContentResolver().insert(
                                        Telephony.Sms.CONTENT_URI,
                                        values);

                                BlocTalkApplication.getSharedDataSource().getMsgs().get(BlocTalkApplication.getSharedDataSource().getMsgs().size()-1).setTime(Long.toString(curMillis));
                                convoMsgItemAdapter.notifyDataSetChanged();


                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(getBaseContext(), "Generic failure",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(getBaseContext(), "No service",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(getBaseContext(), "Null PDU",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(getBaseContext(), "Radio off",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SENT));

                //---when the SMS has been delivered---
                registerReceiver(new BroadcastReceiver(){
                    @Override
                    public void onReceive(Context arg0, Intent arg1) {
                        switch (getResultCode())
                        {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "SMS delivered",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(getBaseContext(), "SMS not delivered",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(DELIVERED));

                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(BlocTalkApplication.getSharedDataSource().getCurrentRecipient(), null, userMsg, sentPI, deliveredPI);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BlocTalkApplication.getSharedInstance().setCurrentActivity(this);
    }

    protected void onStop() {
        super.onStop();
        //unregisterReceiver
    }

    public void notifyAdapter() {
        convoMsgItemAdapter.notifyDataSetChanged();
    }

    public RecyclerView getRecyclerView() { return recyclerView; }

}
