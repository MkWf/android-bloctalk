package io.bloc.android.bloctalk.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.TelephonyManager;

/**
 * Created by Mark on 3/13/2015.
 */
public class HeadlessSmsSendService extends Service{
    @Override
    public IBinder onBind(Intent intent) {

        if(intent != null){
            if(TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(intent.getAction())){

            }
        }

        Uri recipient = intent.getData();
        CharSequence message = intent.getCharSequenceExtra("EXTRA_TEXT");
        String subject = intent.getStringExtra("EXTRA_SUBJECT");
        return null;
    }

}
