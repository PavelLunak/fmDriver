package com.example.fmdriver.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DeviceIdentification implements Parcelable {

    @SerializedName("android_id")
    private String androidId;

    @SerializedName("device_id")
    private String deviceId;


    public DeviceIdentification() {}

    public DeviceIdentification(String androidId, String deviceId) {
        this.androidId = androidId;
        this.deviceId = deviceId;
    }


    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.androidId);
        dest.writeString(this.deviceId);
    }

    protected DeviceIdentification(Parcel in) {
        this.androidId = in.readString();
        this.deviceId = in.readString();
    }

    public static final Creator<DeviceIdentification> CREATOR = new Creator<DeviceIdentification>() {
        @Override
        public DeviceIdentification createFromParcel(Parcel source) {
            return new DeviceIdentification(source);
        }

        @Override
        public DeviceIdentification[] newArray(int size) {
            return new DeviceIdentification[size];
        }
    };
}
