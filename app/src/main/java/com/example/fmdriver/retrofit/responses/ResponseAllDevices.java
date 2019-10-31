package com.example.fmdriver.retrofit.responses;

import com.example.fmdriver.objects.Device;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseAllDevices {

    @SerializedName("devices")
    private List<ResponseDevice> devices;

    @SerializedName("message")
    private String message;


    public ResponseAllDevices() {}

    public ResponseAllDevices(List<ResponseDevice> devices, String message) {
        this.devices = devices;
        this.message = message;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Registered devices: ");

        if (devices == null) {
            return sb.append("NULL").toString();
        }

        if (devices.isEmpty()) {
            return sb.append("EMPTY").toString();
        }

        for (ResponseDevice responseDevice : devices) {
            sb.append("\n");
            sb.append(responseDevice.getName());
        }

        return sb.toString();
    }

    public List<ResponseDevice> getDevices() {
        return devices;
    }

    public void setPositions(List<ResponseDevice> positions) {
        this.devices = positions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
