package com.example.playlist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

//import com.google.firebase.messaging.RemoteMessage;

//import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private String CHANNEL_ID = "1668014816079";
    private String CHANNEL_NAME = "Playlist";
    String TAG = "FCM CLASS";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // token을 서버로 전송
        // 클라우드 서버에 등록될 시 호출, token이 앱을 구분하기 위한 고유 키가 됨.
        Log.i(TAG, "onNewToken");
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // 수신한 메시지를 처리
        // 클라우드 서버에서 메시지 전송시 자동호출, 메시지 처리해 알림 보낼 수 있음.
        Log.i(TAG, "onMessageReceived");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        Log.i(TAG, "notificationManager : " + notificationManager);

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "if) Build.VERSION.SDK_INT >= Build.VERSION_CODES.O");
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                Log.i(TAG, "if) notificationManager.getNotificationChannel(CHANNEL_ID) : " + notificationManager.getNotificationChannel(CHANNEL_ID));
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                Log.i(TAG, "NotiChannel : " + channel);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            Log.i(TAG,"else)");
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        Log.i(TAG, "FCM title check : " + title);
        Log.i(TAG, "FCM body check : " + body);

        builder.setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_background);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    } // onMessageReceived

    // 받아오는 코드가 없어 - stella

} // CLASS END
