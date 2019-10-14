package com.example.fmdriver;

import android.arch.lifecycle.Lifecycle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fmdriver.adapters.AdapterLog;
import com.example.fmdriver.customViews.DialogInfo;
import com.example.fmdriver.customViews.DialogInput;
import com.example.fmdriver.fragments.FragmentCheckedPositionDetails;
import com.example.fmdriver.fragments.FragmentCheckedPositionDetails_;
import com.example.fmdriver.fragments.FragmentCheckedPositions;
import com.example.fmdriver.fragments.FragmentCheckedPositions_;
import com.example.fmdriver.fragments.FragmentLoad;
import com.example.fmdriver.fragments.FragmentLoad_;
import com.example.fmdriver.fragments.FragmentMap;
import com.example.fmdriver.fragments.FragmentSaveToDb;
import com.example.fmdriver.fragments.FragmentSaveToDb_;
import com.example.fmdriver.fragments.FragmentToken;
import com.example.fmdriver.fragments.FragmentToken_;
import com.example.fmdriver.interfaces.AppPrefs_;
import com.example.fmdriver.listeners.OnAllCheckedPositionsLoadedListener;
import com.example.fmdriver.listeners.OnAllItemsDeletedListener;
import com.example.fmdriver.listeners.OnCallCanceledListener;
import com.example.fmdriver.listeners.OnFragmentLoadClosedListener;
import com.example.fmdriver.listeners.OnFragmentLoadShowedListener;
import com.example.fmdriver.listeners.OnInputInsertedListener;
import com.example.fmdriver.listeners.OnServiceStatusCheckedListener;
import com.example.fmdriver.objects.AppLog;
import com.example.fmdriver.objects.ItemLog;
import com.example.fmdriver.objects.NewLocation;
import com.example.fmdriver.objects.PositionChecked;
import com.example.fmdriver.retrofit.ApiDatabase;
import com.example.fmdriver.retrofit.ApiFcm;
import com.example.fmdriver.retrofit.ControllerDatabase;
import com.example.fmdriver.retrofit.ControllerFcm;
import com.example.fmdriver.retrofit.objects.RequestToFcmCall;
import com.example.fmdriver.retrofit.objects.RequestToFcmData;
import com.example.fmdriver.retrofit.requests.RequestToFcm;
import com.example.fmdriver.retrofit.responses.ResponseAllCheckedPositions;
import com.example.fmdriver.retrofit.responses.ResponseCheckedPosition;
import com.example.fmdriver.retrofit.responses.ResponseDeletePosition;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.AppUtils;
import com.example.fmdriver.utils.DateTimeUtils;
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
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements
        AppConstants,
        OnCallCanceledListener,
        OnServiceStatusCheckedListener {

    @Pref
    public static AppPrefs_ appPrefs;

    @ViewById
    TextView
            labelGpsStatus,
            labelServiceStatus,
            labelMessage,
            labelBattery,
            labelCountDownService,
            labelCountDownGps,
            labelLastGpsUpdatet,
            labelAddress;

    @ViewById
    ImageView imgBattery,
            imgMap,
            imgToken,
            imgCheckService,
            imgStartStopService,
            imgStartStopGps,
            imgSettings,
            imgShowData,
            imgAlarm,
            imgCall;

    @ViewById
    ProgressBar progressService, progressGps;

    @ViewById
    RecyclerView recyclerViewLog;

    public CountDownTimer serviceTimer, gpsTimer, settingsTimer, locationResultTimer;

    @InstanceState
    boolean isWaitingForResponseFromFcm;

    public FragmentManager fragmentManager;

    @InstanceState
    public boolean
            isServiceStarted,
            isGpsStarted,
            checkingServiceStatusInProgress,
            isRequestStartGps;

    @InstanceState
    public static ArrayList<PositionChecked> itemsCheckedPositions;

    BroadcastReceiver locacionReceiver;
    BroadcastReceiver serviceStatusCheckedReceiver;
    BroadcastReceiver databaseSettingsUpdatedReceiver;
    BroadcastReceiver databaseSettingsLoadedReceiver;
    BroadcastReceiver messageReceiver;

    @InstanceState
    long serviceCountDownCounter = -1;

    @InstanceState
    long gpsCountDownCounter = -1;

    @InstanceState
    long settingsCountDownCounter = -1;

    @InstanceState
    long locationResultCountDownCounter = -1;

    @InstanceState
    public AppLog appLog;

    AdapterLog adapterLog;


    @AfterViews
    void afterViews() {
        fragmentManager = getSupportFragmentManager();
        if (!checkingServiceStatusInProgress) checkServiceStatus();

        if (serviceCountDownCounter != -1) {
            labelCountDownService.setVisibility(View.VISIBLE);
            progressService.setVisibility(View.VISIBLE);
        }
        if (gpsCountDownCounter != -1) {
            labelCountDownGps.setVisibility(View.VISIBLE);
            progressGps.setVisibility(View.VISIBLE);
        }

        initLog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, appPrefs.fcmToken().get());
        registerServiceStatusCheckedReceiver();
        registerLocationReceiver();
        registerDatabaseSettingsUpdatedReceiver();
        registerDatabaseSettingsLoadedReceiver();
        registerMessageReceiver();

        registerLocationProvidersChangedStatusReceiver();

        initStetho();

        if (serviceCountDownCounter != -1) startCountDownServiceConnect();
        //if (gpsCountDownCounter != -1) startCountDownGpsStart(isRequestStartGps);
        if (locationResultCountDownCounter != -1) startCountDownLocationResult();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterServiceStatusCheckedReceiver();
        unregisterLocationReceiver();
        unregisterDatabaseSettingsUpdatedReceiver();
        unregisterDatabaseSettingsLoadedReceiver();
        unregisterMessageReceiver();

        unregisterLocationProvidersChangedStatusReceiver();

        stopCountDownServiceConnect();
        stopCountDownGpsConnect();
        stopCountDownSettings();
    }

    @Override
    public void onBackPressed() {
        if (AppUtils.isFragmentCurrent("FragmentLoad_", fragmentManager)) return;
        super.onBackPressed();
    }

    @Override
    public void onServiceStatusChecked(final int serviceStatus, final int gpsStatus) {
        Log.i(TAG, "MainActivity - onServiceStatusChecked()");
        Log.i(TAG, "MainActivity - onServiceStatusChecked() - serviceStatus: " + serviceStatus);
        Log.i(TAG, "MainActivity - onServiceStatusChecked() - gpsStatus: " + gpsStatus);

        checkingServiceStatusInProgress = false;
        //afterServiceStatusChanged(serviceStatus == STARTED);

        if (serviceStatus == STARTED) {
            isServiceStarted = true;
            imgStartStopService.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_green));
            labelServiceStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);
        } else if (serviceStatus == STOPED) {
            isServiceStarted = false;
            imgStartStopService.setImageDrawable(getResources().getDrawable(R.drawable.ic_power));
            labelServiceStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        } else {
            DialogInfo.createDialog(MainActivity.this)
                    .setTitle("Chyba")
                    .setMessage("Nepodařilo se zjistit stav service")
                    .show();
        }

        if (gpsStatus == STARTED) {
            isGpsStarted = true;
            imgStartStopGps.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_green));
            labelGpsStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);
        } else if (gpsStatus == STOPED) {
            isGpsStarted = false;
            imgStartStopGps.setImageDrawable(getResources().getDrawable(R.drawable.ic_location));
            labelGpsStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        } else {
            DialogInfo.createDialog(MainActivity.this)
                    .setTitle("Chyba")
                    .setMessage("Nepodařilo se zjistit stav GPS")
                    .show();
        }
    }

    @Override
    public void callCanceled(int taskType) {

    }

    public void appendLog(String log, boolean show) {
        Log.i(TAG, log);
        if (show) updateAppLog(log);
    }

    private void initLog() {
        if (this.appLog == null) this.appLog = new AppLog();
        if (this.adapterLog == null) this.adapterLog = new AdapterLog(this);
        recyclerViewLog.setAdapter(this.adapterLog);
        recyclerViewLog.setLayoutManager(new LinearLayoutManager(this));
        if (this.appLog.getItemsCount() > 0) recyclerViewLog.scrollToPosition(this.adapterLog.getItemCount() - 1);
    }

    public void updateAppLog(String log) {
        if (this.adapterLog == null) initLog();
        if (this.appLog == null) return;
        this.appLog.addLog(log);
        this.adapterLog.notifyDataSetChanged();
        recyclerViewLog.scrollToPosition(this.adapterLog.getItemCount() - 1);
    }

    @Click(R.id.imgMap)
    void clickBtnMap() {
        if (locationResultTimer != null) return;
        Animators.animateButtonClick(imgMap);
        startCountDownGpsStart(false);
        sendRequestToFcm(FCM_REQUEST_TYPE_LOCATION, true, null);

        /*
        if (isGetPosition) {
            labelGpsStatus.setAlpha(ALPHA_VISIBILITY_GONE);
            isGetPosition = false;
            sendRequestToFcm(FCM_REQUEST_TYPE_GPS_STOP, false);
        } else {
            labelGpsStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);
            isGetPosition = true;
            sendRequestToFcm(FCM_REQUEST_TYPE_GPS_START, false);
        }
        */
    }

    @Click(R.id.imgToken)
    void clickBtnToken() {
        Animators.animateButtonClick(imgToken);
        showFragmentToken(appPrefs.fcmToken().get());
    }

    @Click(R.id.imgCheckService)
    void clickCheckService() {
        if (serviceTimer != null) return;
        Animators.animateButtonClick(imgCheckService);
        startCountDownServiceConnect();
        sendRequestToFcm(FCM_REQUEST_TYPE_SERVICE_STATUS, false, null);
    }

    @Click(R.id.imgStartStopService)
    void clickStartStopService() {
        if (serviceTimer != null) return;
        Animators.animateButtonClick(imgStartStopService);
        startCountDownServiceConnect();
        sendRequestToFcm(isServiceStarted ? FCM_REQUEST_TYPE_SERVICE_STOP : FCM_REQUEST_TYPE_SERVICE_START, true, null);
    }

    @Click(R.id.imgStartStopGps)
    void clickBtnStartStopGps() {
        if (gpsTimer != null) return;
        Animators.animateButtonClick(imgStartStopGps);
        isRequestStartGps = true;
        startCountDownGpsStart(true);
        sendRequestToFcm(isGpsStarted ? FCM_REQUEST_TYPE_GPS_STOP : FCM_REQUEST_TYPE_GPS_START, true, null);
    }

    /*
    @Click(R.id.btnCheckGps)
    void clickBtnCheckService() {
        Animators.animateButtonClick(btnCheckGps);
    }
    */

    @Click(R.id.imgSettings)
    void clickSaveToDb() {
        Animators.animateButtonClick(imgSettings);
        showFragmentSaveToDb();
    }

    @Click(R.id.imgShowData)
    void clickShowData() {
        Animators.animateButtonClick(imgShowData);
        getAllCheckedPositions(null);
    }

    @Click(R.id.imgAlarm)
    void clickBtnAlarm() {
        if (gpsTimer != null) return;
        if (serviceTimer != null) return;

        Animators.animateButtonClick(imgAlarm);
        sendRequestToFcm(FCM_REQUEST_TYPE_ALARM, false, null);
    }

    @Click(R.id.imgCall)
    void clickBtnCall() {
        if (gpsTimer != null) return;
        if (serviceTimer != null) return;

        Animators.animateButtonClick(imgCall);

        DialogInput.createDialog(this)
                .setTitle("Telefon")
                .setMessage("Telefonní číslo, na které bude zavoláno:")
                .setInput(appPrefs.lastPhoneNumber().exists() ? appPrefs.lastPhoneNumber().get() : "")
                .setListenerInput(new OnInputInsertedListener() {
                    @Override
                    public void onInputInserted(String input) {
                        String validatedInput = validatePhoneNumber(input);

                        if (validatedInput == null) {
                            appPrefs.edit().lastPhoneNumber().put(input).apply();
                            RequestToFcmCall data = new RequestToFcmCall(appPrefs.fcmToken().get(), FCM_REQUEST_TYPE_CALL, input);
                            sendRequestToFcm(FCM_REQUEST_TYPE_CALL, true, data);
                        } else {
                            DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage(validatedInput).show();
                        }
                    }
                }).show();
    }

    public String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null) {
            if (phoneNumber.length() >= 6) {
                if (Character.isDigit(phoneNumber.charAt(3))) {
                    return null;
                } else {
                    return "Character.isDigit(phoneNumber.indexOf(3)) == FALSE";
                }
            } else {
                return "phoneNumber.length() >= 6 == FALSE";
            }
        } else {
            return "phoneNumber == null";
        }
    }

    @Click(R.id.labelServiceStatus)
    void clickLabelServiceStatus() {
        /*
        if (serviceTimer == null) {
            labelCountDownService.setVisibility(View.VISIBLE);
            progressService.setVisibility(View.VISIBLE);
            startCountDownServiceConnect();
        } else {
            serviceTimer.cancel();
            serviceTimer = null;
            labelCountDownService.setVisibility(View.GONE);
            progressService.setVisibility(View.GONE);
            serviceCountDownCounter = -1;
        }
        */
    }

    @Click(R.id.labelGpsStatus)
    void clickLabelGpsStatus() {
        /*
        if (gpsTimer == null) {
            labelCountDownGps.setVisibility(View.VISIBLE);
            progressGps.setVisibility(View.VISIBLE);
            startCountDownGpsStart();
        } else {
            gpsTimer.cancel();
            gpsTimer = null;
            labelCountDownGps.setVisibility(View.GONE);
            progressGps.setVisibility(View.GONE);
            gpsCountDownCounter = -1;
        }
        */
    }

    public void sendRequestToFcm(int requestType, boolean blockMultiRequest, RequestToFcmData requestToFcmData) {

        //Log.i(TAG, "sendRequestToFcm()");
        appendLog("sendRequestToFcm: " + AppUtils.requestTypeToString(requestType), true);

        if (blockMultiRequest) {
            if (isWaitingForResponseFromFcm) {
                DialogInfo.createDialog(this)
                        .setMessage("Info")
                        .setMessage("Čekám na odpověď serveru. Do té doby není možné odesílat další požadavky")
                        .show();
                return;
            }
        }

        //isWaitingForResponseFromFcm = true;

        if (requestToFcmData == null) {
            requestToFcmData = new RequestToFcmData(MainActivity.appPrefs.fcmToken().get(), requestType);
        }

        RequestToFcm requestToFcm = new RequestToFcm(
                "fFfB73jMSd8:APA91bHLR5xFgmd3wqjYo70x9Ymcq-Ws2yYQGfUYuKdptXFPw759WZF3iYc_wDp_JUHbygOfGaJhWBSfowa-9NLj3fwSGRpVlJAeS66qWc2FgqsZrllBcpq9TIe6SgdZ7YRnglwhpM1I",
                requestToFcmData);

        Log.i(TAG, requestToFcm.toString());

        ApiFcm apiFcm = ControllerFcm.getRetrofitInstance().create(ApiFcm.class);
        Call<ResponseBody> callFcm = apiFcm.sendRequestToFcm(requestToFcm);

        callFcm.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                //Log.i(TAG, "response code: " + response.code());
                appendLog("response code: " + response.code(), true);
                if (response.isSuccessful()) {
                    Log.i(TAG, "isSuccessful: TRUE");
                    appendLog("...isSuccessful", true);

                    try {
                        Log.i(TAG, "response: " + response.body().string());
                    } catch (IOException e) {
                        Log.i(TAG, "response: IOException: ");
                        e.printStackTrace();
                    }
                } else {
                    //Log.i(TAG, "isSuccessful: FALSE");

                    closeFragmentLoad(new OnFragmentLoadClosedListener() {
                        @Override
                        public void onFragmentLoadClosed() {
                            try {
                                //Log.i(TAG, "response: " + response.errorBody().string());
                                appendLog("...failure: " + response.errorBody().toString(), true);

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
        startCountDownServiceConnect();
        sendRequestToFcm(FCM_REQUEST_TYPE_SERVICE_STATUS, true, null);
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
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED))
                fragmentManager.popBackStack();
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

    public void showFragmentSaveToDb() {
        FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) fragmentManager.findFragmentByTag("FragmentSaveToDb_");

        if (fragmentSaveToDb == null) {
            fragmentSaveToDb = FragmentSaveToDb_.builder().build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentSaveToDb, "FragmentSaveToDb_");
            fragmentTransaction.addToBackStack("FragmentSaveToDb_");
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack("FragmentSaveToDb_", 0);
        }
    }

    public void showCheckedPositions() {

        FragmentCheckedPositions fragmentCheckedPositions = (FragmentCheckedPositions) fragmentManager.findFragmentByTag("FragmentCheckedPositions_");

        if (fragmentCheckedPositions == null) {
            fragmentCheckedPositions = FragmentCheckedPositions_.builder().items(itemsCheckedPositions).build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentCheckedPositions, "FragmentCheckedPositions_");
            fragmentTransaction.addToBackStack("FragmentCheckedPositions_");
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack("FragmentCheckedPositions_", 0);
            fragmentCheckedPositions.updateFragment(new ArrayList<PositionChecked>(null));
        }
    }

    public void showCheckedPositionDetails(PositionChecked positionChecked) {
        FragmentCheckedPositionDetails fragmentCheckedPositionDetails = (FragmentCheckedPositionDetails) fragmentManager.findFragmentByTag("FragmentCheckedPositionDetails_");

        if (fragmentCheckedPositionDetails == null) {
            fragmentCheckedPositionDetails = FragmentCheckedPositionDetails_.builder().positionChecked(positionChecked).build();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentCheckedPositionDetails, "FragmentCheckedPositionDetails_");
            fragmentTransaction.addToBackStack("FragmentCheckedPositionDetails_");
            fragmentTransaction.commit();
        } else {
            int beCount = fragmentManager.getBackStackEntryCount();
            if (beCount == 0) return;
            fragmentManager.popBackStack("FragmentCheckedPositionDetails_", 0);
        }
    }

    private void startCountDownServiceConnect() {
        if (labelServiceStatus != null) labelServiceStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        if (labelCountDownService != null) labelCountDownService.setVisibility(View.VISIBLE);
        if (progressService != null) progressService.setVisibility(View.VISIBLE);
        //if (btnCheckService != null) btnCheckService.setBackgroundColor(getResources().getColor(R.color.colorBtnDisabled));
        //if (btnStartStopService != null) btnStartStopService.setBackgroundColor(getResources().getColor(R.color.colorBtnDisabled));

        serviceTimer = new CountDownTimer(serviceCountDownCounter == -1 ? MAX_TIME_FOR_WAITING_FCM_RESPONSE : serviceCountDownCounter, 1000) {

            public void onTick(long millisUntilFinished) {
                labelCountDownService.setText("" + millisUntilFinished / 1000);
                serviceCountDownCounter = millisUntilFinished;
                Log.i("ghghgh", "" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (labelCountDownService != null)
                    labelCountDownService.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                if (labelCountDownService != null) labelCountDownService.setVisibility(View.GONE);
                if (progressService != null) progressService.setVisibility(View.GONE);

                serviceTimer = null;
                serviceCountDownCounter = -1;
                isWaitingForResponseFromFcm = false;

                if (isServiceStarted) labelServiceStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);

                DialogInfo.createDialog(MainActivity.this)
                        .setTitle("Chyba")
                        .setMessage("Nepodařilo se doručit zprávu v nastaveném časovém limitu")
                        .show();
            }
        }.start();
    }

    private void startCountDownGpsStart(final boolean requestStartGps) {
        if (labelGpsStatus != null) labelGpsStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.VISIBLE);
        if (progressGps != null) progressGps.setVisibility(View.VISIBLE);

        long counterLimit = MAX_TIME_FOR_WAITING_FCM_RESPONSE_LOCATION_CREATE;
        if (requestStartGps) counterLimit = MAX_TIME_FOR_WAITING_FCM_RESPONSE;

        gpsTimer = new CountDownTimer(gpsCountDownCounter == -1 ? counterLimit : gpsCountDownCounter, 1000) {

            public void onTick(long millisUntilFinished) {
                labelCountDownGps.setText("" + millisUntilFinished / 1000);
                gpsCountDownCounter = millisUntilFinished;
            }

            public void onFinish() {
                gpsTimer = null;
                gpsCountDownCounter = -1;
                isRequestStartGps = false;

                if (labelCountDownGps != null) labelCountDownGps.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.GONE);
                if (progressGps != null) progressGps.setVisibility(View.GONE);

                isWaitingForResponseFromFcm = false;

                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                    @Override
                    public void onFragmentLoadClosed() {
                        DialogInfo.createDialog(MainActivity.this)
                                .setTitle("Chyba")
                                .setMessage("Nepodařilo se zpracovat úpžadavek v nastaveném časovém limitu")
                                .show();
                    }
                });

                /*
                if (requestStartGps) {
                    if (labelCountDownGps != null) labelCountDownGps.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                    if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.GONE);
                    if (progressGps != null) progressGps.setVisibility(View.GONE);

                    isWaitingForResponseFromFcm = false;

                    closeFragmentLoad(new OnFragmentLoadClosedListener() {
                        @Override
                        public void onFragmentLoadClosed() {
                            DialogInfo.createDialog(MainActivity.this)
                                    .setTitle("Chyba")
                                    .setMessage("Nepodařilo se zapnout GPS v nastaveném časovém limitu")
                                    .show();
                        }
                    });
                } else {
                    startCountDownLocationResult();
                    sendRequestToFcm(FCM_REQUEST_TYPE_LOCATION_RESULT, true, null);
                }
                */
            }
        }.start();
    }

    public void startCountDownSettings() {
        final FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) fragmentManager.findFragmentByTag("FragmentSaveToDb_");
        if (fragmentSaveToDb == null) return;
        if (!AppUtils.isFragmentCurrent("FragmentSaveToDb_", fragmentManager)) return;

        settingsTimer = new CountDownTimer(settingsCountDownCounter == -1 ? MAX_TIME_FOR_WAITING_FCM_RESPONSE : settingsCountDownCounter, 1000) {

            public void onTick(long millisUntilFinished) {
                settingsCountDownCounter = millisUntilFinished;
            }

            public void onFinish() {
                fragmentSaveToDb.updateViewsAfterSendClick(true);
                fragmentSaveToDb.updateViewsAfterLoadClick(true);
                settingsTimer = null;
                settingsCountDownCounter = -1;
                isWaitingForResponseFromFcm = false;

                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                    @Override
                    public void onFragmentLoadClosed() {
                        DialogInfo.createDialog(MainActivity.this)
                                .setTitle("Chyba")
                                .setMessage("Nepodařilo se odeslat nastavení v nastaveném časovém limitu")
                                .show();
                    }
                });
            }
        }.start();
    }

    public void startCountDownLocationResult() {
        locationResultTimer = new CountDownTimer(locationResultCountDownCounter == -1 ? MAX_TIME_FOR_WAITING_FCM_RESPONSE : locationResultCountDownCounter, 1000) {

            public void onTick(long millisUntilFinished) {
                labelCountDownGps.setText("" + millisUntilFinished / 1000);
                locationResultCountDownCounter = millisUntilFinished;
            }

            public void onFinish() {
                if (labelCountDownGps != null) labelCountDownGps.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.GONE);
                if (progressGps != null) progressGps.setVisibility(View.GONE);

                locationResultTimer = null;
                locationResultCountDownCounter = -1;

                isWaitingForResponseFromFcm = false;

                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                    @Override
                    public void onFragmentLoadClosed() {
                        DialogInfo.createDialog(MainActivity.this)
                                .setTitle("Chyba")
                                .setMessage("Nepodařilo se získat polohu v nastaveném časovém limitu")
                                .show();
                    }
                });
            }
        }.start();
    }

    private void stopCountDownServiceConnect() {
        if (serviceTimer != null) {
            serviceTimer.cancel();
            serviceTimer = null;
        }

        if (labelCountDownService != null) labelCountDownService.setVisibility(View.GONE);
        if (progressService != null) progressService.setVisibility(View.GONE);

        serviceCountDownCounter = -1;
    }

    private void stopCountDownGpsConnect() {
        if (gpsTimer != null) {
            gpsTimer.cancel();
            gpsTimer = null;
        }

        if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.GONE);
        if (progressGps != null) progressGps.setVisibility(View.GONE);

        gpsCountDownCounter = -1;
    }

    public void stopCountDownSettings() {
        if (settingsTimer != null) {
            settingsTimer.cancel();
            settingsTimer = null;
        }

        final FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) fragmentManager.findFragmentByTag("FragmentSaveToDb_");
        if (fragmentSaveToDb == null) return;
        fragmentSaveToDb.updateViewsAfterSendClick(true);
        fragmentSaveToDb.updateViewsAfterLoadClick(true);
        settingsCountDownCounter = -1;
    }

    public void stopCountDownLocationResult() {
        if (locationResultTimer != null) {
            locationResultTimer.cancel();
            locationResultTimer = null;
        }

        if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.GONE);
        if (progressGps != null) progressGps.setVisibility(View.GONE);
        locationResultCountDownCounter = -1;
    }

    private void registerLocationReceiver() {
        Log.i(TAG, "registerLocationReceiver()");

        locacionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "LocacionReceiver() - onReceive()");

                stopCountDownLocationResult();
                stopCountDownGpsConnect();

                if (intent == null) return;

                if (intent.hasExtra(KEY_LOCATION_DISABLED)) {
                    updateMessage(intent.getStringExtra(KEY_MESSAGE));

                    DialogInfo.createDialog(MainActivity.this)
                            .setTitle("Info")
                            .setMessage("Na sledovaném zařízení není povoleno zjišťování polohy")
                            .show();
                    return;
                }

                updateBatteryStatus(intent.getStringExtra(KEY_BATTERY_PERCENTAGES), intent.getStringExtra(KEY_BATTERY_PLUGGED));
                RemoteMessage remoteMessage = intent.getParcelableExtra(KEY_DATA);

                if (remoteMessage != null) {
                    Map<String, String> data = remoteMessage.getData();
                    if (data != null) {
                        if (!data.isEmpty()) {
                            NewLocation newLocation = new NewLocation();

                            try {
                                if (data.containsKey("date"))
                                    newLocation.setDate(Long.parseLong(data.get("date")));
                                if (data.containsKey("latitude"))
                                    newLocation.setLatitude(Double.parseDouble(data.get("latitude")));
                                if (data.containsKey("longitude"))
                                    newLocation.setLongitude(Double.parseDouble(data.get("longitude")));
                                if (data.containsKey("speed"))
                                    newLocation.setSpeed(Float.parseFloat(data.get("accuracy")));
                                if (data.containsKey("accuracy"))
                                    newLocation.setAccuracy(Float.parseFloat(data.get("accuracy")));
                                if (data.containsKey("batteryStatus"))
                                    newLocation.setAccuracy(Float.parseFloat(data.get("batteryStatus")));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            labelLastGpsUpdatet.setText(DateTimeUtils.getDateTime(newLocation.getDate()));
                            updateAddress(AppUtils.getAddressForLocation(MainActivity.this, newLocation.getLatitude(), newLocation.getLongitude()));

                            Log.i(TAG, "LocacionReceiver() - onReceive() - newLocation:");
                            Log.i(TAG, newLocation.toString());

                            FragmentMap fragmentMap = (FragmentMap) fragmentManager.findFragmentByTag("FragmentMap2");

                            if (fragmentMap == null) showFragmentMap2(newLocation, "tady");
                            else fragmentMap.updateMap(newLocation, "tady");
                        } else {
                            Log.i(TAG, "LocacionReceiver() - onReceive() - data.isEmpty()");
                        }
                    } else {
                        Log.i(TAG, "LocacionReceiver() - onReceive() - data == null");
                    }
                } else {
                    Log.i(TAG, "LocacionReceiver() - onReceive() - remoteMessage == null");
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

                isWaitingForResponseFromFcm = false;
                isRequestStartGps = false;

                stopCountDownServiceConnect();
                stopCountDownGpsConnect();

                if (intent == null) return;
                updateBatteryStatus(intent.getStringExtra(KEY_BATTERY_PERCENTAGES), intent.getStringExtra(KEY_BATTERY_PLUGGED));
                String responseServiceStatus = intent.getStringExtra(KEY_SERVICE_STATUS);
                String responseGpsStatus = intent.getStringExtra(KEY_GPS_STATUS);
                updateMessage(intent.getStringExtra(KEY_MESSAGE));

                int serviceStatus = STOPED;
                int gpsStatus = STOPED;

                try {
                    serviceStatus = Integer.parseInt(responseServiceStatus);
                    gpsStatus = Integer.parseInt(responseGpsStatus);
                } catch (NumberFormatException e) {
                    Log.i(TAG, "serviceStatusCheckedReceiver - NumberFormatException: " + e.getMessage());
                    return;
                }

                MainActivity.this.onServiceStatusChecked(serviceStatus, gpsStatus);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceStatusCheckedReceiver, new IntentFilter(ACTION_SERVICE_STATUS_BROADCAST));
    }

    public void registerDatabaseSettingsUpdatedReceiver() {
        databaseSettingsUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "databaseSettingsUpdatedReceiver - onReceive()");

                stopCountDownSettings();

                FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) fragmentManager.findFragmentByTag("FragmentSaveToDb_");
                if (fragmentSaveToDb != null) {
                    fragmentSaveToDb.updateViewsAfterSendClick(true);
                    fragmentSaveToDb.updateViewsAfterLoadClick(true);
                }

                if (intent.getIntExtra(KEY_SAVE_NEW_DB_SETTINGS, -1) == DB_SETTINGS_UPDATED_SUCCESS) {
                    DialogInfo.createDialog(MainActivity.this)
                            .setTitle("Info")
                            .setMessage(intent.getStringExtra(KEY_MESSAGE))
                            .show();
                } else if (intent.getIntExtra(KEY_SAVE_NEW_DB_SETTINGS, -1) == DB_SETTINGS_UPDATED_ERROR) {
                    DialogInfo.createDialog(MainActivity.this)
                            .setTitle("Info")
                            .setMessage(intent.getStringExtra(KEY_MESSAGE))
                            .show();
                } else {
                    DialogInfo.createDialog(MainActivity.this)
                            .setTitle("Info")
                            .setMessage("Neznámá chyba při ukládání nového nastavení databáze")
                            .show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(databaseSettingsUpdatedReceiver, new IntentFilter(ACTION_DATABASE_SETTINGS_UPDATED));
    }

    public void registerDatabaseSettingsLoadedReceiver() {
        databaseSettingsLoadedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "databaseSettingsLoadedReceiver - onReceive()");

                int savingToDatabaseEnabled = 0;
                long autoCheckedPositionSavingInterval = -1;
                int maxCountOfLocationChecked = -1;
                int timeUnit = TIME_UNIT_SECONDS;
                long locationsInterval = LOCATION_DEFAULT_INTERVAL;
                int locationsIntervalTimeUnit = TIME_UNIT_SECONDS;

                stopCountDownSettings();

                try {
                    savingToDatabaseEnabled = Integer.parseInt(intent.getStringExtra(KEY_DB_ENABLED));
                    autoCheckedPositionSavingInterval = Long.parseLong(intent.getStringExtra(KEY_SAVE_INTERVAL));
                    maxCountOfLocationChecked = Integer.parseInt(intent.getStringExtra(KEY_MAX_COUNT_LOC_SAVE));
                    timeUnit = Integer.parseInt(intent.getStringExtra(KEY_TIME_UNIT));
                    locationsInterval = Long.parseLong(intent.getStringExtra(KEY_LOCATIONS_INTERVAL));
                    locationsIntervalTimeUnit = Integer.parseInt(intent.getStringExtra(KEY_LOCATIONS_INTERVAL_TIME_UNIT));

                    appPrefs.edit()
                            .savingToDatabaseEnabled().put(savingToDatabaseEnabled == 1)
                            .autoCheckedPositionSavingInterval().put(autoCheckedPositionSavingInterval)
                            .maxCountOfLocationChecked().put(maxCountOfLocationChecked)
                            .timeUnit().put(timeUnit)
                            .locationInterval().put(locationsInterval)
                            .locationIntervalTimeUnit().put(locationsIntervalTimeUnit)
                            .apply();

                    FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) fragmentManager.findFragmentByTag("FragmentSaveToDb_");
                    if (fragmentSaveToDb != null) {
                        fragmentSaveToDb.afterLoaded();
                    }


                } catch (NullPointerException e) {
                    DialogInfo.createDialog(MainActivity.this)
                            .setTitle("Info")
                            .setMessage("Načítání nastavení : NullPointerException")
                            .show();
                } catch (NumberFormatException e) {
                    DialogInfo.createDialog(MainActivity.this)
                            .setTitle("Info")
                            .setMessage("Načítání nastavení : NumberFormatException")
                            .show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(databaseSettingsLoadedReceiver, new IntentFilter(ACTION_DATABASE_SETTINGS_LOADED));
    }

    public void registerMessageReceiver() {
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "messageReceiver - onReceive()");

                DialogInfo.createDialog(MainActivity.this)
                        .setTitle("Info")
                        .setMessage(intent.getStringExtra(KEY_MESSAGE))
                        .show();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(ACTION_SHOW_MESSAGE));
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

    private void unregisterDatabaseSettingsUpdatedReceiver() {
        Log.i(TAG, "unregisterDatabaseSettingsUpdatedReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(databaseSettingsUpdatedReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterDatabaseSettingsUpdatedReceiver(): " + e.getMessage());
        }
    }

    private void unregisterDatabaseSettingsLoadedReceiver() {
        Log.i(TAG, "unregisterDatabaseSettingsLoadedReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(databaseSettingsLoadedReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterDatabaseSettingsLoadedReceiver(): " + e.getMessage());
        }
    }

    private void unregisterMessageReceiver() {
        Log.i(TAG, "unregisterMessageReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "unregisterMessageReceiver(): " + e.getMessage());
        }
    }

    private void updateBatteryStatus(String batteryPercentages, String batteryPlugged) {
        if (batteryPercentages == null) labelBattery.setText("???");
        if (batteryPercentages.equals("")) labelBattery.setText("???");
        if (batteryPercentages.equals("0")) labelBattery.setText("???");
        else labelBattery.setText("" + batteryPercentages + "%");

        int percentages = -1;
        boolean plugged = !batteryPlugged.equals("0");

        try {
            percentages = (int) Float.parseFloat(batteryPercentages);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        imgBattery.setImageDrawable(getResources().getDrawable(AppUtils.getImageResForBattery(percentages, plugged)));

    }

    private void updateMessage(String message) {
        if (message == null) labelMessage.setText("");
        else labelMessage.setText(message);
    }

    private void updateAddress(Address address) {
        labelAddress.setText(AppUtils.addressToString(address));
    }

    public void getAllCheckedPositions(final OnAllCheckedPositionsLoadedListener listener) {
        ApiDatabase api = ControllerDatabase.getRetrofitInstance().create(ApiDatabase.class);
        final Call<ResponseAllCheckedPositions> call = api.getAllCheckedPositions(1);

        showFrgmentLoad("Stahuji data", 0, new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                call.enqueue(new Callback<ResponseAllCheckedPositions>() {
                    @Override
                    public void onResponse(Call<ResponseAllCheckedPositions> call, Response<ResponseAllCheckedPositions> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                                ResponseAllCheckedPositions responseAllCheckedPositions = response.body();
                                MainActivity.itemsCheckedPositions = new ArrayList<PositionChecked>();

                                for (ResponseCheckedPosition rp : responseAllCheckedPositions.getPositions()) {
                                    MainActivity.itemsCheckedPositions.add(rp.toPositionChecked());
                                }

                                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                    @Override
                                    public void onFragmentLoadClosed() {

                                        if (listener != null) {
                                            listener.onAllCheckedPositionsLoaded(MainActivity.itemsCheckedPositions);
                                        } else {
                                            showCheckedPositions();
                                        }
                                    }
                                });
                            } else {
                                closeFragmentLoad(null);

                                if (listener != null) {
                                    listener.onAllCheckedPositionsLoaded(MainActivity.itemsCheckedPositions);
                                } else {
                                    DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage("Nestáhla se žádná data." + "\nkód " + response.code()).show();
                                }
                            }
                        } else {
                            closeFragmentLoad(null);

                            if (listener != null) {
                                listener.onAllCheckedPositionsLoaded(null);
                            } else {
                                DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage("Chyba při stahování dat" + "\nkód " + response.code()).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseAllCheckedPositions> call, Throwable t) {
                        closeFragmentLoad(null);

                        if (listener != null) {
                            listener.onAllCheckedPositionsLoaded(null);
                        } else {
                            DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage("Chyba při stahování dat").show();
                        }
                    }
                });
            }
        });
    }

    public void deleteAllItems(final OnAllItemsDeletedListener listener) {
        final ApiDatabase api = ControllerDatabase.getRetrofitInstance().create(ApiDatabase.class);
        final Call<ResponseDeletePosition> call = api.deleteAllCheckedPositions();

        showFrgmentLoad("Mazání všech dat...", 0, new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                call.enqueue(new Callback<ResponseDeletePosition>() {
                    @Override
                    public void onResponse(Call<ResponseDeletePosition> call, Response<ResponseDeletePosition> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                                ResponseDeletePosition responseAllCheckedPositions = response.body();

                                if (responseAllCheckedPositions.getMessage() != null) {
                                    DialogInfo.createDialog(MainActivity.this).setTitle("Zpráva z databáze:").setMessage(responseAllCheckedPositions.getMessage());
                                }

                                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                    @Override
                                    public void onFragmentLoadClosed() {

                                        if (listener != null) {
                                            listener.onAllItemsDeletedListener();
                                        } else {
                                            showCheckedPositions();
                                        }
                                    }
                                });
                            } else {
                                closeFragmentLoad(null);

                                if (listener != null) {
                                    listener.onAllItemsDeletedListener();
                                } else {
                                    DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage("Chyba při mazání všech poloh." + "\nkód " + response.code()).show();
                                }
                            }
                        } else {
                            closeFragmentLoad(null);

                            if (listener != null) {
                                listener.onAllItemsDeletedListener();
                            } else {
                                DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage("Chyba při mazání všech poloh." + "\nkód " + response.code()).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseDeletePosition> call, Throwable t) {
                        closeFragmentLoad(null);

                        if (listener != null) {
                            listener.onAllItemsDeletedListener();
                        } else {
                            DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage("Chyba při mazání všech poloh.").show();
                        }
                    }
                });
            }
        });
    }

    /*
    private String getPhoneNumber() {
        Log.i(TAG, "MyFirebaseMessagingService - getPhoneNumber()");

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                return null;
            } else {
                return tm.getLine1Number();
            }
        } else {
            return tm.getLine1Number();
        }
    }
    */

    private void initStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    private BroadcastReceiver locationProvidersChangedStatusReceiver;

    private void registerLocationProvidersChangedStatusReceiver() {
        Log.i(TAG, "LocationMonitoringService - registerLocationProvidersChangedStatusReceiver()");
        locationProvidersChangedStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "locationProvidersChangedStatusReceiver - onReceive()");
                intent.toString();
            }
        };

        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        //filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        filter.addAction("android.location.PROVIDERS_CHANGED");

        LocalBroadcastManager.getInstance(this).registerReceiver(locationProvidersChangedStatusReceiver, filter);
    }

    private void unregisterLocationProvidersChangedStatusReceiver() {
        Log.i(TAG, "LocationMonitoringService - unregisterLocationProvidersChangedStatusReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(locationProvidersChangedStatusReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG, "LocationMonitoringService - unregisterLocationProvidersChangedStatusReceiver(): " + e.getMessage());
        }
    }
}
