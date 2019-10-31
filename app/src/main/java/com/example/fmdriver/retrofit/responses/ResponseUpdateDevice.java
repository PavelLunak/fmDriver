package com.example.fmdriver.retrofit.responses;

import com.google.gson.annotations.SerializedName;

public class ResponseUpdateDevice {

    @SerializedName("device")
    private ResponseDevice responseDevice;

    @SerializedName("message")
    private String message;


    public ResponseUpdateDevice() {}

    public ResponseUpdateDevice(ResponseDevice responseDevice, String message) {
        this.responseDevice = responseDevice;
        this.message = message;
    }


    public ResponseDevice getResponseDevice() {
        return responseDevice;
    }

    public void setResponseDevice(ResponseDevice responseDevice) {
        this.responseDevice = responseDevice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
