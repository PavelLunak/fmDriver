package com.example.fmdriver.fragments;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.adapters.AdapterDevices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_devices)
public class FragmentDevices extends Fragment implements RecyclerView.OnItemTouchListener {

    @ViewById
    RecyclerView recyclerView;

    MainActivity activity;
    AdapterDevices adapter;

    float x1, y1, x2, y2;
    boolean cancelClick = false;

    CountDownTimer timer = new CountDownTimer(500, 500) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            activity.vibrate(100);
            Log.i("tag_motionevent", "CountDownTimer - onFinish()");
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @AfterViews
    void afterViews() {
        updateAdapter();
    }

    public void updateAdapter() {
        if (activity.getRegisteredDevices() != null) {
            adapter = new AdapterDevices(activity);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.addOnItemTouchListener(this);
        }
    }

    public AdapterDevices getAdapter() {
        return adapter;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        activity.processDeviceClick(e, this.recyclerView);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}
