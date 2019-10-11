package com.example.fmdriver.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentManager;

import com.example.fmdriver.objects.MarkerPosition;
import com.example.fmdriver.objects.Position;
import com.example.fmdriver.objects.PositionChecked;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppUtils implements AppConstants {

    public static boolean isFragmentCurrent(String name, FragmentManager fragmentManager) {
        if (fragmentManager.getBackStackEntryCount() != 0) {
            FragmentManager.BackStackEntry be = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            return be.getName().equals(name);
        }
        return false;
    }

    public static String responseTypeToString(int responseType) {
        switch (responseType) {
            case FCM_RESPONSE_SERVICE_STATUS_STARTED:
                return "FCM_RESPONSE_SERVICE_STATUS_STARTED";
            case FCM_RESPONSE_SERVICE_STATUS_STOPED:
                return "FCM_RESPONSE_SERVICE_STATUS_STOPED";
            case FCM_RESPONSE_GPS_START:
                return "FCM_RESPONSE_GPS_START";
            case FCM_RESPONSE_GPS_STOP:
                return "FCM_RESPONSE_GPS_STOP";
            case FCM_RESPONSE_TYPE_LOCATION:
                return "FCM_RESPONSE_TYPE_LOCATION";
            case FCM_RESPONSE_TYPE_LOCATION_DISABLED:
                return "FCM_RESPONSE_TYPE_LOCATION_DISABLED";
            case FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVED:
                return "FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVED";
            case FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVE_ERROR:
                return "FCM_RESPONSE_TYPE_SETTINGS_DATABASE_SAVE_ERROR";
            case FCM_RESPONSE_TYPE_SETTINGS_LOADED:
                return "FCM_RESPONSE_TYPE_SETTINGS_LOADED";
            case FCM_RESPONSE_TYPE_MESSAGE:
                return "FCM_RESPONSE_TYPE_MESSAGE";
            case FCM_RESPONSE_SERVICE_STATUS:
                return "FCM_RESPONSE_SERVICE_STATUS";
            default: return "unknown";
        }
    }

    public static String requestTypeToString(int requestType) {
        switch (requestType) {
            case FCM_REQUEST_TYPE_SERVICE_STATUS:
                return "FCM_REQUEST_TYPE_SERVICE_STATUS";
            case FCM_REQUEST_TYPE_SERVICE_START:
                return "FCM_REQUEST_TYPE_SERVICE_START";
            case FCM_REQUEST_TYPE_SERVICE_STOP:
                return "FCM_REQUEST_TYPE_SERVICE_STOP";
            case FCM_REQUEST_TYPE_GPS_START:
                return "FCM_REQUEST_TYPE_GPS_START";
            case FCM_REQUEST_TYPE_GPS_STOP:
                return "FCM_REQUEST_TYPE_GPS_STOP";
            case FCM_REQUEST_TYPE_LOCATION:
                return "FCM_REQUEST_TYPE_LOCATION";
            case FCM_REQUEST_TYPE_SETTINGS_DATABASE:
                return "FCM_REQUEST_TYPE_SETTINGS_DATABASE";
            case FCM_REQUEST_TYPE_LOAD_SETTINGS:
                return "FCM_REQUEST_TYPE_LOAD_SETTINGS";
            case FCM_REQUEST_TYPE_ALARM:
                return "FCM_REQUEST_TYPE_ALARM";
            case FCM_REQUEST_TYPE_CALL:
                return "FCM_REQUEST_TYPE_CALL";
            default: return "unknown";
        }
    }

    public static Address getAddressForLocation(Context context, double la, double lo) {

        try {
            double latitude = la;
            double longitude = lo;
            int maxResults = 1;

            Geocoder gc = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

            if (addresses.size() == 1) {
                return addresses.get(0);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String addressToString(Address address) {
        StringBuilder sb = new StringBuilder("Adresa: ");

        if (address == null) return sb.append("null").toString();

        if (address.getThoroughfare() != null) {
            sb.append(address.getThoroughfare() + " ");
            sb.append(address.getFeatureName() != null ? (address.getFeatureName() + ", ") : "?, ");
        } else if (address.getLocality() != null) {
            sb.append(address.getLocality());
            if (address.getFeatureName() != null) sb.append(address.getFeatureName());
        }

        if (address.getLocality() != null) {
            sb.append(", " + address.getLocality());
        } else if (address.getSubAdminArea() != null) {
            sb.append(", " + address.getSubAdminArea());
        } else if (address.getAdminArea() != null) {
            sb.append(", " + address.getAdminArea());
        }

        if (sb.toString().equals("")) return "? ? ?";
        return sb.toString();
    }

    public static ArrayList<MarkerPosition> prepareMarkersFromPositionsChecked(ArrayList<PositionChecked> items) {
        ArrayList<MarkerPosition> toReturn = new ArrayList<>();

        if (items == null) return toReturn;
        if (items.isEmpty()) return toReturn;

        int counter = 1;

        StringBuilder sb;

        for (PositionChecked p : items) {
            sb = new StringBuilder();
            sb.append(DateTimeUtils.getDate(p.getDate()));
            sb.append("\n");
            sb.append(DateTimeUtils.getTime(new Date(p.getDate())));
            sb.append("\n");
            sb.append(p.getSpeed());
            sb.append("km/h");


            toReturn.add(new MarkerPosition(p.getLatitude(), p.getLongitude(), "" + counter, sb.toString()));
            counter ++;
        }

        return toReturn;
    }

    public static ArrayList<MarkerPosition> prepareMarkersFromPositionChecked(double latitude, double longitude, String name, int radius) {
        ArrayList<MarkerPosition> toReturn = new ArrayList<>();
        toReturn.add(new MarkerPosition(latitude, longitude, name, radius));
        return toReturn;
    }

    public static ArrayList<MarkerPosition> prepareMarkersFromPositions(ArrayList<Position> items) {
        ArrayList<MarkerPosition> toReturn = new ArrayList<>();

        if (items == null) return toReturn;
        if (items.isEmpty()) return toReturn;

        int counter = 1;

        for (Position p : items) {
            toReturn.add(new MarkerPosition(p.getLatitude(), p.getLongitude(), "" + counter));
            counter ++;
        }

        return toReturn;
    }
}
