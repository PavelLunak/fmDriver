package com.example.fmdriver.retrofit.requests;

import com.example.fmdriver.retrofit.objects.RequestPositionData;

public class RequestPosition {

    private String to;
    private String collapse_key;
    private String priority;
    private int time_to_live;
    private RequestPositionData data;


    public RequestPosition() {}

    public RequestPosition(String to, RequestPositionData data) {
        this.to = to;
        this.collapse_key = "type_a";
        this.priority = "high";
        this.time_to_live = 60;
        this.data = data;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCollapse_key() {
        return collapse_key;
    }

    public void setCollapse_key(String collapse_key) {
        this.collapse_key = collapse_key;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getTime_to_live() {
        return time_to_live;
    }

    public void setTime_to_live(int time_to_live) {
        this.time_to_live = time_to_live;
    }

    public RequestPositionData getData() {
        return data;
    }

    public void setData(RequestPositionData data) {
        this.data = data;
    }
}
