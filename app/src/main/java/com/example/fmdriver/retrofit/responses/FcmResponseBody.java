package com.example.fmdriver.retrofit.responses;

import com.google.gson.annotations.SerializedName;

public class FcmResponseBody {

    //Unique ID (number) identifying the multicast message
    @SerializedName("multicast_id")
    private int multicastId;

    //Number of messages that were processed without an error.
    @SerializedName("success")
    private int success;

    //Number of messages that could not be processed.
    @SerializedName("failure")
    private int failure;


}
