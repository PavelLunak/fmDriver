package com.example.fmdriver.utils;

public class AppUtils implements AppConstants {

    public static String responseTypeToString(int responseType) {
        switch (responseType) {
            case FCM_RESPONSE_SERVICE_STATUS_STARTED:
                return "FCM_RESPONSE_SERVICE_STATUS_STARTED";
            case FCM_RESPONSE_SERVICE_STATUS_STOPED:
                return "FCM_RESPONSE_SERVICE_STATUS_STOPED";
            case FCM_RESPONSE_SERVICE_START:
                return "FCM_RESPONSE_SERVICE_START";
            case FCM_RESPONSE_SERVICE_STOP:
                return "FCM_RESPONSE_SERVICE_STOP";
            case FCM_RESPONSE_GPS_START:
                return "FCM_RESPONSE_GPS_START";
            case FCM_RESPONSE_GPS_STOP:
                return "FCM_RESPONSE_GPS_STOP";
            case FCM_RESPONSE_TYPE_LOCATION:
                return "FCM_RESPONSE_TYPE_LOCATION";
            default: return "unknown";
        }
    }

    public static String requestTypeToString(int requestType) {
        switch (requestType) {
            case FCM_REQUEST_TYPE_SERVICE_STATUS:
                return "FCM_REQUEST_TYPE_SERVICE_STATUS";
            case FCM_REQUEST_TYPE_SERVICE_START:
                return "FCM_REQUEST_TYPE_SERVICE_START";
            case FCM_REQUEST_TYPE_SERVICE_STOP:
                return "FCM_REQUEST_TYPE_SERVICE_STOP";
            case FCM_REQUEST_TYPE_GPS_START:
                return "FCM_REQUEST_TYPE_GPS_START";
            case FCM_REQUEST_TYPE_GPS_STOP:
                return "FCM_REQUEST_TYPE_GPS_STOP";
            default: return "unknown";
        }
    }
}
