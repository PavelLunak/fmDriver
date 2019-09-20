package com.example.fmdriver.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.AppUtils;
import com.example.fmdriver.utils.DateTimeUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService implements AppConstants {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                MainActivity.appPrefs.edit().fcmToken().put(newToken).apply();
                Log.i(TAG, "new token: " + newToken);
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "MyFirebaseMessagingService - onMessageReceived");

        if (remoteMessage != null) {
            /*
            Log.i(TAG, "MessageType: " + remoteMessage.getMessageType());
            Log.i(TAG, "CollapseKey: " + remoteMessage.getCollapseKey());
            Log.i(TAG, "From: " + remoteMessage.getFrom());
            Log.i(TAG, "MessageId: " + remoteMessage.getMessageId());
            Log.i(TAG, "To: " + remoteMessage.getTo());
            Log.i(TAG, "Priority: " + remoteMessage.getPriority());
            Log.i(TAG, "SentTime: " + DateTimeUtils.getDateTime(remoteMessage.getSentTime()));
            Log.i(TAG, "Ttl: " + remoteMessage.getTtl() / 60);
            */

            Map<String, String> data = remoteMessage.getData();

            if (data != null) {
                if (!data.isEmpty()) {
                    ArrayList<String> keys = new ArrayList<>();
                    for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                        keys.add(entry.getKey());
                        Log.i(TAG, "key : " + entry.getKey() + ", value : " + entry.getValue());
                    }

                    int responseType = 0;

                    if (keys.contains(KEY_RESPONSE_TYPE)) {
                        try {
                            responseType = Integer.parseInt(data.get(KEY_RESPONSE_TYPE));
                        } catch (NumberFormatException e) {
                            Log.i(TAG, "NumberFormatException: " + e.getMessage());
                            return;
                        }
                    } else {
                        return;
                    }

                    Log.i(TAG, "responseType: " + AppUtils.responseTypeToString(responseType));

                    Intent intent;

                    switch (responseType) {
                        case FCM_RESPONSE_SERVICE_STATUS_STARTED:
                        case FCM_RESPONSE_SERVICE_STATUS_STOPED:
                            intent = new Intent(ACTION_SERVICE_STATUS_BROADCAST);
                            intent.putExtra(KEY_RESPONSE_SERVICE_STATUS, data.get("responseType"));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_RESPONSE_TYPE_LOCATION:
                            showNotification(DateTimeUtils.getDateTime(new Date()));
                            intent = new Intent(ACTION_LOCATION_BROADCAST);
                            intent.putExtra(KEY_DATA, remoteMessage);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_REQUEST_TYPE_SERVICE_START:
                            intent = new Intent(ACTION_SERVICE_STARTET_BROADCAST);
                            intent.putExtra(KEY_MESSAGE, data.get(KEY_MESSAGE));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_REQUEST_TYPE_SERVICE_STOP:
                            intent = new Intent(ACTION_SERVICE_STOPED_BROADCAST);
                            intent.putExtra(KEY_MESSAGE, data.get(KEY_MESSAGE));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                    }
                } else {
                    Log.i(TAG, "remoteMessage.getData().isEmpty()");
                }
            } else {
                Log.i(TAG, "remoteMessage.getData() == null");
            }
        } else {
            Log.i(TAG, "remoteMessage == null");
        }
    }

    private void showNotification(String message) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_location_black_24dp)
                .setSound(null)
                .setContentTitle("fm")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(9372, builder.build());
    }
}
