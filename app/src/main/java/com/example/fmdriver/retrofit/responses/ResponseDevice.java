package com.example.fmdriver.retrofit.responses;

import com.example.fmdriver.objects.Device;
import com.example.fmdriver.objects.DeviceIdentification;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ResponseDevice {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("token")
    private String token;

    @SerializedName("android_id")
    private String android_id;

    @SerializedName("device_id")
    private String device_id;

    @SerializedName("date")
    private String date;


    public ResponseDevice() {}

    public ResponseDevice(
            int id,
            String name,
            String description,
            String token,
            String android_id,
            String device_id,
            String date) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.token = token;
        this.android_id = android_id;
        this.device_id = device_id;
        this.date = date;
    }

    public Device toDevice() {
        return new Device(
                this.id,
                this.name,
                this.description,
                this.token,
                this.date,
                new DeviceIdentification(this.android_id, this.device_id));
    }

    private long parseStringDate(String date) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date parsedDate =  sdf1.parse(date);
            return parsedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
