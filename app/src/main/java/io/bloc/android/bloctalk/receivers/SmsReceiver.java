package io.bloc.android.bloctalk.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
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

        if(action.equals(ACTION_SMS_DELIVER)){
            SmsMessage[] messages = getMessagesFromIntent(intent);
            if (messages != null) {
                for(SmsMessage message : messages){
                    sender = message.getDisplayOriginatingAddress();
                    msg = message.getDisplayMessageBody();
                    time = message.getTimestampMillis();

                    Uri conversationUri = Uri.parse("content://sms//");
                    Cursor cursor = context.getContentResolver().query(conversationUri, null, Telephony.Sms.ADDRESS + "= ?", new String[]{sender}, null);

                    if(cursor.getCount() > 0){
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

                if(BlocTalkApplication.getSharedDataSource().getCurrentRecipient() != null && BlocTalkApplication.getSharedDataSource().getCurrentRecipient().equals(sender)) {
                    BlocTalkApplication.getSharedDataSource().getMsgs().add(new MessageItem(msg, MessageItem.READ_MSG, MessageItem.INCOMING_MSG, Long.toString(time)));
                    ConversationActivity ca = (ConversationActivity) BlocTalkApplication.getSharedInstance().getCurrentActivity();
                    ca.notifyAdapter();

                    ca.getRecyclerView().smoothScrollToPosition(BlocTalkApplication.getSharedDataSource().getMsgs().size() - 1);
                }else{
                    BlocTalkApplication.getSharedDataSource().query(context);

                    MainActivity ma = (MainActivity)BlocTalkApplication.getSharedInstance().getCurrentActivity();
                    ma.notifyAdapter();

                    BlocTalkApplication.getSharedDataSource().query(context);
                    Notification.Builder noti = new Notification.Builder(context)
                            .setContentTitle("Message from: " + sender)
                            .setContentText(msg + "\n\n" + time)
                            .setSmallIcon(R.mipmap.conversation_item_user_notification)
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, noti.build());
                }
            }
        }
    }
}
