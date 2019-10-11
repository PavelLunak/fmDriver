package com.example.fmdriver.retrofit.responses;

import com.example.fmdriver.objects.PositionChecked;
import com.google.gson.annotations.SerializedName;

public class ResponseCheckedPosition {

    @SerializedName("id")
    int id;

    @SerializedName("nazev")
    private String nazev;

    @SerializedName("datum")
    private String datum;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("rychlost")
    private float rychlost;

    @SerializedName("accuracy")
    private float accuracy;

    @SerializedName("country_code")
    private String country_code;

    @SerializedName("country_name")
    private String country_name;

    @SerializedName("feature_name")
    private String feature_name;

    @SerializedName("locality")
    private String locality;

    @SerializedName("phone")
    private String phone;

    @SerializedName("postal_code")
    private String postal_code;

    @SerializedName("premises")
    private String premises;

    @SerializedName("adminArea")
    private String adminArea;

    @SerializedName("sub_adminArea")
    private String sub_adminArea;

    @SerializedName("sub_locality")
    private String sub_locality;

    @SerializedName("thoroughfare")
    private String thoroughfare;

    @SerializedName("sub_thoroughfare")
    private String sub_thoroughfare;

    @SerializedName("url")
    private String url;

    @SerializedName("status")
    private int status;


    public ResponseCheckedPosition() {}

    public ResponseCheckedPosition(
            int id,
            String nazev,
            String datum,
            String latitude,
            String longitude,
            float rychlost,
            float accuracy,
            String country_code,
            String country_name,
            String feature_name,
            String locality,
            String phone,
            String postal_code,
            String premises,
            String adminArea,
            String sub_adminArea,
            String sub_locality,
            String thoroughfare,
            String sub_thoroughfare,
            String url) {

        this.id = id;
        this.nazev = nazev;
        this.datum = datum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rychlost = rychlost;
        this.accuracy = accuracy;
        this.country_code = country_code;
        this.country_name = country_name;
        this.feature_name = feature_name;
        this.locality = locality;
        this.phone = phone;
        this.postal_code = postal_code;
        this.premises = premises;
        this.adminArea = adminArea;
        this.sub_adminArea = sub_adminArea;
        this.sub_locality = sub_locality;
        this.thoroughfare = thoroughfare;
        this.sub_thoroughfare = sub_thoroughfare;
        this.url = url;


    }

    public PositionChecked toPositionChecked() {
        PositionChecked toReturn = new PositionChecked();

        toReturn.setId(this.getId());
        toReturn.setName(this.getNazev());

        try {
            toReturn.setDate(Long.parseLong(this.getDatum()));
        } catch (NumberFormatException e) {
            toReturn.setDate(0);
        }

        try {
            toReturn.setLatitude(Double.parseDouble(this.getLatitude()));
        } catch (NumberFormatException e) {
            toReturn.setLatitude(0);
        }

        try {
            toReturn.setLongitude(Double.parseDouble(this.getLongitude()));
        } catch (NumberFormatException e) {
            toReturn.setLongitude(0);
        }

        toReturn.setSpeed(this.getRychlost());
        toReturn.setAccuracy(this.getAccuracy());
        toReturn.setCountryCode(this.country_code);
        toReturn.setCountryName(this.country_name);
        toReturn.setFeatureName(this.feature_name);
        toReturn.setLocality(this.getLocality());
        toReturn.setPhone(this.getPhone());
        toReturn.setPostalCode(this.getPostal_code());
        toReturn.setPremises(this.getPremises());
        toReturn.setAdminArea(this.getAdminArea());
        toReturn.setSubAdminArea(this.getSub_adminArea());
        toReturn.setSubLocality(this.getSub_locality());
        toReturn.setThoroughfare(this.getThoroughfare());
        toReturn.setSubThoroughfare(this.getSub_thoroughfare());
        toReturn.setUrl(this.getUrl());
        toReturn.setStatus(this.getStatus());

        return toReturn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public float getRychlost() {
        return rychlost;
    }

    public void setRychlost(float rychlost) {
        this.rychlost = rychlost;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getFeature_name() {
        return feature_name;
    }

    public void setFeature_name(String feature_name) {
        this.feature_name = feature_name;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public void setAdminArea(String adminArea) {
        this.adminArea = adminArea;
    }

    public String getSub_adminArea() {
        return sub_adminArea;
    }

    public void setSub_adminArea(String sub_adminArea) {
        this.sub_adminArea = sub_adminArea;
    }

    public String getSub_locality() {
        return sub_locality;
    }

    public void setSub_locality(String sub_locality) {
        this.sub_locality = sub_locality;
    }

    public String getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(String thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public String getSub_thoroughfare() {
        return sub_thoroughfare;
    }

    public void setSub_thoroughfare(String sub_thoroughfare) {
        this.sub_thoroughfare = sub_thoroughfare;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
