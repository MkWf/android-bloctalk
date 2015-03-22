package io.bloc.android.bloctalk.ui.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private MultiAutoCompleteTextView textView;
    String userMsg;
    private BroadcastReceiver sentReceiver;
    private BroadcastReceiver deliverReceiver;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    List<String> recipients = new ArrayList<>();
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, lastVisibleItem, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    int id;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        sentReceiver = new sentReceiver();
        deliverReceiver = new deliverReceiver();

        registerReceiver(sentReceiver,new IntentFilter(SENT));
        registerReceiver(deliverReceiver, new IntentFilter(DELIVERED));

        BlocTalkApplication.getSharedInstance().setCurrentActivity(this);

        Intent intent = getIntent();
        int notifId = intent.getIntExtra("notifId", -1);
        id = intent.getIntExtra("id", -1);
        String name = intent.getStringExtra("name");

        if(notifId != -1){
            BlocTalkApplication.getSharedDataSource().queryForMessages(this, notifId);
            recyclerView.scrollToPosition(BlocTalkApplication.getSharedDataSource().getMsgs().size() - 1);
        }else{
            BlocTalkApplication.getSharedDataSource().queryForMessages(this, id, 0, 10);
        }

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
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setVerticalScrollBarEnabled(true);
       // recyclerView.setScrollbarFadingEnabled(false);
        //recyclerView.setScrollBarSize(5);
        recyclerView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        recyclerView.setAdapter(convoMsgItemAdapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition() + 1;

                if(totalItemCount == lastVisibleItem){
                    Toast.makeText(getBaseContext(), "Loading more messages",
                            Toast.LENGTH_SHORT).show();

                    BlocTalkApplication.getSharedDataSource().queryForMoreMessages(ConversationActivity.this, id, BlocTalkApplication.getSharedDataSource().getMsgs().size(), 5);
                    convoMsgItemAdapter.notifyDataSetChanged();

                }
            }
        });

        convoNavigationAdapter = new ConversationNavigationAdapter();
        navigationRecyclerView = (RecyclerView) findViewById(R.id.rv_nav_activity_conversation);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        navigationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navigationRecyclerView.setAdapter(convoNavigationAdapter);

        message = (EditText) findViewById(R.id.activity_conversation_message_field);
        sendButton = (Button) findViewById(R.id.activity_conversation_message_send_button);

        sendButton.setOnClickListener(this);

        Cursor peopleCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PEOPLE_PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        ContactListAdapter contactadapter = new ContactListAdapter(this,
                peopleCursor);

        textView = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
        textView.setAdapter(contactadapter);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        if(intent.getIntExtra("newConvo", -1) == 1){
            textView.setVisibility(View.VISIBLE);
            textView.requestFocus(1);
        }
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
                userMsg = message.getText().toString();
                message.setText("");

                PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

                if((textView.getText().toString().equals(""))) {
                    BlocTalkApplication.getSharedDataSource().getMsgs().add(new MessageItem(userMsg, MessageItem.READ_MSG, MessageItem.OUTGOING_MSG, "Sending..."));
                    convoMsgItemAdapter.notifyDataSetChanged();

                    recyclerView.smoothScrollToPosition(BlocTalkApplication.getSharedDataSource().getMsgs().size() - 1);

                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(BlocTalkApplication.getSharedDataSource().getCurrentRecipient(), null, userMsg, sentPI, deliveredPI);
                }else{
                    Editable k = textView.getText();
                    String f = k.toString();

                    int start = f.indexOf("<");
                    int end = f.indexOf(">");

                    while(start != -1){
                        recipients.add(f.substring(start+1, end));
                        start = f.indexOf("<", end+1);
                        end = f.indexOf(">", end+1);
                    }

                    SmsManager sms = SmsManager.getDefault();

                    for(int z = 0; z<recipients.size(); z++){
                        sms.sendTextMessage(recipients.get(z), null, userMsg, sentPI, deliveredPI);
                    }
                }
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

    public static class ContactListAdapter extends CursorAdapter implements Filterable {
        public ContactListAdapter(Context context, Cursor c) {
            super(context, c);
            mContent = context.getContentResolver();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final TextView view = (TextView) inflater.inflate(
                    android.R.layout.simple_dropdown_item_1line, parent, false);
            view.setText(cursor.getString(5)+"<"+cursor.getString(3)+">");
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((TextView) view).setText(cursor.getString(5)+"<"+cursor.getString(3)+">");
        }

        @Override
        public String convertToString(Cursor cursor) {
            return cursor.getString(5)+"<"+cursor.getString(3)+">";
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                buffer = new StringBuilder();
                buffer.append("UPPER(");
                buffer.append(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                buffer.append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*" };
            }

            return mContent.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PEOPLE_PROJECTION,
                    buffer == null ? null : buffer.toString(), args,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        }

        private ContentResolver mContent;
    }

    private static final String[] PEOPLE_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.TYPE,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.LABEL,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
    };

    class deliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
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
    }

    class sentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "SMS sent",
                            Toast.LENGTH_SHORT).show();

                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                    formatter.setLenient(false);

                    Date curDate = new Date();
                    final long curMillis = curDate.getTime();
                    String curTime = formatter.format(curDate);

                    ContentValues values = new ContentValues();

                    if(recipients.size() > 0){
                        for(int i = 0; i<recipients.size(); i++){
                            values.put(Telephony.Sms.ADDRESS, recipients.get(i));
                            values.put(Telephony.Sms.TYPE, MessageItem.OUTGOING_MSG);
                            values.put(Telephony.Sms.BODY, userMsg);
                            values.put(Telephony.Sms.DATE_SENT, curMillis);
                            getContentResolver().insert(
                                    Telephony.Sms.CONTENT_URI,
                                    values);
                        }
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        values.put(Telephony.Sms.ADDRESS, BlocTalkApplication.getSharedDataSource().getCurrentRecipient());
                        values.put(Telephony.Sms.TYPE, MessageItem.OUTGOING_MSG);
                        values.put(Telephony.Sms.BODY, userMsg);
                        values.put(Telephony.Sms.DATE_SENT, curMillis);
                        getContentResolver().insert(
                                Telephony.Sms.CONTENT_URI,
                                values);

                        BlocTalkApplication.getSharedDataSource().getMsgs().get(BlocTalkApplication.getSharedDataSource().getMsgs().size()-1).setTime(Long.toString(curMillis));
                        convoMsgItemAdapter.notifyDataSetChanged();
                    }



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
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(sentReceiver);
        unregisterReceiver(deliverReceiver);
    }
}

