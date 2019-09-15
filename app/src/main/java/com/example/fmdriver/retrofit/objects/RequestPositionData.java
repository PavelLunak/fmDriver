package com.example.fmdriver.retrofit.objects;

public class RequestPositionData {

    private String thisFcmToken;
    private int requestType;


    public RequestPositionData() {}

    public RequestPositionData(String thisFcmToken, int requestType) {
        this.thisFcmToken = thisFcmToken;
        this.requestType = requestType;
    }


    public String getThisFcmToken() {
        return thisFcmToken;
    }

    public void setThisFcmToken(String thisFcmToken) {
        this.thisFcmToken = thisFcmToken;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }
}
