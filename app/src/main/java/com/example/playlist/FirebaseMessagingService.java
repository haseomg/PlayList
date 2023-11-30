package com.example.playlist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

//import com.google.firebase.messaging.RemoteMessage;

//import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private String CHANNEL_ID = "1668014816079";
    private String CHANNEL_NAME = "Playlist";
    String TAG = "FCM CLASS";
    String title, body;


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

        Map<String, String> data = remoteMessage.getData();
        String name = data.get("name");
        String message = data.get("message");
        String time = data.get("time");
        String me = data.get("me");
        Log.i(TAG, "onMessageReceived - data : " + data);
        Log.i(TAG, "onMessageReceived - name : " + name);
        Log.i(TAG, "onMessageReceived message : " + message);
        Log.i(TAG, "onMessageReceived time : " + time);
        Log.i(TAG, "onMessageReceived me : " + me);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        } // else

        Intent intent = new Intent(this, ChatSelect.class );
        intent.putExtra("before_class", "home");
        intent.putExtra("username", me);
        intent.putExtra("name", name); // 알림 이름 추가
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // title 이랑 body 설정
        builder.setContentTitle(name)
//                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.logo_circle)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

                Notification notification = builder.build();
        notificationManager.notify((int) System.currentTimeMillis(), notification);
    } // onMessageReceived


    // 받아오는 코드가 없어 - stella

} // CLASS END
