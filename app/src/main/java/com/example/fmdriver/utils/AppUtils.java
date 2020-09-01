package com.example.fmdriver.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.customViews.DialogInfo;
import com.example.fmdriver.objects.DeviceIdentification;
import com.example.fmdriver.objects.MarkerPosition;
import com.example.fmdriver.objects.Position;
import com.example.fmdriver.objects.PositionChecked;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppUtils implements AppConstants {

    public static boolean isOnline(MainActivity activity) {

        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

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
            default:
                return "unknown";
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
            default:
                return "unknown";
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
            counter++;
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
            counter++;
        }

        return toReturn;
    }

    public static int getImageResForBattery(int batteryPercentages, boolean batteryPlugged) {
        int toReturn = R.drawable.ic_battery_unknown;

        if (batteryPercentages >= 0 && batteryPercentages <= 20) {
            if (batteryPlugged) toReturn = R.drawable.ic_battery_charging_20;
            else toReturn = R.drawable.ic_battery_20;
        } else if (batteryPercentages > 20 && batteryPercentages <= 30) {
            if (batteryPlugged) toReturn = R.drawable.ic_battery_charging_30;
            else toReturn = R.drawable.ic_battery_30;
        } else if (batteryPercentages > 30 && batteryPercentages <= 50) {
            if (batteryPlugged) toReturn = R.drawable.ic_battery_charging_50;
            else toReturn = R.drawable.ic_battery_50;
        } else if (batteryPercentages > 50 && batteryPercentages <= 60) {
            if (batteryPlugged) toReturn = R.drawable.ic_battery_charging_60;
            else toReturn = R.drawable.ic_battery_60;
        } else if (batteryPercentages > 60 && batteryPercentages <= 80) {
            if (batteryPlugged) toReturn = R.drawable.ic_battery_charging_80;
            else toReturn = R.drawable.ic_battery_80;
        } else if (batteryPercentages > 80 && batteryPercentages <= 90) {
            if (batteryPlugged) toReturn = R.drawable.ic_battery_charging_90;
            else toReturn = R.drawable.ic_battery_90;
        } else if (batteryPercentages > 90) {
            if (batteryPlugged) toReturn = R.drawable.ic_battery_charging_100;
            else toReturn = R.drawable.ic_battery_100;
        }

        return toReturn;
    }

    public static void showRequestTimerError(MainActivity activity) {
        DialogInfo.createDialog(activity)
                .setTitle("Chyba")
                .setMessage("Nepodařilo se zpraovat požadavek v nastaveném časovém limitu")
                .show();
    }

    public static DeviceIdentification getDeviceIdentification(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = "";

        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            /*
             * getDeviceId() returns the unique device ID.
             * For example,the IMEI for GSM and the MEID or ESN for CDMA phones.
             */
            deviceId = telephonyManager.getDeviceId();

            /*
             * getSubscriberId() returns the unique subscriber ID,
             * For example, the IMSI for a GSM phone.
             */
            String subscriberId = telephonyManager.getSubscriberId();
        }

        return new DeviceIdentification(androidId, deviceId);
    }
}
