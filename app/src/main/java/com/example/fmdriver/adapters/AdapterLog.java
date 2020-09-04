package com.example.fmdriver.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.objects.ItemLog;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.DateTimeUtils;

import java.util.Date;


public class AdapterLog extends RecyclerView.Adapter<AdapterLog.MyViewHolder> implements AppConstants {

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView label_log;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    MainActivity activity;

    public AdapterLog(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        vh.label_log = (TextView) v.findViewById(R.id.label_log);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (activity.appLog == null) return;

        final ItemLog log = activity.appLog.getItem(position);

        if (log == null) return;

        holder.label_log.setText(DateTimeUtils.getDateTime(log.getDate()) + " " + log.getText());

        if (log.isMarked()) holder.label_log.setTextColor(Color.YELLOW);
        else holder.label_log.setTextColor(Color.GRAY);

        if (log.getColorRes() != -1) holder.label_log.setTextColor(activity.getResources().getColor(log.getColorRes()));
    }

    @Override
    public int getItemCount() {
        if (activity.appLog == null) return 0;
        return activity.appLog.getItemsCount();
    }
}
