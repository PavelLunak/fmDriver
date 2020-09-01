package com.example.fmdriver.retrofit.requests;

import com.example.fmdriver.retrofit.objects.RequestToFcmData;

public class MultiRequestToFcm {

    private String[] registration_ids;
    private String collapse_key;
    private String priority;
    private int time_to_live;
    private RequestToFcmData data;


    public MultiRequestToFcm(String[] registration_ids, RequestToFcmData data) {
        this.registration_ids = registration_ids;
        this.collapse_key = "type_a";
        this.priority = "high";
        this.time_to_live = 20;
        this.data = data;
    }


    @Override
    public String toString() {
        return new StringBuilder("RequestToFcm: ")
                .append("\nto: ")
                .append(recipientsToString(this.registration_ids))
                .append("\ndata: ")
                .append(this.data == null ? "null" : this.data.toString())
                .toString();
    }

    private String recipientsToString(String[] recipients) {
        StringBuilder sb = new StringBuilder("Recipients: ");

        if (recipients == null) return sb.append("NULL").toString();
        if (recipients.length == 0) return sb.append("EMPTY").toString();

        for (String recipient : recipients) {
            sb.append("\n\t" + recipient);
        }

        return sb.toString();
    }

    public String[] getRegistration_ids() {
        return registration_ids;
    }

    public void setRegistration_ids(String[] registration_ids) {
        this.registration_ids = registration_ids;
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

    public RequestToFcmData getData() {
        return data;
    }

    public void setData(RequestToFcmData data) {
        this.data = data;
    }
}
