package com.example.fmdriver.retrofit.objects;


import com.example.fmdriver.utils.AppUtils;

public class RequestSettingsSaveIntoDatabase extends RequestToFcmData {

    private int savingToDatabaseEnabled;    //0 = DISABLED, 1 = ENABLED
    private long autoCheckedPositionSavingInterval;
    private int maxCountOfLocationChecked;
    private int timeUnit;


    public RequestSettingsSaveIntoDatabase(
            String thisFcmToken,
            int requestType,
            int savingToDatabaseEnabled,
            long autoCheckedPositionSavingInterval,
            int maxCountOfLocationChecked,
            int timeUnit) {

        super(thisFcmToken, requestType);
        this.savingToDatabaseEnabled = savingToDatabaseEnabled;
        this.autoCheckedPositionSavingInterval = autoCheckedPositionSavingInterval;
        this.maxCountOfLocationChecked = maxCountOfLocationChecked;
        this.timeUnit = timeUnit;
    }


    @Override
    public String toString() {
        return new StringBuilder("RequestToFcmData: ")
                .append("\nthisFcmToken: ")
                .append(super.thisFcmToken == null ? "null" : thisFcmToken)
                .append("\nrequestType: ")
                .append(AppUtils.requestTypeToString(this.requestType))
                .append("\nsavingToDatabaseEnabled: ")
                .append(savingToDatabaseEnabled)
                .append("\nautoCheckedPositionSavingInterval: ")
                .append(autoCheckedPositionSavingInterval)
                .append("\nmaxCountOfLocationChecked: ")
                .append(maxCountOfLocationChecked)
                .toString();
    }


    public int isSavingToDatabaseEnabled() {
        return savingToDatabaseEnabled;
    }

    public void setSavingToDatabaseEnabled(int savingToDatabaseEnabled) {
        this.savingToDatabaseEnabled = savingToDatabaseEnabled;
    }

    public long getAutoCheckedPositionSavingInterval() {
        return autoCheckedPositionSavingInterval;
    }

    public void setAutoCheckedPositionSavingInterval(long autoCheckedPositionSavingInterval) {
        this.autoCheckedPositionSavingInterval = autoCheckedPositionSavingInterval;
    }

    public int getMaxCountOfLocationChecked() {
        return maxCountOfLocationChecked;
    }

    public void setMaxCountOfLocationChecked(int maxCountOfLocationChecked) {
        this.maxCountOfLocationChecked = maxCountOfLocationChecked;
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }
}
