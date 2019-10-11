package com.example.fmdriver.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class MarkerPosition implements Parcelable {
    private double lattitude;
    private double longitude;
    private String name;
    private String snippet;
    private int radius;


    public MarkerPosition() {}

    public MarkerPosition(double lattitude, double longitude) {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }

    public MarkerPosition(double lattitude, double longitude, String name) {
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.name = name;
    }

    public MarkerPosition(double lattitude, double longitude, String name, String snippet) {
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.name = name;
        this.snippet = snippet;
    }

    public MarkerPosition(double lattitude, double longitude, String name, int radius) {
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.name = name;
        this.radius = radius;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lattitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.name);
        dest.writeString(this.snippet);
        dest.writeInt(this.radius);
    }

    protected MarkerPosition(Parcel in) {
        this.lattitude = in.readDouble();
        this.longitude = in.readDouble();
        this.name = in.readString();
        this.snippet = in.readString();
        this.radius = in.readInt();
    }

    public static final Creator<MarkerPosition> CREATOR = new Creator<MarkerPosition>() {
        @Override
        public MarkerPosition createFromParcel(Parcel source) {
            return new MarkerPosition(source);
        }

        @Override
        public MarkerPosition[] newArray(int size) {
            return new MarkerPosition[size];
        }
    };
}
