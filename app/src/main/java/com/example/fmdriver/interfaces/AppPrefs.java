package com.example.fmdriver.interfaces;


import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref
public interface AppPrefs {

    boolean darkTheme();

    int positionDetectInterval();   //SEKUNDY
    int stopDelay();                //MINUTY
    int saveTimeInterval();         //MINUTY

    int itemsPerPage();
    int saveCountInterval();

    String fcmToken();
}
