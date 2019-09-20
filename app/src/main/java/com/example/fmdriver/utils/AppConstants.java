package com.example.fmdriver.utils;

import com.example.fmdriver.R;

public interface AppConstants {

    int animShowFragment = R.anim.anim_fragment_show;
    int animHideFragment = R.anim.anim_fragment_hide;

    String TAG = "log_tag";

    int LOCATION_INTERVAL = 1000;
    int FASTEST_LOCATION_INTERVAL = 1000;

    int FCM_REQUEST_TYPE_SERVICE_STATUS = 1;
    int FCM_REQUEST_TYPE_SERVICE_START = 2;
    int FCM_REQUEST_TYPE_SERVICE_STOP = 3;
    int FCM_REQUEST_TYPE_GPS_START = 4;
    int FCM_REQUEST_TYPE_GPS_STOP = 5;

    int FCM_RESPONSE_SERVICE_STATUS_STARTED = 11;
    int FCM_RESPONSE_SERVICE_STATUS_STOPED = 12;
    int FCM_RESPONSE_SERVICE_START = 13;
    int FCM_RESPONSE_SERVICE_STOP = 14;
    int FCM_RESPONSE_GPS_START = 15;
    int FCM_RESPONSE_GPS_STOP = 16;
    int FCM_RESPONSE_TYPE_LOCATION = 17;
    int FCM_RESPONSE_TYPE_MESSAGE = 18;
    int FCM_RESPONSE_TYPE_MESSAGE_ERROR = 19;

    String KEY_RESPONSE_TYPE = "responseType";
    String KEY_RESPONSE_SERVICE_STATUS = "key_service_status";
    String KEY_DATA = "data";
    String KEY_TOKEN_FOR_RESPONSE = "thisFcmToken";
    String KEY_MESSAGE = "message";

    String ACTION_SERVICE_STATUS_BROADCAST = "action_service_status";
    String ACTION_LOCATION_BROADCAST = "action_location_receiver";
    String ACTION_SERVICE_STARTET_BROADCAST = "action_service_startet";
    String ACTION_SERVICE_STOPED_BROADCAST = "action_service_stoped";

    float ALPHA_VISIBILITY_VISIBLE = 1f;
    float ALPHA_VISIBILITY_GONE = 0.15f;
}
