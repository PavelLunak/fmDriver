package com.example.fmdriver.utils;

import com.example.fmdriver.R;

public interface AppConstants {

    int animShowFragment = R.anim.anim_fragment_show;
    int animHideFragment = R.anim.anim_fragment_hide;

    String TAG = "log_tag";

    int LOCATION_INTERVAL = 1000;
    int FASTEST_LOCATION_INTERVAL = 1000;

    long MAX_TIME_FOR_WAITING_FCM_RESPONSE = 11000;
    long MAX_TIME_FOR_WAITING_FCM_RESPONSE_LOCATION_CREATE = 11000;

    int FCM_REQUEST_TYPE_SERVICE_STATUS = 1;
    int FCM_REQUEST_TYPE_SERVICE_START = 2;
    int FCM_REQUEST_TYPE_SERVICE_STOP = 3;
    int FCM_REQUEST_TYPE_GPS_START = 4;
    int FCM_REQUEST_TYPE_GPS_STOP = 5;
    int FCM_REQUEST_TYPE_CANCEL = 6;
    int FCM_REQUEST_TYPE_LOCATION = 7;
    int FCM_REQUEST_TYPE_SETTINGS_DATABASE = 8;
    int FCM_REQUEST_TYPE_LOAD_SETTINGS = 9;
    int FCM_REQUEST_TYPE_ALARM = 10;
    int FCM_REQUEST_TYPE_CALL = 101;
    int FCM_REQUEST_TYPE_LOCATION_RESULT = 102;

    int FCM_RESPONSE_SERVICE_STATUS_STARTED = 11;
    int FCM_RESPONSE_SERVICE_STATUS_STOPED = 12;
    int FCM_RESPONSE_GPS_START = 15;
    int FCM_RESPONSE_GPS_STOP = 16;
    int FCM_RESPONSE_TYPE_LOCATION = 17;
    int FCM_RESPONSE_TYPE_LOCATION_DISABLED = 18;
    int FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVED = 19;
    int FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVE_ERROR = 20;
    int FCM_RESPONSE_TYPE_SETTINGS_LOADED = 21;
    int FCM_RESPONSE_TYPE_MESSAGE = 22;
    int FCM_RESPONSE_SERVICE_STATUS = 23;

    int DB_SETTINGS_UPDATED_SUCCESS = 1;
    int DB_SETTINGS_UPDATED_ERROR = 0;

    String KEY_RESPONSE_TYPE = "responseType";
    String KEY_RESPONSE_SERVICE_STATUS = "key_service_status";
    String KEY_DATA = "data";
    String KEY_TOKEN_FOR_RESPONSE = "thisFcmToken";
    String KEY_LOCATION_DISABLED = "gps_disabled";
    String KEY_SAVE_NEW_DB_SETTINGS = "db_settings";
    String KEY_DB_ENABLED = "savingToDatabaseEnabled";
    String KEY_SAVE_INTERVAL = "autoCheckedPositionSavingInterval";
    String KEY_TIME_UNIT = "timeUnit";
    String KEY_MAX_COUNT_LOC_SAVE = "maxCountOfLocationChecked";
    String KEY_MESSAGE = "message";
    String KEY_BATTERY = "battery";
    String KEY_SERVICE_STATUS = "serviceStatus";
    String KEY_GPS_STATUS = "gpsStatus";

    String ACTION_SERVICE_STATUS_BROADCAST = "action_service_status";
    String ACTION_LOCATION_BROADCAST = "action_location_receiver";
    String ACTION_SERVICE_STARTET_BROADCAST = "action_service_startet";
    String ACTION_SERVICE_STOPED_BROADCAST = "action_service_stoped";
    String ACTION_DATABASE_SETTINGS_UPDATED = "action_db_settings_updated";
    String ACTION_DATABASE_SETTINGS_LOADED = "action_db_settings_loaded";
    String ACTION_SHOW_MESSAGE = "action_show_message";

    int TIME_UNIT_SECONDS = 1;
    int TIME_UNIT_MINUTES = 2;
    int TIME_UNIT_HOURS = 3;

    int STARTED = 1;
    int STOPED = 0;

    int COUNT_OF_LOCATIONS_INFINITY = -2;

    float ALPHA_VISIBILITY_VISIBLE = 1f;
    float ALPHA_VISIBILITY_GONE = 0.15f;
}
