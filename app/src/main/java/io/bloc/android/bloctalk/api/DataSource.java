package io.bloc.android.bloctalk.api;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.List;

import io.bloc.android.bloctalk.api.model.ConversationItem;

import static android.database.DatabaseUtils.dumpCursor;

/**
 * Created by Mark on 3/8/2015.
 */
public class DataSource {

    List<ConversationItem> convos;

    public DataSource() {
        convos = new ArrayList<>();
        createFakeData();
    }

    public List<ConversationItem> getConvos(){
        return convos;
    }

    public void createFakeData(){
        for(int i = 0; i < 6; i++){
            convos.add(new ConversationItem("Mark W."));
        }
    }

    public void query(Context context){
        Cursor convos = context.getContentResolver().query(
                Telephony.MmsSms.CONTENT_CONVERSATIONS_URI,
                        null, null, null, null);
        if(convos.getCount() > 0){
            convos.moveToFirst();
            dumpCursor(convos);
        }
       // convos.close();
        /*String recipientIDs = allConversations.getString(
                allConversations.getColumnIndex(
                        Telephony.ThreadsColumns.RECIPIENT_IDS));
        for (String recipientID : recipientIDs.split(" ")) {
            Cursor address = context.getContentResolver().query(
                    Uri.parse("content://mms-sms/canonical-addresses"),
                    null, "_id = " + recipientID, null, null);
            // Recover address data
        }
        /*Uri lookupByEmail = Uri.withAppendedPath(
                ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI,
                Uri.encode(recipientEmail));

        Cursor contactInfo = context.getContentResolver().query(lookupByEmail, null, null, null, null);*/
    }


}
