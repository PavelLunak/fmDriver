package com.example.fmdriver.retrofit.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseAllCheckedPositions {

    @SerializedName("positions")
    private List<ResponseCheckedPosition> positions;

    @SerializedName("message")
    private String message;

    public ResponseAllCheckedPositions() {}

    public ResponseAllCheckedPositions(List<ResponseCheckedPosition> positions, String message) {
        this.positions = positions;
        this.message = message;
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
}
