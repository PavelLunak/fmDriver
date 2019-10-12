package com.example.fmdriver.interfaces;


import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref
public interface AppPrefs {
    String fcmToken();
    long locationInterval();
    int locationIntervalTimeUnit();
    boolean savingToDatabaseEnabled();
    long autoCheckedPositionSavingInterval();
    int maxCountOfLocationChecked();
    int timeUnit();
    String lastPhoneNumber();
}
