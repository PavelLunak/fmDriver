package com.example.fmdriver.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.objects.Device;
import com.example.fmdriver.utils.AppConstants;


public class AdapterDevices extends RecyclerView.Adapter<AdapterDevices.MyViewHolder> implements AppConstants {

    static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout root;
        TextView labelServiceStatus;
        TextView labelGpsStatus;
        TextView labelStatusUnknown;
        ProgressBar progressStatus;
        TextView labelDeviceName;
        TextView labelDate;
        TextView labelDeviceDescription;
        ImageView imgItem;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    MainActivity activity;

    public AdapterDevices(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        MyViewHolder vh = new MyViewHolder(v);

        vh.root = (RelativeLayout) v.findViewById(R.id.root);
        vh.labelServiceStatus = (TextView) v.findViewById(R.id.labelServiceStatus);
        vh.labelStatusUnknown = (TextView) v.findViewById(R.id.labelStatusUnknown);
        vh.progressStatus = (ProgressBar) v.findViewById(R.id.progressStatus);
        vh.labelGpsStatus = (TextView) v.findViewById(R.id.labelGpsStatus);
        vh.labelDeviceName = (TextView) v.findViewById(R.id.labelDeviceName);
        vh.labelDate = (TextView) v.findViewById(R.id.labelDate);
        vh.labelDeviceDescription = (TextView) v.findViewById(R.id.labelDeviceDescription);
        vh.imgItem = (ImageView) v.findViewById(R.id.imgItem);

        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Device device = getItem(position);

        holder.labelDeviceName.setText(device.getName());
        holder.labelDeviceDescription.setText(device.getDescription());
        holder.labelDate.setText(device.getDate());

        if (device.isCurrent()) {
            holder.root.setBackgroundColor(activity.getResources().getColor(R.color.colorDeviceSpinnerCurrentItemBg));
            holder.imgItem.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_item_current));
        } else {
            holder.root.setBackgroundColor(activity.getResources().getColor(R.color.colorDeviceSpinnerItemBg));
            holder.imgItem.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_item));
        }

        if (!device.isServiceStatusUnknown()) {
            holder.labelServiceStatus.setTextColor(activity.getResources().getColor(device.isServiceIsStarted() ? R.color.colorDeviceItemStatusStarted : R.color.colorDeviceItemStatusStoped));
            holder.labelGpsStatus.setTextColor(activity.getResources().getColor(device.isGpsIsStarted() ? R.color.colorDeviceItemStatusStarted : R.color.colorDeviceItemStatusStoped));

            holder.labelServiceStatus.setTextColor(activity.getResources().getColor(device.isServiceIsStarted() ? R.color.colorDeviceItemStatusStarted : R.color.colorDeviceItemStatusStoped));
            holder.labelGpsStatus.setTextColor(activity.getResources().getColor(device.isGpsIsStarted() ? R.color.colorDeviceItemStatusStarted : R.color.colorDeviceItemStatusStoped));
        } else {
            holder.labelServiceStatus.setTextColor(activity.getResources().getColor(R.color.colorDeviceItemStatusStoped));
            holder.labelGpsStatus.setTextColor(activity.getResources().getColor(R.color.colorDeviceItemStatusStoped));
            holder.labelStatusUnknown.setTextColor(activity.getResources().getColor(R.color.colorDeviceItemStatusStarted));
        }

        holder.root.setBackgroundColor(activity.getResources().getColor(R.color.colorDeviceOdd/*isEven(position) ? R.color.colorDeviceEven : R.color.colorDeviceOdd*/));
    }

    public Device getItem(int position) {
        if (getItemCount() != 0) return activity.getRegisteredDevices().get(position);
        return null;
    }

    private boolean isEven(int position) {
        return position % 2 == 0;
    }

    @Override
    public int getItemCount() {
        if (activity.getRegisteredDevices() == null) return 0;
        return activity.getRegisteredDevices().size();
    }

    public void uncheckAllItems() {
        for (Device device : activity.getRegisteredDevices()) {
            device.setCurrent(false);
        }
    }

    public void checkItem(int position) {
        uncheckAllItems();
        getItem(position).setCurrent(true);
        this.notifyDataSetChanged();
    }
}
