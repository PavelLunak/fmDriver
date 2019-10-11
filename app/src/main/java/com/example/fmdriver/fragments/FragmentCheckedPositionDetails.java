package com.example.fmdriver.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.objects.PositionChecked;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;


@EFragment(R.layout.fragment_checked_positions_details)
public class FragmentCheckedPositionDetails extends Fragment {

    @ViewById
    TextView
            labelId,
            labelCountryCode,
            labelCountryName,
            labelFeatureName,
            labelLocality,
            labelPhone,
            labelPostalCode,
            labelPremises,
            labelAdminArea,
            labelSubAdminArea,
            labelSubLocality,
            labelThoroughfare,
            labelSubThoroughfareity,
            labelUrl,
            labelExtras;

    MainActivity activity;

    @FragmentArg
    PositionChecked positionChecked;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @AfterViews
    void afterViews() {
        if (positionChecked == null) return;

        labelId.setText("" + positionChecked.getId());
        labelCountryCode.setText(positionChecked.getCountryCode());
        labelCountryName.setText(positionChecked.getCountryName());
        labelFeatureName.setText(positionChecked.getFeatureName());
        labelLocality.setText(positionChecked.getLocality());
        labelPhone.setText(positionChecked.getPhone());
        labelPostalCode.setText(positionChecked.getPostalCode());
        labelPremises.setText(positionChecked.getPremises());
        labelAdminArea.setText(positionChecked.getAdminArea());
        labelSubAdminArea.setText(positionChecked.getSubAdminArea());
        labelSubLocality.setText(positionChecked.getSubLocality());
        labelThoroughfare.setText(positionChecked.getThoroughfare());
        labelSubThoroughfareity.setText(positionChecked.getSubThoroughfare());
        labelUrl.setText(positionChecked.getUrl());
        labelExtras.setText(positionChecked.getExtras() != null ? positionChecked.getExtras().toString() : "Extras == NULL");
    }
}
