package com.example.fmdriver.retrofit.objects;

public class RequestToFcmCall extends RequestToFcmData {

    private String phoneNumber;


    public RequestToFcmCall(String thisFcmToken, int requestType, String phoneNumber) {
        super(thisFcmToken, requestType);
        this.phoneNumber = phoneNumber;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
