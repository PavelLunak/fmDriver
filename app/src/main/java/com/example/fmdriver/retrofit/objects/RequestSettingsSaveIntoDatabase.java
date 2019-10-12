package com.example.fmdriver.retrofit.objects;


public class RequestSettingsSaveIntoDatabase extends RequestToFcmData {

    private int savingToDatabaseEnabled;    //0 = DISABLED, 1 = ENABLED
    private long autoCheckedPositionSavingInterval;
    private int maxCountOfLocationChecked;
    private int timeUnit;
    private long locationsInterval;
    private int locationsIntervalTimeUnit;


    public RequestSettingsSaveIntoDatabase(
            String thisFcmToken,
            int requestType,
            int savingToDatabaseEnabled,
            long autoCheckedPositionSavingInterval,
            int maxCountOfLocationChecked,
            int timeUnit,
            long locationsInterval,
            int locationsIntervalTimeUnit) {

        super(thisFcmToken, requestType);
        this.savingToDatabaseEnabled = savingToDatabaseEnabled;
        this.autoCheckedPositionSavingInterval = autoCheckedPositionSavingInterval;
        this.maxCountOfLocationChecked = maxCountOfLocationChecked;
        this.timeUnit = timeUnit;
        this.locationsInterval = locationsInterval;
        this.locationsIntervalTimeUnit = locationsIntervalTimeUnit;
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

    public long getLocationsInterval() {
        return locationsInterval;
    }

    public void setLocationsInterval(long locationsInterval) {
        this.locationsInterval = locationsInterval;
    }

    public int getLocationsIntervalTimeUnit() {
        return locationsIntervalTimeUnit;
    }

    public void setLocationsIntervalTimeUnit(int locationsIntervalTimeUnit) {
        this.locationsIntervalTimeUnit = locationsIntervalTimeUnit;
    }
}
