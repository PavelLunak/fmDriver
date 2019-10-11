package com.example.fmdriver.retrofit.requests;

import com.example.fmdriver.retrofit.objects.RequestToFcmData;

public class RequestToFcm {

    private String to;
    private String collapse_key;
    private String priority;
    private int time_to_live;
    private RequestToFcmData data;


    public RequestToFcm() {}

    public RequestToFcm(String to, RequestToFcmData data) {
        this.to = to;
        this.collapse_key = "type_a";
        this.priority = "high";
        this.time_to_live = 10;
        this.data = data;
    }


    @Override
    public String toString() {
        return new StringBuilder("RequestToFcm: ")
                .append("\nto: ")
                .append(to == null ? "null" : to)
                .append("\ndata: ")
                .append(data == null ? "null" : data.toString())
                .toString();
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
}
