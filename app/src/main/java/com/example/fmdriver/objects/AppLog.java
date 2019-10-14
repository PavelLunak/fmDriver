package com.example.fmdriver.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AppLog implements Parcelable {

    private ArrayList<ItemLog> itemsLog;


    public AppLog() {
        this.itemsLog = new ArrayList<>();
    }

    public void addLog(String log) {
        if (log == null) return;
        if (log.trim().equals("")) return;

        uncheckAll();
        this.itemsLog.add(new ItemLog(log, true));
    }

    public ItemLog getItem(int position) {
        if (itemsLog == null) return null;
        return this.itemsLog.get(position);
    }

    public ArrayList<ItemLog> getItemsLog() {
        return itemsLog;
    }

    public int getItemsCount() {
        if (itemsLog == null) return 0;
        return itemsLog.size();
    }

    public void uncheckAll() {
        if (itemsLog == null) return;

        for (ItemLog itemLog : itemsLog) {
            itemLog.setMarked(false);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.itemsLog);
    }

    protected AppLog(Parcel in) {
        this.itemsLog = in.createTypedArrayList(ItemLog.CREATOR);
    }

    public static final Parcelable.Creator<AppLog> CREATOR = new Parcelable.Creator<AppLog>() {
        @Override
        public AppLog createFromParcel(Parcel source) {
            return new AppLog(source);
        }

        @Override
        public AppLog[] newArray(int size) {
            return new AppLog[size];
        }
    };
}
