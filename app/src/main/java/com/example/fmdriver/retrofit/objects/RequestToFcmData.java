package com.example.fmdriver.retrofit.objects;

public class RequestToFcmData {

    private String thisFcmToken;
    private int requestType;


    public RequestToFcmData() {}

    public RequestToFcmData(String thisFcmToken, int requestType) {
        this.thisFcmToken = thisFcmToken;
        this.requestType = requestType;
    }
}
