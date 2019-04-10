package com.bcabuddies.fitsteps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class notificationReceiver extends BroadcastReceiver {
    private static final String TAG ="notifReceiver test" ;

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("com.bcabuddies.fitsteps.CANCEL_ACTION".equals(intent.getAction())){
            Log.e(TAG, "onReceive: cancel" );
            NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);
            notificationManagerCompat.cancel(1);
            System.exit(0);
        }
           }
}
