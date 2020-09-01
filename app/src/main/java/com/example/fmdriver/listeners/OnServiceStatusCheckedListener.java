package com.example.fmdriver.listeners;

public interface OnServiceStatusCheckedListener {
    public void onServiceStatusChecked(
            String senderFcmToken,
            int senderDatabaseId,
            String senderAndroidId,
            int serviceStatus,
            int gpsStatus);
}
