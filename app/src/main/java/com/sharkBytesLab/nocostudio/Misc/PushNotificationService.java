package com.sharkBytesLab.nocostudio.Misc;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sharkBytesLab.nocostudio.R;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class PushNotificationService extends FirebaseMessagingService
{
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        String title = Objects.requireNonNull(message.getNotification()).getTitle();
        String text = message.getNotification().getBody();
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Heads Up Notification", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title).setContentText(text).setSmallIcon(R.drawable.ic_music).setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notification.build());

        super.onMessageReceived(message);
    }
}
