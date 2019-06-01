package com.dennisdavydov.quizapp.notification;

import android.util.Log;

import com.dennisdavydov.quizapp.data.sqlite.NotificationDbController;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    @Override
    public  void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCMSG", "onMessageReceived: " + remoteMessage);
        
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> params = remoteMessage.getData();
            sendNotification(params.get("title"),params.get("message"), params.get("url"));
            broadcastNewNotification();
        }
    }



    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.d("FCMSG", "onMessageSent: " + s);
    }

    private void sendNotification(String title, String messageBody, String url) {
        //insert data into database
        NotificationDbController notificationDbController = new NotificationDbController(MyFirebaseMessagingService.this);
        notificationDbController.insertData(title, messageBody, url);
    }

    private void broadcastNewNotification() {
    }
}
