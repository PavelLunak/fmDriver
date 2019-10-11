package com.example.fmdriver.listeners;


import com.example.fmdriver.objects.PositionChecked;

import java.util.ArrayList;

public interface OnAllCheckedPositionsLoadedListener {
    public void onAllCheckedPositionsLoaded(ArrayList<PositionChecked> itemsCheckedPositions);
}
