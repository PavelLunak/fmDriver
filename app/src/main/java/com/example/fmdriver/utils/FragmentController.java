package com.example.fmdriver.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.fmdriver.fragments.FragmentCheckedPositionDetails;
import com.example.fmdriver.fragments.FragmentCheckedPositions;
import com.example.fmdriver.fragments.FragmentDeviceDetail;
import com.example.fmdriver.fragments.FragmentDevices;
import com.example.fmdriver.fragments.FragmentLoad;
import com.example.fmdriver.fragments.FragmentMap;
import com.example.fmdriver.fragments.FragmentSaveToDb;
import com.example.fmdriver.fragments.FragmentToken;


public class FragmentController implements FragmentsNames {

    private Context context;
    private FragmentManager fragmentManager;
    int containerId;
    private int animationEnter;
    private int animationExit;

    //----------------------------------------------------------------------------------------------

    //Konstruktor bez vlastní animace
    public FragmentController(
            Context context,
            FragmentManager fragmentManager,
            int containerId) {

        this.context = context;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    //Konstruktor s vlastní animací
    public FragmentController(
            Context context,
            FragmentManager fragmentManager,
            int containerId,
            int animationEnter,
            int animationExit) {

        this.context = context;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.animationEnter = animationEnter;
        this.animationExit = animationExit;
    }

    //----------------------------------------------------------------------------------------------


    private boolean validateResources() {
        return  (animationEnter != 0 && animationExit != 0);
    }

    public boolean isFragmentCurrent(String name) {
        if (fragmentManager.getBackStackEntryCount() != 0) {
            FragmentManager.BackStackEntry be = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            return be.getName().equals(name);
        }

        return false;
    }

    public Fragment findFragmentByName(String fragmentName) {
        return fragmentManager.findFragmentByTag(fragmentName);
    }

    public void showFragment(String fragmentName, Bundle args, boolean addToBackStack) {
        if (fragmentName == null) return;
        if (fragmentName.equals("")) return;
        if (containerId == 0) return;

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentName);

        if (fragment == null) {
            fragment = getFragment(fragmentName);
            if (fragment == null) return;

            if (args != null) {
                if (!args.isEmpty()) {
                    fragment.setArguments(args);
                }
            }

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (validateResources()) fragmentTransaction.setCustomAnimations(animationEnter, animationExit, animationEnter, animationExit);
            fragmentTransaction.add(containerId, fragment, fragmentName);
            if (addToBackStack) fragmentTransaction.addToBackStack(fragmentName);
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack(fragmentName, 0);
        }
    }

    private Fragment getFragment(String fragmentName) {
        if (fragmentName.equals(FRAGMENT_LOAD)) return new FragmentLoad();
        else if (fragmentName.equals(FRAGMENT_MAP)) return new FragmentMap();
        else if (fragmentName.equals(FRAGMENT_TOKEN)) return new FragmentToken();
        else if (fragmentName.equals(FRAGMENT_SAVE_TO_DB)) return new FragmentSaveToDb();
        else if (fragmentName.equals(FRAGMENT_CHECKED_POSITIONS)) return new FragmentCheckedPositions();
        else if (fragmentName.equals(FRAGMENT_CHECKED_POSITION_DETAIL)) return new FragmentCheckedPositionDetails();
        else if (fragmentName.equals(FRAGMENT_DEVICES)) return new FragmentDevices();
        else if (fragmentName.equals(FRAGMENT_DEVICE_DETAIL)) return new FragmentDeviceDetail();
        return null;
    }
}
