package io.bloc.android.bloctalk.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Mark on 3/13/2015.
 */
public class HeadlessSmsSendService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
