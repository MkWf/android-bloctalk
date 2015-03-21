package io.bloc.android.bloctalk.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.api.model.ConversationItem;
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
                for(SmsMessage message : messages) {
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

                        if(BlocTalkApplication.getSharedDataSource().getCurrentRecipient() != null && BlocTalkApplication.getSharedDataSource().getCurrentRecipient().equals(sender)) {
                            BlocTalkApplication.getSharedDataSource().getMsgs().add(new MessageItem(msg, MessageItem.READ_MSG, MessageItem.INCOMING_MSG, Long.toString(time)));
                            ConversationActivity ca = (ConversationActivity) BlocTalkApplication.getSharedInstance().getCurrentActivity();
                            ca.notifyAdapter();

                            ca.getRecyclerView().smoothScrollToPosition(BlocTalkApplication.getSharedDataSource().getMsgs().size() - 1);
                        }else {
                            BlocTalkApplication.getSharedDataSource().query(context);

                            MainActivity ma = (MainActivity) BlocTalkApplication.getSharedInstance().getCurrentActivity();

                            if (ma != null) {
                                ma.notifyAdapter();
                            }

                            int userIdforIntent = -1;

                            List<ConversationItem> convos = BlocTalkApplication.getSharedDataSource().getConvos();
                            for(int i = 0; i < convos.size(); i++){
                                if(convos.get(i).getAddress().equals(sender)){
                                    userIdforIntent = convos.get(i).getId();
                                }
                            }

                            Intent resultIntent = new Intent(context, ConversationActivity.class);
                            resultIntent.putExtra("notifId", userIdforIntent);

                            PendingIntent resultPendingIntent =
                                    PendingIntent.getActivity(
                                            context,
                                            0,
                                            resultIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );

                            SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy, HH:mm");
                            formatter.setLenient(false);

                            String timeDayHr = formatter.format(time);


                            Uri lookupByPhone = Uri.withAppendedPath(
                                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                                    Uri.encode(sender));
                            Cursor contactInfo = context.getContentResolver().query(lookupByPhone, null, null, null, null);

                            if(contactInfo.getCount() > 0){
                                contactInfo.moveToFirst();
                                sender = contactInfo.getColumnName(contactInfo.getColumnIndexOrThrow("display_name"));
                            }

                            Notification.Builder noti = new Notification.Builder(context)
                                    .setContentTitle("Message from: " + sender)
                                    .setContentText(msg + "\n\n" + timeDayHr)
                                    .setContentIntent(resultPendingIntent)
                                    .setSmallIcon(R.mipmap.conversation_item_user_notification);
                            // .setStyle(new Notification.BigTextStyle().bigText(msg))
                            // .setAutoCancel(false);

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                            notificationManager.notify(userIdforIntent, noti.build());
                        }
                    }else{   ///////////////THIS IS A NEW CONVERSATION. CHECK IF THE CONTACT EXISTS BY SEARCHING FOR IT USING THE MESSAGE'S ADDRESS IN THE CANONICAL ADDRESS PROVIDER
                        Uri lookupByPhone = Uri.withAppendedPath(
                                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                                Uri.encode(sender));
                        Cursor contactInfo = context.getContentResolver().query(lookupByPhone, null, null, null, null);

                        String name;
                        String id;

                        if(contactInfo.getCount() > 0){
                            contactInfo.moveToFirst();
                            name = contactInfo.getString(contactInfo.getColumnIndexOrThrow("display_name"));

                            ContentValues values = new ContentValues();

                            values.put(Telephony.Sms.ADDRESS, sender);
                            values.put(Telephony.Sms.TYPE, MessageItem.INCOMING_MSG);
                            values.put(Telephony.Sms.BODY, msg);
                            values.put(Telephony.Sms.DATE_SENT, Long.toString(time));
                            values.put(Telephony.Sms.READ, Integer.toString((MessageItem.UNREAD_MSG)));

                            context.getContentResolver().insert(
                                    Telephony.Sms.CONTENT_URI,
                                    values);

                            BlocTalkApplication.getSharedDataSource().query(context);

                            MainActivity ma = (MainActivity) BlocTalkApplication.getSharedInstance().getCurrentActivity();

                            if (ma != null) {
                                ma.notifyAdapter();
                            }

                            int userIdforIntent = -1;

                            List<ConversationItem> convos = BlocTalkApplication.getSharedDataSource().getConvos();
                            for(int i = 0; i < convos.size(); i++){
                                if(convos.get(i).getAddress().equals(sender)){
                                    userIdforIntent = convos.get(i).getId();
                                }
                            }

                            Intent resultIntent = new Intent(context, ConversationActivity.class);
                            resultIntent.putExtra("notifId", userIdforIntent);

                            PendingIntent resultPendingIntent =
                                    PendingIntent.getActivity(
                                            context,
                                            0,
                                            resultIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );

                            SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy, HH:mm");
                            formatter.setLenient(false);

                            String timeDayHr = formatter.format(time);

                            Notification.Builder noti = new Notification.Builder(context)
                                    .setContentTitle("Message from: " + name)
                                    .setContentText(msg + "\n\n" + timeDayHr)
                                    .setContentIntent(resultPendingIntent)
                                    .setSmallIcon(R.mipmap.conversation_item_user_notification);
                            // .setStyle(new Notification.BigTextStyle().bigText(msg))
                            // .setAutoCancel(false);

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                            notificationManager.notify(userIdforIntent, noti.build());
                        }else{
                            ContentValues values = new ContentValues();

                            values.put(Telephony.Sms.ADDRESS, sender);
                            values.put(Telephony.Sms.TYPE, MessageItem.INCOMING_MSG);
                            values.put(Telephony.Sms.BODY, msg);
                            values.put(Telephony.Sms.DATE_SENT, Long.toString(time));
                            values.put(Telephony.Sms.READ, Integer.toString((MessageItem.UNREAD_MSG)));

                            context.getContentResolver().insert(
                                    Telephony.Sms.CONTENT_URI,
                                    values);

                            BlocTalkApplication.getSharedDataSource().query(context);

                            MainActivity ma = (MainActivity) BlocTalkApplication.getSharedInstance().getCurrentActivity();

                            if (ma != null) {
                                ma.notifyAdapter();
                            }

                            int userIdforIntent = -1;

                            List<ConversationItem> convos = BlocTalkApplication.getSharedDataSource().getConvos();
                            for(int i = 0; i < convos.size(); i++){
                                if(convos.get(i).getAddress().equals(sender)){
                                    userIdforIntent = convos.get(i).getId();
                                }
                            }

                            Intent resultIntent = new Intent(context, ConversationActivity.class);
                            resultIntent.putExtra("notifId", userIdforIntent);

                            PendingIntent resultPendingIntent =
                                    PendingIntent.getActivity(
                                            context,
                                            0,
                                            resultIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );

                            SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy, HH:mm");
                            formatter.setLenient(false);

                            String timeDayHr = formatter.format(time);

                            Notification.Builder noti = new Notification.Builder(context)
                                    .setContentTitle("Message from: " + sender)
                                    .setContentText(msg + "\n\n" + timeDayHr)
                                    .setContentIntent(resultPendingIntent)
                                    .setSmallIcon(R.mipmap.conversation_item_user_notification);
                            // .setStyle(new Notification.BigTextStyle().bigText(msg))
                            // .setAutoCancel(false);

                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                            notificationManager.notify(userIdforIntent, noti.build());

                            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                            int rawContactInsertIndex = ops.size();

                            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,null )
                                    .build());
                            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, sender)
                                    .build());
                            try{
                                ContentProviderResult[] res = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                            }catch(RemoteException re){

                            }catch(OperationApplicationException oae){

                            }
                        }
                    }
                }
            }
        }
    }
}
