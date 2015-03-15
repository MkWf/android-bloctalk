package io.bloc.android.bloctalk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Created by Mark on 3/13/2015.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(ACTION_SMS_RECEIVED)){
            SmsMessage[] messages = getMessagesFromIntent(intent);
            if (messages != null) {
                for(SmsMessage message : messages){
                    String sender = message.getDisplayOriginatingAddress();
                    String msg = message.getDisplayMessageBody();
                    boolean isEmail = message.isEmail();
                    byte[] data = message.getUserData();
                    long time = message.getTimestampMillis();
                    int icc = message.getIndexOnIcc();
                    int icc2 = message.getStatusOnIcc();

                    /*Notification.Builder noti = new Notification.Builder(context)
                            .setContentTitle("Message")
                            .setContentText(sender + "\n" + msg + "\n" + Boolean.toString(isEmail) + "\n" + data.toString() + "\n" + Long.toString(time)
                                                + "\n" + Integer.toString(icc) +  "\n" + Integer.toString(icc2))
                            .setSmallIcon(R.mipmap.conversation_item_user);

                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, noti.build());*/


                }
            }
        }
    }
}
