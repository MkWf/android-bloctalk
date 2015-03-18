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
import io.bloc.android.bloctalk.ui.activities.MainActivity;

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
        long time=0;


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
                        values.put(Telephony.Sms.DATE_SENT, Long.toString(time));

                        if(BlocTalkApplication.getSharedDataSource().getCurrentRecipient() != null && BlocTalkApplication.getSharedDataSource().getCurrentRecipient().equals(sender)){
                            values.put(Telephony.Sms.READ, Integer.toString(MessageItem.READ_MSG));
                        }else{
                            values.put(Telephony.Sms.READ, Integer.toString((MessageItem.UNREAD_MSG)));
                        }

                        context.getContentResolver().insert(
                                Telephony.Sms.CONTENT_URI,
                                values);
                    }
                }

                if(BlocTalkApplication.getSharedDataSource().getCurrentRecipient() != null && BlocTalkApplication.getSharedDataSource().getCurrentRecipient().equals(sender)){
                    BlocTalkApplication.getSharedDataSource().getMsgs().add(new MessageItem(msg, MessageItem.READ_MSG, MessageItem.INCOMING_MSG, Long.toString(time)));
                    ConversationActivity ca = (ConversationActivity)BlocTalkApplication.getSharedInstance().getCurrentActivity();
                    ca.notifyAdapter();
                }else{
                    BlocTalkApplication.getSharedDataSource().getMsgs().add(new MessageItem(msg, MessageItem.UNREAD_MSG, MessageItem.INCOMING_MSG, Long.toString(time)));

                    BlocTalkApplication.getSharedDataSource().query(context);

                    MainActivity ma = (MainActivity)BlocTalkApplication.getSharedInstance().getCurrentActivity();
                    ma.notifyAdapter();
                }

                /*Notification.Builder noti = new Notification.Builder(context)
                        .setContentTitle("New Message from")
                        .setContentText(sender + "\n" + msg + "\n" + Boolean.toString(isEmail) + "\n" + data.toString() + "\n" + Long.toString(time)
                                + "\n" + Integer.toString(icc) +  "\n" + Integer.toString(icc2))
                        .setSmallIcon(R.mipmap.conversation_item_user);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify(Integer.parseInt(sender), noti.build());*/
            }
        }
    }
}
