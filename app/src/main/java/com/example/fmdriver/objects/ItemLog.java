package com.example.fmdriver.objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class ItemLog implements Parcelable {

    private String text;
    private long date;
    private boolean marked;
    private int colorRes;


    public ItemLog() {}

    public ItemLog(String text, boolean marked) {
        this.text = text;
        this.date = new Date().getTime();
        this.marked = marked;
        this.colorRes = -1;
    }

    public ItemLog(String text, boolean marked, int colorRes) {
        this.text = text;
        this.date = new Date().getTime();
        this.marked = marked;
        this.colorRes = colorRes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public int getColorRes() {
        return colorRes;
    }

    public void setColorRes(int colorRes) {
        this.colorRes = colorRes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeLong(this.date);
        dest.writeByte(this.marked ? (byte) 1 : (byte) 0);
        dest.writeInt(this.colorRes);
    }

    protected ItemLog(Parcel in) {
        this.text = in.readString();
        this.date = in.readLong();
        this.marked = in.readByte() != 0;
        this.colorRes = in.readInt();
    }

    public static final Parcelable.Creator<ItemLog> CREATOR = new Parcelable.Creator<ItemLog>() {
        @Override
        public ItemLog createFromParcel(Parcel source) {
            return new ItemLog(source);
        }

        @Override
        public ItemLog[] newArray(int size) {
            return new ItemLog[size];
        }
    };
}
