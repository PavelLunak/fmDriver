package com.example.fmdriver.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.fmdriver.utils.DateTimeUtils;

public class NewLocation implements Parcelable {

    long date;
    double latitude;
    double longitude;
    float speed;
    float accuracy;
    float batteryStatus;


    public NewLocation() {}

    public NewLocation(long date, double latitude, double longitude, float speed, float accuracy, float batteryStatus) {
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accuracy = accuracy;
        this.batteryStatus = batteryStatus;
    }


    @Override
    public String toString() {
        return "NewLocation{" +
                "\ndate=" + DateTimeUtils.getDateTime(date) +
                "\nlatitude=" + latitude +
                "\nlongitude=" + longitude +
                "\naccuracy=" + accuracy +
                "\nbattery=" + batteryStatus +
                "}";
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(float batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.date);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeFloat(this.speed);
        dest.writeFloat(this.accuracy);
        dest.writeFloat(this.batteryStatus);
    }

    protected NewLocation(Parcel in) {
        this.date = in.readLong();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.speed = in.readFloat();
        this.accuracy = in.readFloat();
        this.batteryStatus = in.readFloat();
    }

    public static final Parcelable.Creator<NewLocation> CREATOR = new Parcelable.Creator<NewLocation>() {
        @Override
        public NewLocation createFromParcel(Parcel source) {
            return new NewLocation(source);
        }

        @Override
        public NewLocation[] newArray(int size) {
            return new NewLocation[size];
        }
    };
}
