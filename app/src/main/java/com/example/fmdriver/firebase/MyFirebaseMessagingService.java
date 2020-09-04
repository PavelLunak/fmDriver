package com.example.fmdriver.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.objects.DeviceIdentification;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.AppUtils;
import com.example.fmdriver.utils.PrefsUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
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

                DeviceIdentification di = AppUtils.getDeviceIdentification(MyFirebaseMessagingService.this);
                Log.i(TAG_DB, "MyFirebaseMessagingService - onNewToken() - Android ID: " + di.getAndroidId());
                Log.i(TAG_DB, "MyFirebaseMessagingService - onNewToken() - Device ID: " + di.getDeviceId());

                PrefsUtils.updateAndroidId(MyFirebaseMessagingService.this, di.getAndroidId());
                PrefsUtils.updateDeviceId(MyFirebaseMessagingService.this, di.getDeviceId());
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "MyFirebaseMessagingService - onMessageReceived");

        if (remoteMessage != null) {
            Map<String, String> data = remoteMessage.getData();

            if (data != null) {
                if (!data.isEmpty()) {
                    ArrayList<String> keys = new ArrayList<>();
                    for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                        keys.add(entry.getKey());
                        Log.i(TAG, "key : " + entry.getKey() + ", value : " + entry.getValue());
                    }

                    int responseType = 0;
                    int databaseIdOfMessageSender = -1;
                    String androidIdOfMessageSender = data.get(KEY_SENDER_ANDROID_ID);

                    if (keys.contains(KEY_RESPONSE_TYPE)) {
                        try {
                            responseType = Integer.parseInt(data.get(KEY_RESPONSE_TYPE));
                            databaseIdOfMessageSender = Integer.parseInt(data.get(KEY_SENDER_DATABASE_ID));
                        } catch (NumberFormatException e) {
                            Log.i(TAG, "NumberFormatException: " + e.getMessage());
                            return;
                        }
                    } else {
                        return;
                    }

                    Log.i(TAG, "responseType: " + AppUtils.responseTypeToString(responseType));
                    Log.i(TAG, "sender androidId: " + androidIdOfMessageSender);

                    Intent intent;

                    switch (responseType) {
                        case FCM_RESPONSE_SERVICE_STATUS:
                            intent = new Intent(ACTION_SERVICE_STATUS_BROADCAST);
                            intent.putExtra(KEY_BATTERY_PERCENTAGES, data.get(KEY_BATTERY_PERCENTAGES));
                            intent.putExtra(KEY_BATTERY_PLUGGED, data.get(KEY_BATTERY_PLUGGED));
                            intent.putExtra(KEY_MESSAGE, data.get(KEY_MESSAGE));
                            intent.putExtra(KEY_SERVICE_STATUS, data.get(KEY_SERVICE_STATUS));
                            intent.putExtra(KEY_GPS_STATUS, data.get(KEY_GPS_STATUS));
                            intent.putExtra(KEY_SENDER_DATABASE_ID, databaseIdOfMessageSender);
                            intent.putExtra(KEY_SENDER_ANDROID_ID, androidIdOfMessageSender);
                            intent.putExtra(KEY_SENDER_FCM_TOKEN, data.get(KEY_SENDER_FCM_TOKEN));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_RESPONSE_GPS_START:
                            break;
                        case FCM_RESPONSE_GPS_STOP:
                            break;
                        case FCM_RESPONSE_TYPE_LOCATION:
                            intent = new Intent(ACTION_LOCATION_BROADCAST);
                            intent.putExtra(KEY_DATA, remoteMessage);
                            intent.putExtra(KEY_BATTERY_PERCENTAGES, data.get(KEY_BATTERY_PERCENTAGES));
                            intent.putExtra(KEY_BATTERY_PLUGGED, data.get(KEY_BATTERY_PLUGGED));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVED:
                            intent = new Intent(ACTION_DATABASE_SETTINGS_UPDATED);
                            intent.putExtra(KEY_SAVE_NEW_DB_SETTINGS, 1);
                            intent.putExtra(KEY_MESSAGE, data.get(KEY_MESSAGE));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVE_ERROR:
                            intent = new Intent(ACTION_DATABASE_SETTINGS_UPDATED);
                            intent.putExtra(KEY_SAVE_NEW_DB_SETTINGS, 0);
                            intent.putExtra(KEY_MESSAGE, data.get(KEY_MESSAGE));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_RESPONSE_TYPE_SETTINGS_LOADED:
                            intent = new Intent(ACTION_DATABASE_SETTINGS_LOADED);
                            intent.putExtra(KEY_MESSAGE, data.get(KEY_MESSAGE));
                            intent.putExtra(KEY_DB_ENABLED, data.get(KEY_DB_ENABLED));
                            intent.putExtra(KEY_SAVE_INTERVAL, data.get(KEY_SAVE_INTERVAL));
                            intent.putExtra(KEY_TIME_UNIT, data.get(KEY_TIME_UNIT));
                            intent.putExtra(KEY_MAX_COUNT_LOC_SAVE, data.get(KEY_MAX_COUNT_LOC_SAVE));
                            intent.putExtra(KEY_LOCATIONS_INTERVAL, data.get(KEY_LOCATIONS_INTERVAL));
                            intent.putExtra(KEY_LOCATIONS_INTERVAL_TIME_UNIT, data.get(KEY_LOCATIONS_INTERVAL_TIME_UNIT));
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                            break;
                        case FCM_RESPONSE_TYPE_MESSAGE:
                            intent = new Intent(ACTION_SHOW_MESSAGE);
                            intent.putExtra(KEY_MESSAGE, data.get(KEY_MESSAGE));
                            intent.putExtra(KEY_ACTION_MESSAGE_CODE, data.get(KEY_ACTION_MESSAGE_CODE));
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
