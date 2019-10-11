package com.example.fmdriver.objects;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


public class CheckedPositionDetails implements Parcelable {

    private int id;
    private String countryCode;
    private String countryName;
    private String adminArea;
    private String subAdminArea;
    private String locality;
    private String subLocality;
    private String featureName;
    private String phone;
    private String postalCode;
    private String premises;
    private String thoroughfare;
    private String subThoroughfare;
    private Bundle extras;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public void setSubAdminArea(String subAdminArea) {
        this.subAdminArea = subAdminArea;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public String getSubThoroughfare() {
        return subThoroughfare;
    }

    public void setSubThoroughfare(String subThoroughfare) {
        this.subThoroughfare = subThoroughfare;
    }

    public Bundle getExtras() {
        return extras;
    }

    public void setExtras(Bundle extras) {
        this.extras = extras;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.countryCode);
        dest.writeString(this.countryName);
        dest.writeString(this.adminArea);
        dest.writeString(this.subAdminArea);
        dest.writeString(this.locality);
        dest.writeString(this.subLocality);
        dest.writeString(this.featureName);
        dest.writeString(this.phone);
        dest.writeString(this.postalCode);
        dest.writeString(this.premises);
        dest.writeString(this.thoroughfare);
        dest.writeString(this.subThoroughfare);
        dest.writeBundle(this.extras);
    }

    public CheckedPositionDetails() {
    }

    protected CheckedPositionDetails(Parcel in) {
        this.id = in.readInt();
        this.countryCode = in.readString();
        this.countryName = in.readString();
        this.adminArea = in.readString();
        this.subAdminArea = in.readString();
        this.locality = in.readString();
        this.subLocality = in.readString();
        this.featureName = in.readString();
        this.phone = in.readString();
        this.postalCode = in.readString();
        this.premises = in.readString();
        this.thoroughfare = in.readString();
        this.subThoroughfare = in.readString();
        this.extras = in.readBundle();
    }

    public static final Creator<CheckedPositionDetails> CREATOR = new Creator<CheckedPositionDetails>() {
        @Override
        public CheckedPositionDetails createFromParcel(Parcel source) {
            return new CheckedPositionDetails(source);
        }

        @Override
        public CheckedPositionDetails[] newArray(int size) {
            return new CheckedPositionDetails[size];
        }
    };
}
