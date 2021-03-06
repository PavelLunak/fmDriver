package com.example.fmdriver.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Device implements Parcelable {
    private int id;
    private String name;
    private String description;
    private String token;
    private String date;
    private DeviceIdentification deviceIdentification;

    private boolean serviceIsStarted;
    private boolean gpsIsStarted;
    private boolean serviceStatusUnknown;

    private String dateOfLastServiceUpdate;   //Datum poslední změny stavu služby z FmDriver
    private long dateMillis;                //Datum registrace
    private boolean isCurrent;              //Je aktuálně zobrayeno


    public Device() {}

    public Device(
            int id,
            String name,
            String description,
            String token,
            String date,
            DeviceIdentification deviceIdentification) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.token = token;
        this.date = date;
        this.deviceIdentification = deviceIdentification;
    }

    //gettery a settery navíc pro získání (uložení) hodnoty z(do) DeviceIdentification;
    //------------------------------------------------------------------
    public String getAndroidId() {
        if (this.deviceIdentification == null) return "";
        if (this.deviceIdentification.getAndroidId() == null) return "";
        return this.deviceIdentification.getAndroidId();
    }

    public String getDeviceId() {
        if (this.deviceIdentification == null) return "";
        if (this.deviceIdentification.getDeviceId() == null) return "";
        return this.deviceIdentification.getDeviceId();
    }

    public void setAndroidId(String androidId) {
        if (this.deviceIdentification == null) this.deviceIdentification = new DeviceIdentification();
        this.deviceIdentification.setAndroidId(androidId);
    }

    public void setDeviceId(String deviceId) {
        if (this.deviceIdentification == null) this.deviceIdentification = new DeviceIdentification();
        this.deviceIdentification.setDeviceId(deviceId);
    }
    //------------------------------------------------------------------

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
        this.dateMillis = parseStringDate(date);
    }

    public DeviceIdentification getDeviceIdentification() {
        return deviceIdentification;
    }

    public void setDeviceIdentification(DeviceIdentification deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public boolean isServiceIsStarted() {
        return serviceIsStarted;
    }

    public void setServiceIsStarted(boolean serviceIsStarted) {
        this.serviceIsStarted = serviceIsStarted;
    }

    public boolean isGpsIsStarted() {
        return gpsIsStarted;
    }

    public void setGpsIsStarted(boolean gpsIsStarted) {
        this.gpsIsStarted = gpsIsStarted;
    }

    public boolean isServiceStatusUnknown() {
        return serviceStatusUnknown;
    }

    public void setServiceStatusUnknown(boolean serviceStatusUnknown) {
        this.serviceStatusUnknown = serviceStatusUnknown;
    }

    public String getDateOfLastServiceUpdate() {
        return dateOfLastServiceUpdate;
    }

    public void setDateOfLastServiceUpdate(String dateOfLastServiceUpdate) {
        this.dateOfLastServiceUpdate = dateOfLastServiceUpdate;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.token);
        dest.writeString(this.date);
        dest.writeParcelable(this.deviceIdentification, flags);
        dest.writeByte(this.serviceIsStarted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.gpsIsStarted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.serviceStatusUnknown ? (byte) 1 : (byte) 0);
        dest.writeString(this.dateOfLastServiceUpdate);
        dest.writeLong(this.dateMillis);
        dest.writeByte(this.isCurrent ? (byte) 1 : (byte) 0);
    }

    protected Device(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.token = in.readString();
        this.date = in.readString();
        this.deviceIdentification = in.readParcelable(DeviceIdentification.class.getClassLoader());
        this.serviceIsStarted = in.readByte() != 0;
        this.gpsIsStarted = in.readByte() != 0;
        this.serviceStatusUnknown = in.readByte() != 0;
        this.dateOfLastServiceUpdate = in.readString();
        this.dateMillis = in.readLong();
        this.isCurrent = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel source) {
            return new Device(source);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
}
