package com.example.fmdriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.util.Log;

import com.example.fmdriver.fragments.FragmentMap;
import com.example.fmdriver.fragments.FragmentToken;
import com.example.fmdriver.fragments.FragmentToken_;
import com.example.fmdriver.interfaces.AppPrefs_;
import com.example.fmdriver.objects.NewLocation;
import com.example.fmdriver.retrofit.ApiFcm;
import com.example.fmdriver.retrofit.ControllerFcm;
import com.example.fmdriver.retrofit.objects.RequestPositionData;
import com.example.fmdriver.retrofit.requests.RequestPosition;
import com.example.fmdriver.utils.AppConstants;
import com.google.firebase.messaging.RemoteMessage;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.fmdriver.utils.AppConstants.animHideFragment;
import static com.example.fmdriver.utils.AppConstants.animShowFragment;

@EActivity
public class MainActivity extends AppCompatActivity implements AppConstants {

    @Pref
    public static AppPrefs_ appPrefs;

    public FragmentManager fragmentManager;

    @InstanceState
    public boolean isGetPosition;

    BroadcastReceiver locacionReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerLocationReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterLocationReceiver();
    }

    @Click(R.id.btnMap)
    void clickBtnMap() {
        sendRequestLocation(REQUEST_TYPE_START);
    }

    @Click(R.id.btnToken)
    void clickBtnToken() {
        showFragmentToken(appPrefs.fcmToken().get());
    }

    public void sendRequestLocation(int requestType) {

        RequestPositionData requestPositionData = new RequestPositionData(MainActivity.appPrefs.fcmToken().get(), requestType);
        RequestPosition requestPosition = new RequestPosition("token", requestPositionData);

        ApiFcm apiFcm = ControllerFcm.getRetrofitInstance().create(ApiFcm.class);
        Call<ResponseBody> callFcm = apiFcm.sendRequestLocation(requestPosition);

        callFcm.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "response code: " + response.code());
                if (response.isSuccessful()) {
                    Log.i(TAG, "isSuccessful: TRUE");

                    try {
                        Log.i(TAG, "response: " + response.body().string());
                    } catch (IOException e) {
                        Log.i(TAG, "response: IOException: ");
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "isSuccessful: FALSE");

                    try {
                        Log.i(TAG, "response: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.i(TAG, "response: IOException: ");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure: ");
                Log.i(TAG, t.getMessage());
            }
        });
    }

    public void showFragmentMap2(NewLocation newLocation, String marker) {
        FragmentMap fragmentMap = (FragmentMap) fragmentManager.findFragmentByTag("FragmentMap2");

        if (fragmentMap == null) {
            fragmentMap = new FragmentMap();

            Bundle args = new Bundle();
            args.putParcelable("location", newLocation);
            args.putString("marker", marker);
            fragmentMap.setArguments(args);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentMap, "FragmentMap2");
            fragmentTransaction.addToBackStack("FragmentMap2");
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack("FragmentMap2", 0);
        }
    }

    public void showFragmentToken(String token) {
        FragmentToken fragmentToken = (FragmentToken) fragmentManager.findFragmentByTag("FragmentToken_");

        if (fragmentToken == null) {
            fragmentToken = FragmentToken_.builder().token(token).build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentToken, "FragmentToken_");
            fragmentTransaction.addToBackStack("FragmentToken_");
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack("FragmentToken_", 0);
        }
    }

    private void registerLocationReceiver() {
        Log.i(TAG, "registerLocationReceiver()");

        locacionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;

                RemoteMessage remoteMessage = intent.getParcelableExtra("data");

                if (remoteMessage != null) {
                    Map<String, String> data = remoteMessage.getData();
                    if (data != null) {
                        if (!data.isEmpty()) {
                            NewLocation newLocation = new NewLocation();

                            try {
                                if (data.containsKey("date")) newLocation.setAccuracy(Long.parseLong(data.get("date")));
                                if (data.containsKey("latitude")) newLocation.setLatitude(Double.parseDouble(data.get("latitude")));
                                if (data.containsKey("longitude")) newLocation.setLongitude(Double.parseDouble(data.get("longitude")));
                                if (data.containsKey("accuracy")) newLocation.setAccuracy(Float.parseFloat(data.get("accuracy")));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            FragmentMap fragmentMap = (FragmentMap) fragmentManager.findFragmentByTag("FragmentMap2");

                            if (fragmentMap == null) showFragmentMap2(newLocation, "tady");
                            else fragmentMap.updateMap(newLocation, "tady");
                        }
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(locacionReceiver, new IntentFilter(ACTION_LOCATION_BROADCAST));
    }

    private void unregisterLocationReceiver() {
        Log.i(TAG, "unregisterLocationReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(locacionReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterLocationReceiver(): " + e.getMessage());
        }
    }
}
