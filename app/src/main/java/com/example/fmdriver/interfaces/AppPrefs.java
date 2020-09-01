package com.example.fmdriver.interfaces;


import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref
public interface AppPrefs {
    String fcmToken();
    String actualRemoteToken();
    int actualRemoteDeviceId();   //Id v tabulce v externí databázi
    String actualRemoteAndroidId();
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
