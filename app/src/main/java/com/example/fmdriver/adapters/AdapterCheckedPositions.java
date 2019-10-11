package com.example.fmdriver.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.objects.NewLocation;
import com.example.fmdriver.objects.PositionChecked;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;


public class AdapterCheckedPositions extends RecyclerView.Adapter<AdapterCheckedPositions.MyViewHolder> implements AppConstants {

    static class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout root;
        TextView labelPosItemIndex;
        TextView labelPosItemName;
        TextView labelPosItemLat;
        TextView labelPosItemLong;
        TextView labelPosItemDate;
        TextView labelPosItemSpeed;
        TextView labelPosItemAccuracy;
        ImageView imgShowOnMap;
        View divider;
        RelativeLayout clickLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    MainActivity activity;
    ArrayList<PositionChecked> items;

    public AdapterCheckedPositions(MainActivity activity, ArrayList<PositionChecked> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checked_positions_list, parent, false);

        MyViewHolder vh = new MyViewHolder(v);

        vh.root = (RelativeLayout) v.findViewById(R.id.root);
        vh.labelPosItemIndex = (TextView) v.findViewById(R.id.labelPosItemIndex);
        vh.labelPosItemName = (TextView) v.findViewById(R.id.labelPosItemName);
        vh.labelPosItemLat = (TextView) v.findViewById(R.id.labelPosItemLat);
        vh.labelPosItemLong = (TextView) v.findViewById(R.id.labelPosItemLong);
        vh.labelPosItemDate = (TextView) v.findViewById(R.id.labelPosItemDate);
        vh.labelPosItemSpeed = (TextView) v.findViewById(R.id.labelPosItemSpeed);
        vh.labelPosItemAccuracy = (TextView) v.findViewById(R.id.labelPosItemAccuracy);
        vh.imgShowOnMap = (ImageView) v.findViewById(R.id.imgShowOnMap);
        vh.divider = (View) v.findViewById(R.id.divider);
        vh.clickLayout = (RelativeLayout) v.findViewById(R.id.clickLayout);

        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PositionChecked gpsPosition = items.get(position);

        holder.labelPosItemIndex.setText("" + gpsPosition.getId());
        holder.labelPosItemName.setText(gpsPosition.getName());
        holder.labelPosItemLat.setText("Lat: " + gpsPosition.getLatitude());
        holder.labelPosItemLong.setText("Long: " + gpsPosition.getLongitude());
        holder.labelPosItemDate.setText("Datum: " + DateTimeUtils.getDateTime(new Date(gpsPosition.getDate())));
        holder.labelPosItemSpeed.setText("Rychlost: " + gpsPosition.getSpeed() + "Km/h");
        holder.labelPosItemAccuracy.setText("Přesnost: " + gpsPosition.getAccuracy() + "m");

        if (gpsPosition.isChecked()) {
            holder.root.setBackgroundColor(0xFFFFFC9F);
        } else {
            holder.root.setBackgroundColor(0xFFFFFF);
        }

        holder.imgShowOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkItem(position);

                NewLocation newLocation = new NewLocation(
                        gpsPosition.getDate(),
                        gpsPosition.getLatitude(),
                        gpsPosition.getLongitude(),
                        gpsPosition.getSpeed(),
                        gpsPosition.getAccuracy(),
                        0.0f);

                activity.showFragmentMap2(
                        newLocation,
                        null);
            }
        });

        holder.clickLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkItem(position);
                activity.showCheckedPositionDetails(items.get(position));
            }
        });

        if (position == items.size() - 1)
            holder.divider.setVisibility(View.GONE);
        else
            holder.divider.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void uncheckAllItems() {
        for (PositionChecked positionChecked : items) {
            positionChecked.setChecked(false);
        }
    }

    public void checkItem(int position) {
        uncheckAllItems();
        items.get(position).setChecked(true);
        MainActivity.itemsCheckedPositions = new ArrayList<>(items);
        this.notifyDataSetChanged();
    }
}
