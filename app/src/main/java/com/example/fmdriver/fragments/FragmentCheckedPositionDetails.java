package com.example.fmdriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.objects.PositionChecked;


public class FragmentCheckedPositionDetails extends Fragment {

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
    Bundle args;
    PositionChecked positionChecked;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checked_positions_details, container, false);
        args = getArguments();
        if (args != null) positionChecked = args.getParcelable("positionChecked");

        labelId = (TextView) rootView.findViewById(R.id.labelId);
        labelCountryCode = (TextView) rootView.findViewById(R.id.labelCountryCode);
        labelCountryName = (TextView) rootView.findViewById(R.id.labelCountryName);
        labelFeatureName = (TextView) rootView.findViewById(R.id.labelFeatureName);
        labelLocality = (TextView) rootView.findViewById(R.id.labelLocality);
        labelPhone = (TextView) rootView.findViewById(R.id.labelPhone);
        labelPostalCode = (TextView) rootView.findViewById(R.id.labelPostalCode);
        labelPremises = (TextView) rootView.findViewById(R.id.labelPremises);
        labelAdminArea = (TextView) rootView.findViewById(R.id.labelAdminArea);
        labelSubAdminArea = (TextView) rootView.findViewById(R.id.labelSubAdminArea);
        labelSubLocality = (TextView) rootView.findViewById(R.id.labelSubLocality);
        labelThoroughfare = (TextView) rootView.findViewById(R.id.labelThoroughfare);
        labelSubThoroughfareity = (TextView) rootView.findViewById(R.id.labelSubThoroughfareity);
        labelUrl = (TextView) rootView.findViewById(R.id.labelUrl);
        labelExtras = (TextView) rootView.findViewById(R.id.labelExtras);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews();
    }

    private void setViews() {
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
