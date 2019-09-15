package com.example.fmdriver.utils;

import com.example.fmdriver.R;

public interface AppConstants {

    int animShowFragment = R.anim.anim_fragment_show;
    int animHideFragment = R.anim.anim_fragment_hide;

    String TAG = "log_tag";

    int LOCATION_INTERVAL = 1000;
    int FASTEST_LOCATION_INTERVAL = 1000;

    int REQUEST_TYPE_START = 1;
    int REQUEST_TYPE_STOP = 2;

    String ACTION_LOCATION_BROADCAST = "action_location_receiver";
}
