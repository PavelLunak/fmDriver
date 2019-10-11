package com.example.fmdriver.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.objects.NewLocation;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.DateTimeUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class FragmentMap extends Fragment implements AppConstants {

    TextView labelMapType,
            labelDate,
            labelLat,
            labelLong,
            labelAccuracy,
            labelSpeed;

    ImageView imgZoomPlus, imgZoomMinus, imgGetPosition;

    MainActivity activity;

    private NewLocation location;
    private String marker = "null";

    int zoom = 15;
    int mapType = GoogleMap.MAP_TYPE_NORMAL;

    MapView mMapView;
    private GoogleMap googleMap;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateImgGetLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_map, container, false);

        Bundle args = getArguments();
        location = args.getParcelable("location");
        marker = args.getString("marker");

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        imgZoomPlus = (ImageView) rootView.findViewById(R.id.imgZoomPlus);
        imgZoomMinus = (ImageView) rootView.findViewById(R.id.imgZoomMinus);
        imgGetPosition = (ImageView) rootView.findViewById(R.id.imgGetPosition);

        labelMapType = (TextView) rootView.findViewById(R.id.labelMapType);
        labelDate = (TextView) rootView.findViewById(R.id.labelDate);
        labelLat = (TextView) rootView.findViewById(R.id.labelLat);
        labelLong = (TextView) rootView.findViewById(R.id.labelLong);
        labelAccuracy = (TextView) rootView.findViewById(R.id.labelAccuracy);
        labelSpeed = (TextView) rootView.findViewById(R.id.labelSpeed);

        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        imgZoomPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animators.animateButtonClick(imgZoomPlus);
                if (mMapView != null) {
                    if (zoom < googleMap.getMaxZoomLevel()) {
                        zoom += 1;
                        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
                    }
                }
            }
        });

        imgZoomMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animators.animateButtonClick(imgZoomMinus);
                if (googleMap != null) {
                    if (zoom > googleMap.getMinZoomLevel()) {
                        zoom -= 1;
                        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
                    }
                }
            }
        });

        /*
        imgGetPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animators.animateButtonClick(imgZoomMinus);

                if (activity.isGetPosition) {
                    activity.isGetPosition = false;
                    activity.sendRequestToFcm(FCM_REQUEST_TYPE_GPS_STOP, true);
                } else {
                    activity.isGetPosition = true;
                    activity.sendRequestToFcm(FCM_REQUEST_TYPE_GPS_START, true);
                }

                updateImgGetLocation();
            }
        });
        */

        labelMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animators.animateButtonClick(labelMapType);
                if (googleMap != null) {
                    if (mapType == GoogleMap.MAP_TYPE_NORMAL) {
                        mapType = GoogleMap.MAP_TYPE_SATELLITE;
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        labelMapType.setText("NORMÁLNÍ");
                    } else {
                        mapType = GoogleMap.MAP_TYPE_NORMAL;
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        labelMapType.setText("SATELITNÍ");
                    }
                }
            }
        });

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (checkLocationPermission()) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        googleMap.setMyLocationEnabled(true);
                    }
                }

                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);

                updateMap(location, marker);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        //if (activity.isGetPosition) activity.sendRequestToFcm(FCM_REQUEST_TYPE_GPS_START, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        //activity.sendRequestToFcm(FCM_REQUEST_TYPE_GPS_STOP, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void updateMap(NewLocation location, String marker) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(pos).title(marker));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(pos).zoom(googleMap.getMaxZoomLevel()).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (location.getAccuracy() > 0) {
            CircleOptions mCircleOptions = new CircleOptions().fillColor(Color.argb(97, 93, 185, 139)).strokeColor(Color.argb(200, 93, 185, 139));
            googleMap.addCircle(mCircleOptions.center(pos).radius(location.getAccuracy()));
        }

        updateData(location);
    }

    public void updateImgGetLocation() {
        if (activity.isGpsStarted) imgGetPosition.setImageDrawable(activity.getResources().getDrawable(R.drawable.pause));
        else imgGetPosition.setImageDrawable(activity.getResources().getDrawable(R.drawable.play));
    }

    public void updateData(NewLocation location) {
        labelDate.setText("Datum: " + DateTimeUtils.getDateTime(location.getDate()));
        labelLat.setText("Lat: " + location.getLatitude());
        labelLong.setText("Long: " + location.getLongitude());
        labelAccuracy.setText("Přesnost: " + location.getAccuracy() + "m");
        labelSpeed.setText("Rychlost: " + location.getSpeed() + "Km/h");
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),new String[]
                                        {Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        googleMap.setMyLocationEnabled(true);
                    }

                } else {

                }
                return;
            }
        }
    }
}
