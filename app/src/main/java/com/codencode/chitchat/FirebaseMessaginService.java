package com.codencode.chitchat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaActionSound;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaginService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "friend request";
    private static final String CHANNEL_NAME = "friend request channel";
    private static final String CHANNEL_DESC = "friend request notification channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        String user_id = remoteMessage.getData().get("from_user_id");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID , CHANNEL_NAME , NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this , CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.ic_notification)
                                                    .setContentTitle(title)
                                                    .setContentText(message)
                                                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                                    .setPriority(NotificationCompat.PRIORITY_HIGH);


        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_uid" , user_id);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this , 0 , resultIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);


        int notificationID = (int) System.currentTimeMillis();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationID , mBuilder.build());
    }
}
