package com.example.fmdriver.objects;

import android.os.Parcel;
import android.os.Parcelable;


public class Position implements Parcelable {

    int id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
    private long proximity;
    private boolean proximityEnabled;
    private boolean isChecked;

    public Position() {}

    public Position(String name,
                    String description,
                    double latitude,
                    double longitude,
                    long proximity,
                    boolean proximityEnabled) {

        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.proximity = proximity;
        this.proximityEnabled = proximityEnabled;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n")
                .append(this.name)
                .append("\n")
                .append(this.description)
                .append("\n")
                .append("Lat: " + this.latitude)
                .append("\n")
                .append("Long: " + this.longitude)
                .append("\n");

        return sb.toString();
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

    public long getProximity() {
        return proximity;
    }

    public void setProximity(long proximity) {
        this.proximity = proximity;
    }

    public boolean isProximityEnabled() {
        return proximityEnabled;
    }

    public void setProximityEnabled(boolean proximityEnabled) {
        this.proximityEnabled = proximityEnabled;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeLong(this.proximity);
        dest.writeByte(this.proximityEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
    }

    protected Position(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.proximity = in.readLong();
        this.proximityEnabled = in.readByte() != 0;
        this.isChecked = in.readByte() != 0;
    }

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel source) {
            return new Position(source);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };
}
