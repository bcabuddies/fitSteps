package com.bcabuddies.fitsteps;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "notifchannel";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "run activity",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription("steps counter");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
    }
}
