package com.example.fmdriver;

import android.arch.lifecycle.Lifecycle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fmdriver.customViews.DialogInfo;
import com.example.fmdriver.fragments.FragmentLoad;
import com.example.fmdriver.fragments.FragmentLoad_;
import com.example.fmdriver.fragments.FragmentMap;
import com.example.fmdriver.fragments.FragmentToken;
import com.example.fmdriver.fragments.FragmentToken_;
import com.example.fmdriver.interfaces.AppPrefs_;
import com.example.fmdriver.listeners.OnCallCanceledListener;
import com.example.fmdriver.listeners.OnFragmentLoadClosedListener;
import com.example.fmdriver.listeners.OnFragmentLoadShowedListener;
import com.example.fmdriver.listeners.OnServiceStatusCheckedListener;
import com.example.fmdriver.objects.NewLocation;
import com.example.fmdriver.retrofit.ApiFcm;
import com.example.fmdriver.retrofit.ControllerFcm;
import com.example.fmdriver.retrofit.objects.RequestToFcmData;
import com.example.fmdriver.retrofit.requests.RequestToFcm;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.AppUtils;
import com.facebook.stetho.Stetho;
import com.google.firebase.messaging.RemoteMessage;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.fmdriver.utils.AppConstants.animHideFragment;
import static com.example.fmdriver.utils.AppConstants.animShowFragment;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements
        AppConstants,
        OnCallCanceledListener, OnServiceStatusCheckedListener {

    @Pref
    public static AppPrefs_ appPrefs;

    @ViewById
    TextView btnMap,
            btnToken,
            btnCheckService,
            btnStartStopService,
            labelGpsStatus,
            labelServiceStatus,
            labelMessage,
            labelBattery;

    public FragmentManager fragmentManager;

    @InstanceState
    public boolean
            isServiceStarted,
            isGetPosition,
            checkingServiceStatusInProgress;

    BroadcastReceiver locacionReceiver;
    BroadcastReceiver serviceStatusCheckedReceiver;
    BroadcastReceiver serviceStartetReceiver;
    BroadcastReceiver serviceStopedReceiver;


    @AfterViews
    void afterViews() {
        fragmentManager = getSupportFragmentManager();
        checkServiceStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, appPrefs.fcmToken().get());
        registerServiceStatusCheckedReceiver();
        registerLocationReceiver();
        registerServiceStartetReceiver();
        registerServiceStopedReceiver();
        initStetho();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterServiceStatusCheckedReceiver();
        unregisterLocationReceiver();
        unregisterServiceStartetReceiver();
        unregisterServiceStopedReceiver();
        sendRequestToFcm(FCM_REQUEST_TYPE_GPS_STOP);
    }

    @Override
    public void onServiceStatusChecked(final int serviceStatus) {
        Log.i(TAG, "MainActivity - onServiceStatusChecked()");

        checkingServiceStatusInProgress = false;

        if (serviceStatus == FCM_RESPONSE_SERVICE_STATUS_STARTED) {
            isServiceStarted = true;
            btnStartStopService.setText("Stop service");
            Toast.makeText(MainActivity.this, "Service started", Toast.LENGTH_LONG).show();
            labelServiceStatus.setAlpha(1f);
            labelMessage.setText("Služba běží");
        } else if (serviceStatus == FCM_RESPONSE_SERVICE_STATUS_STOPED) {
            isServiceStarted = false;
            btnStartStopService.setText("Start service");
            Toast.makeText(MainActivity.this, "Service stoped", Toast.LENGTH_LONG).show();
            labelServiceStatus.setAlpha(0.15f);
            labelMessage.setText("Služba neběží");
        } else {
            DialogInfo.createDialog(MainActivity.this)
                    .setTitle("Chyba")
                    .setMessage("Nepodařilo se zjistit stav service")
                    .show();
        }
    }

    @Override
    public void callCanceled(int taskType) {

    }

    @Click(R.id.btnMap)
    void clickBtnMap() {
        Animators.animateButtonClick(btnMap);
        if (isGetPosition) {
            isGetPosition = false;
            sendRequestToFcm(FCM_REQUEST_TYPE_GPS_STOP);
        } else {
            isGetPosition = true;
            sendRequestToFcm(FCM_REQUEST_TYPE_GPS_START);
        }
    }

    @Click(R.id.btnToken)
    void clickBtnToken() {
        Animators.animateButtonClick(btnToken);
        showFragmentToken(appPrefs.fcmToken().get());
    }

    @Click(R.id.btnCheckService)
    void clickCheckService() {
        Animators.animateButtonClick(btnCheckService);
        sendRequestToFcm(FCM_REQUEST_TYPE_SERVICE_STATUS);
    }

    @Click(R.id.btnStartStopService)
    void clickStartStopService() {
        Animators.animateButtonClick(btnStartStopService);
        sendRequestToFcm(isServiceStarted ? FCM_REQUEST_TYPE_SERVICE_STOP : FCM_REQUEST_TYPE_SERVICE_START);
    }

    public void sendRequestToFcm(int requestType) {

        Log.i(TAG, "sendRequestToFcm()");

        RequestToFcmData requestToFcmData = new RequestToFcmData(MainActivity.appPrefs.fcmToken().get(), requestType);
        RequestToFcm requestToFcm = new RequestToFcm(
                "fFfB73jMSd8:APA91bHLR5xFgmd3wqjYo70x9Ymcq-Ws2yYQGfUYuKdptXFPw759WZF3iYc_wDp_JUHbygOfGaJhWBSfowa-9NLj3fwSGRpVlJAeS66qWc2FgqsZrllBcpq9TIe6SgdZ7YRnglwhpM1I",
                requestToFcmData);

        Log.i(TAG, requestToFcm.toString());

        ApiFcm apiFcm = ControllerFcm.getRetrofitInstance().create(ApiFcm.class);
        Call<ResponseBody> callFcm = apiFcm.sendRequestToFcm(requestToFcm);

        callFcm.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
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

                    closeFragmentLoad(new OnFragmentLoadClosedListener() {
                        @Override
                        public void onFragmentLoadClosed() {
                            try {
                                Log.i(TAG, "response: " + response.errorBody().string());

                                DialogInfo.createDialog(
                                        MainActivity.this)
                                        .setTitle("Chyba")
                                        .setMessage("Chyba: " + response.errorBody().string())
                                        .show();
                            } catch (IOException e) {
                                Log.i(TAG, "response: IOException: ");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "onFailure: ");
                Log.i(TAG, t.getMessage());

                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                    @Override
                    public void onFragmentLoadClosed() {
                        DialogInfo.createDialog(
                                MainActivity.this)
                                .setTitle("Chyba")
                                .setMessage("Chyba: " + "onFailure()")
                                .show();
                    }
                });
            }
        });
    }

    public void checkServiceStatus() {
        Log.i(TAG, "serviceStopedReceiver - checkServiceStatus()");
        checkingServiceStatusInProgress = true;
        sendRequestToFcm(FCM_REQUEST_TYPE_SERVICE_STATUS);
    }

    @UiThread
    public void showFrgmentLoad(String labelText, int taskType, OnFragmentLoadShowedListener listener) {
        Log.i(TAG, "MainActivity - showFrgmentLoad(" + labelText + ")");

        FragmentLoad fragmentLoad = (FragmentLoad) fragmentManager.findFragmentByTag("FragmentLoad_");

        if (fragmentLoad == null) {
            fragmentLoad = FragmentLoad_.builder().labelText(labelText).taskType(taskType).build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentLoad, fragmentLoad.getClass().getSimpleName());
            fragmentTransaction.addToBackStack("FragmentLoad_");
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            //if (beCount == 0) return;
            fragmentManager.popBackStack("FragmentLoad_", 0);
            fragmentLoad.setLabel(labelText);
        }

        if (listener != null) listener.onFragmentLoadShowed();
    }

    @UiThread
    public void closeFragmentLoad(OnFragmentLoadClosedListener listener) {
        if (isFragmentLoadShowed()) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) fragmentManager.popBackStack();
        }

        if (listener != null) listener.onFragmentLoadClosed();
    }

    public boolean isFragmentLoadShowed() {
        int beCount = fragmentManager.getBackStackEntryCount();
        if (beCount == 0) return false;
        return fragmentManager.getBackStackEntryAt(beCount - 1).getName().equals("FragmentLoad_");
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
                updateBatteryStatus(intent.getStringExtra(KEY_BATTERY));
                RemoteMessage remoteMessage = intent.getParcelableExtra(KEY_DATA);

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

                            /*
                            FragmentMap fragmentMap = (FragmentMap) fragmentManager.findFragmentByTag("FragmentMap2");

                            if (fragmentMap == null) showFragmentMap2(newLocation, "tady");
                            else fragmentMap.updateMap(newLocation, "tady");
                            */
                        }
                    }
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(locacionReceiver, new IntentFilter(ACTION_LOCATION_BROADCAST));
    }

    public void registerServiceStatusCheckedReceiver() {
        serviceStatusCheckedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "serviceStatusCheckedReceiver - onReceive()");
                if (intent == null) return;
                updateBatteryStatus(intent.getStringExtra(KEY_BATTERY));
                String response = intent.getStringExtra(KEY_RESPONSE_SERVICE_STATUS);
                int status = 0;

                try {
                    status = Integer.parseInt(response);
                } catch (NumberFormatException e) {
                    Log.i(TAG, "serviceStatusCheckedReceiver - NumberFormatException: " + e.getMessage());
                    return;
                }

                Log.i(TAG, "status == " + AppUtils.responseTypeToString(status));

                MainActivity.this.onServiceStatusChecked(status);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStatusCheckedReceiver, new IntentFilter(ACTION_SERVICE_STATUS_BROADCAST));
    }

    public void registerServiceStartetReceiver() {
        serviceStartetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "serviceStartetReceiver - onReceive()");
                if (intent != null) updateBatteryStatus(intent.getStringExtra(KEY_BATTERY));
                checkServiceStatus();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStartetReceiver, new IntentFilter(ACTION_SERVICE_STARTET_BROADCAST));
    }

    public void registerServiceStopedReceiver() {
        serviceStopedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "serviceStopedReceiver - onReceive()");
                if (intent != null) updateBatteryStatus(intent.getStringExtra(KEY_BATTERY));
                checkServiceStatus();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStopedReceiver, new IntentFilter(ACTION_SERVICE_STOPED_BROADCAST));
    }

    private void unregisterLocationReceiver() {
        Log.i(TAG, "unregisterLocationReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(locacionReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterLocationReceiver(): " + e.getMessage());
        }
    }

    private void unregisterServiceStatusCheckedReceiver() {
        Log.i(TAG, "unregisterServiceStatusCheckedReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStatusCheckedReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterServiceStatusCheckedReceiver(): " + e.getMessage());
        }
    }

    private void unregisterServiceStartetReceiver() {
        Log.i(TAG, "unregisterServiceStartetReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStartetReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterServiceStartetReceiver(): " + e.getMessage());
        }
    }

    private void unregisterServiceStopedReceiver() {
        Log.i(TAG, "unregisterServiceStopedReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceStopedReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterServiceStopedReceiver(): " + e.getMessage());
        }
    }

    private void updateBatteryStatus(String status) {
        if (status == null) labelBattery.setText("???");
        else labelBattery.setText("" + status + "%");
    }

    private void initStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }
}
