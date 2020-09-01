package com.example.fmdriver.listeners.internaldatabase;

import com.example.fmdriver.objects.Device;

import java.util.ArrayList;

public interface OnAllDevicesLoadedListener {
    public void onAllDevicesLoaded(ArrayList<Device> devices);
}
