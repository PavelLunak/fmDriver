package com.example.fmdriver.retrofit.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseAllCheckedPositions {

    @SerializedName("positions")
    private List<ResponseCheckedPosition> positions;

    @SerializedName("message")
    private String message;

    @SerializedName("count")
    private int count;


    public ResponseAllCheckedPositions() {}

    public ResponseAllCheckedPositions(List<ResponseCheckedPosition> positions, String message, int count) {
        this.positions = positions;
        this.message = message;
        this.count = count;
    }

    public List<ResponseCheckedPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<ResponseCheckedPosition> positions) {
        this.positions = positions;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
