package com.example.fmdriver.interfaces;


import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref
public interface AppPrefs {
    String fcmToken();
    String remoteToken();
    int remoteDeviceId();
    long locationInterval();
    int locationIntervalTimeUnit();
    boolean savingToDatabaseEnabled();
    long autoCheckedPositionSavingInterval();
    int maxCountOfLocationChecked();
    int timeUnit();
    String lastPhoneNumber();
    String androidId();
    String deviceId();
}
