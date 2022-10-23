package com.sharkBytesLab.nocostudio.Misc;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.gson.JsonObject;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class MyApplication extends Application
{
    private static final String ONESIGNAL_APP_ID = "021eebb5-b392-4248-8ba8-0ec1718509ef";

    public static final String CHANNEL_ID_1 = "channel1";
    public static final String CHANNEL_ID_2 = "channel2";
    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_PLAY = "actionplay";

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        createNotificationChannel();

    }

    private void createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Channel(1)", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc..");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_ID_2, "Channel(2)", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription("Channel 2 Desc..");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);

        }
    }



}
