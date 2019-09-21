package com.example.fmdriver.retrofit.objects;

import com.example.fmdriver.utils.AppUtils;

public class RequestToFcmData {

    private String thisFcmToken;
    private int requestType;


    public RequestToFcmData() {}

    public RequestToFcmData(String thisFcmToken, int requestType) {
        this.thisFcmToken = thisFcmToken;
        this.requestType = requestType;
    }


    @Override
    public String toString() {
        return new StringBuilder("RequestToFcmData: ")
                .append("\nthisFcmToken: ")
                .append(thisFcmToken == null ? "null" : thisFcmToken)
                .append("\nrequestType: ")
                .append(AppUtils.requestTypeToString(requestType))
                .toString();
    }
}
