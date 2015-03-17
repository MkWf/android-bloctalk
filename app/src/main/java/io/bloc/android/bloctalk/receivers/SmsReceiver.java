package io.bloc.android.bloctalk.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.api.model.MessageItem;
import io.bloc.android.bloctalk.ui.activities.ConversationActivity;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Created by Mark on 3/13/2015.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String ACTION_SMS_DELIVER = "android.provider.Telephony.SMS_DELIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        String sender="";
        String msg="";
        long time;


        if(action.equals(ACTION_SMS_RECEIVED) || action.equals(ACTION_SMS_DELIVER)){
            SmsMessage[] messages = getMessagesFromIntent(intent);
            if (messages != null) {
                for(SmsMessage message : messages){
                    sender = message.getDisplayOriginatingAddress();
                    msg = message.getDisplayMessageBody();
                    boolean isEmail = message.isEmail();
                    byte[] data = message.getUserData();
                    time = message.getTimestampMillis();
                    int icc = message.getIndexOnIcc();
                    int icc2 = message.getStatusOnIcc();

                    Uri conversationUri = Uri.parse("content://sms//");
                    Cursor cursor = context.getContentResolver().query(conversationUri, null, Telephony.Sms.ADDRESS + "= ?", new String[]{sender}, null);

                    if(cursor.getCount() > 0){
                        //cursor.moveToFirst();
                        //String a = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                        ContentValues values = new ContentValues();

                        values.put(Telephony.Sms.ADDRESS, sender);
                        values.put(Telephony.Sms.TYPE, MessageItem.INCOMING_MSG);
                        values.put(Telephony.Sms.BODY, msg);
                        values.put(Telephony.Sms.DATE_SENT, time);
                        context.getContentResolver().insert(
                                Telephony.Sms.CONTENT_URI,
                                values);
                    }
                }

                if(BlocTalkApplication.getSharedDataSource().getCurrentRecipient().equals(sender)){
                    BlocTalkApplication.getSharedDataSource().getMsgs().add(new MessageItem(msg, 0, MessageItem.INCOMING_MSG));
                    ConversationActivity ca = (ConversationActivity)BlocTalkApplication.getSharedInstance().getCurrentActivity();
                    ca.notifyAdapter();
                }

            }
        }
    }
}
