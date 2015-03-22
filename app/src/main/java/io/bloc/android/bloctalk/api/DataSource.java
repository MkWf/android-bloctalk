package io.bloc.android.bloctalk.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.api.model.ConversationItem;
import io.bloc.android.bloctalk.api.model.MessageItem;

import static android.database.DatabaseUtils.dumpCursor;

/**
 * Created by Mark on 3/8/2015.
 */
public class DataSource {

    List<ConversationItem> conversations;
    List<MessageItem> messages;
    String currentRecipient;

    public DataSource() {
        conversations = new ArrayList<>();
        messages = new ArrayList<>();
        //createFakeData();
    }

    public List<ConversationItem> getConvos(){
        return conversations;
    }
    public List<MessageItem> getMsgs(){
        return messages;
    }
    public String getCurrentRecipient() { return currentRecipient; }

    public void setCurrentRecipient(String recipient) {
        this.currentRecipient = recipient;
    }

    public void query(Context context){

        if(conversations.size() > 0){
            conversations.clear();
        }

        Uri allConversations = Uri.parse("content://mms-sms/conversations/?simple=true");

        final String[] projection = new String[]{"*"};
        Cursor cursor = context.getContentResolver().query(
                allConversations,
                        projection, null, null, null);

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            Log.i("Split", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$CONVERSATIONS");
            dumpCursor(cursor);

            int id = -1;
            String name = "";
            int unreadMsgCount=0;
            String photo;
            Uri photoURI = null;
            String emailOrPhone = "";

            for(int i = 0; i<cursor.getCount(); i++, cursor.moveToNext()){
                id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                unreadMsgCount = cursor.getInt(cursor.getColumnIndexOrThrow("unread_count"));
                String recipientIDs = cursor.getString(cursor.getColumnIndexOrThrow("recipient_ids"));

                for (String recipientID : recipientIDs.split(" ")) {
                    Cursor address = context.getContentResolver().query(
                            Uri.parse("content://mms-sms/canonical-addresses"),
                            null, "_id = " + recipientID, null, null);
                    if(address.getCount() > 0){
                        address.moveToFirst();
                        Log.i("Split", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ADDRESSES");
                        dumpCursor(address);

                        emailOrPhone = address.getString(address.getColumnIndexOrThrow("address"));

                        Log.i("Split", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$CONTACTINFO");
                        if(emailOrPhone.contains("@")){
                            Uri lookupByEmail = Uri.withAppendedPath(
                                    ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI,
                                    Uri.encode(emailOrPhone));
                            Cursor contactInfo = context.getContentResolver().query(lookupByEmail, null, null, null, null);
                            dumpCursor(contactInfo);
                        }else{
                            Uri lookupByPhone = Uri.withAppendedPath(
                                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                                    Uri.encode(emailOrPhone));
                            Cursor contactInfo = context.getContentResolver().query(lookupByPhone, null, null, null, null);
                            dumpCursor(contactInfo);

                            if(contactInfo.getCount() > 0){
                                contactInfo.moveToFirst();
                                name = contactInfo.getString(contactInfo.getColumnIndexOrThrow("display_name"));
                                photo = contactInfo.getString(contactInfo.getColumnIndexOrThrow("photo_thumb_uri"));

                                if(photo != null){
                                    photoURI = Uri.parse(photo);
                                }
                                else{
                                    photoURI = null;
                                }
                            }else{
                                name = emailOrPhone;
                            }
                        }
                    }
                }
                conversations.add(new ConversationItem(id, name, photoURI, emailOrPhone, unreadMsgCount));
            }
        }
    }

    public void queryForMessages(Context context, int id){
        if(messages.size() > 0){
            messages.clear();
        }

        String selection = "thread_id = "+id;
        Uri conversationUri = Uri.parse("content://sms//");
        Cursor cursor = context.getContentResolver().query(conversationUri, null, selection, null, "_id ASC");

        if(cursor.getCount() > 0){
            dumpCursor(cursor);

            cursor.moveToFirst();

            for(int i = 0; i<cursor.getCount(); i++, cursor.moveToNext()){
                //String msgId = cursor.getString(cursor.getColumnIndexOrThrow("thread_id"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                int read = cursor.getInt(cursor.getColumnIndexOrThrow("read"));
                int sender = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("date_sent"));
                messages.add(new MessageItem(body, read, sender, time));

                BlocTalkApplication.getSharedDataSource().setCurrentRecipient(cursor.getString(cursor.getColumnIndexOrThrow("address")));

            }
        }
    }

    public void queryForMessages(Context context, int id, int offset, int limit){
        if(messages.size() > 0){
            messages.clear();
        }

        String selection = "thread_id = "+id;
        Uri conversationUri = Uri.parse("content://sms//");
        Cursor cursor = context.getContentResolver().query(conversationUri, null, selection, null, "_id ASC limit " + limit + " offset " + offset);

        if(cursor.getCount() > 0){
            dumpCursor(cursor);

            cursor.moveToFirst();

            for(int i = 0; i<cursor.getCount(); i++, cursor.moveToNext()){
                //String msgId = cursor.getString(cursor.getColumnIndexOrThrow("thread_id"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                int read = cursor.getInt(cursor.getColumnIndexOrThrow("read"));
                int sender = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("date_sent"));
                messages.add(new MessageItem(body, read, sender, time));

                BlocTalkApplication.getSharedDataSource().setCurrentRecipient(cursor.getString(cursor.getColumnIndexOrThrow("address")));

            }
        }
    }

    public void queryForMoreMessages(Context context, int id, int offset, int limit){

        String selection = "thread_id = "+id;
        Uri conversationUri = Uri.parse("content://sms//");
        Cursor cursor = context.getContentResolver().query(conversationUri, null, selection, null, "_id ASC limit " + limit + " offset " + offset);

        if(cursor.getCount() > 0){
            dumpCursor(cursor);

            cursor.moveToFirst();

            for(int i = 0; i<cursor.getCount(); i++, cursor.moveToNext()){
                //String msgId = cursor.getString(cursor.getColumnIndexOrThrow("thread_id"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                int read = cursor.getInt(cursor.getColumnIndexOrThrow("read"));
                int sender = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("date_sent"));
                messages.add(new MessageItem(body, read, sender, time));
            }
        }
    }

    public void updateMsgReadStatus(String msgId){
        Context context = BlocTalkApplication.getSharedInstance();

        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.READ, MessageItem.READ_MSG);

        Uri conversationUri = Uri.parse("content://sms//");
        context.getContentResolver().update(conversationUri, values, Telephony.Sms._ID + "= ?", new String[]{msgId});
    }

}
