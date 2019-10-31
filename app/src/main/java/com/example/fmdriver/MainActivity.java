package com.example.fmdriver;

import android.app.ActivityManager;
import android.arch.lifecycle.Lifecycle;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.fmdriver.adapters.AdapterLog;
import com.example.fmdriver.customViews.DialogInfo;
import com.example.fmdriver.customViews.DialogInput;
import com.example.fmdriver.customViews.DialogYesNo;
import com.example.fmdriver.fragments.FragmentLoad;
import com.example.fmdriver.fragments.FragmentMap;
import com.example.fmdriver.fragments.FragmentSaveToDb;
import com.example.fmdriver.interfaces.AppPrefs_;
import com.example.fmdriver.listeners.OnAllCheckedPositionsLoadedListener;
import com.example.fmdriver.listeners.OnAllItemsDeletedListener;
import com.example.fmdriver.listeners.OnCallCanceledListener;
import com.example.fmdriver.listeners.OnFragmentLoadClosedListener;
import com.example.fmdriver.listeners.OnFragmentLoadShowedListener;
import com.example.fmdriver.listeners.OnInputInsertedListener;
import com.example.fmdriver.listeners.OnRegisteredDevicesLoadedListener;
import com.example.fmdriver.listeners.OnServiceStatusCheckedListener;
import com.example.fmdriver.listeners.OnYesNoDialogSelectedListener;
import com.example.fmdriver.objects.AppLog;
import com.example.fmdriver.objects.Device;
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
import com.example.fmdriver.retrofit.responses.ResponseAllDevices;
import com.example.fmdriver.retrofit.responses.ResponseCheckedPosition;
import com.example.fmdriver.retrofit.responses.ResponseDeletePosition;
import com.example.fmdriver.retrofit.responses.ResponseDevice;
import com.example.fmdriver.services.AppService;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;
import com.example.fmdriver.utils.AppUtils;
import com.example.fmdriver.utils.DateTimeUtils;
import com.example.fmdriver.utils.FragmentController;
import com.example.fmdriver.utils.FragmentsNames;
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
        FragmentsNames,
        OnCallCanceledListener,
        OnServiceStatusCheckedListener {

    @Pref
    public static AppPrefs_ appPrefs;

    @ViewById
    TextView
            labelGpsStatus,
            labelServiceStatus,
            labelBattery,
            labelCountDownService,
            labelCountDownGps,
            labelLastGpsUpdated,
            labelAddress,
            labelToolbarDevice;

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
    ProgressBar progressService, progressGps, progressAlarm, progressCall;

    @ViewById
    RecyclerView recyclerViewLog;

    @InstanceState
    boolean isAfterStart;

    @InstanceState
    int batteryPercentages = -1;

    @InstanceState
    boolean batteryIsPlugged;

    @InstanceState
    public boolean isWaitingForResponseFromFcm, isRequestStopSevice;

    private FragmentController fragmentController;

    @InstanceState
    public boolean
            isServiceStarted,
            isGpsStarted,
            isAlarmStarted,
            checkingServiceStatusInProgress,
            isRequestLocation,
            isRequestStopServiceAfterWork;

    @InstanceState
    public static ArrayList<PositionChecked> itemsCheckedPositions;

    BroadcastReceiver locationReceiver;
    BroadcastReceiver serviceStatusCheckedReceiver;
    BroadcastReceiver databaseSettingsUpdatedReceiver;
    BroadcastReceiver databaseSettingsLoadedReceiver;
    BroadcastReceiver messageReceiver;
    BroadcastReceiver counterReceiver;

    @InstanceState
    public AppLog appLog;

    @InstanceState
    ArrayList<Device> registeredDevices;

    AdapterLog adapterLog;
    AppService appService;

    @InstanceState
    int timerRequestType = SERVICE_TIMER_TYPE_NONE;

    @InstanceState
    boolean mBounded;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG_SERVICE, "MainActivity - onServiceConnected()");
            mBounded = false;
            appService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG_SERVICE, "MainActivity - onServiceConnected()");

            AppService.LocalBinderAppSevice mLocalBinder = (AppService.LocalBinderAppSevice) service;
            appService = mLocalBinder.getService();
            mBounded = true;

            if (timerRequestType != SERVICE_TIMER_TYPE_NONE && !isWaitingForResponseFromFcm)
                appService.start(timerRequestType);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAfterStart = savedInstanceState == null;
    }

    @AfterViews
    void afterViews() {
        checkOnline(true);

        if (isAfterStart) {
            getRegisteredDevices(new OnRegisteredDevicesLoadedListener() {
                @Override
                public void onRegisteredDevicesLoaded() {
                    if (registeredDevices == null) return;
                    int lastSelectedDevicePosition = getDevicePositionInListById(appPrefs.remoteDeviceId().getOr(-1));
                    labelToolbarDevice.setText(registeredDevices.get(lastSelectedDevicePosition).getName());
                    //if (!checkingServiceStatusInProgress) checkServiceStatus();
                    Animators.animateStatusAfterStart(imgCheckService);
                }
            });
        } else {
            int lastSelectedDevicePosition = getDevicePositionInListById(appPrefs.remoteDeviceId().getOr(-1));
            labelToolbarDevice.setText(registeredDevices.get(lastSelectedDevicePosition).getName());

            if (isServiceStarted) {
                labelServiceStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);
                imgStartStopService.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_green));
            } else {
                labelServiceStatus.setAlpha(ALPHA_VISIBILITY_GONE);
                imgStartStopService.setImageDrawable(getResources().getDrawable(R.drawable.ic_power));
            }

            if (isGpsStarted) {
                labelGpsStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);
                imgStartStopGps.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_green));
            } else {
                labelGpsStatus.setAlpha(ALPHA_VISIBILITY_GONE);
                imgStartStopGps.setImageDrawable(getResources().getDrawable(R.drawable.ic_location));
            }

            if (isAlarmStarted) {
                imgAlarm.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_red));
            } else {
                imgAlarm.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm));
            }
        }

        updateBatteryStatus();
        initLog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG_SERVICE, "MainActivity - onPause()");
        unbindTimerService();

        unregisterServiceStatusCheckedReceiver();
        unregisterLocationReceiver();
        unregisterDatabaseSettingsUpdatedReceiver();
        unregisterDatabaseSettingsLoadedReceiver();
        unregisterMessageReceiver();
        unregisterCounterReceiver();

        unregisterLocationProvidersChangedStatusReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG_SERVICE, "MainActivity - onResume()");
        Log.i(TAG, appPrefs.fcmToken().get());
        registerServiceStatusCheckedReceiver();
        registerLocationReceiver();
        registerDatabaseSettingsUpdatedReceiver();
        registerDatabaseSettingsLoadedReceiver();
        registerMessageReceiver();
        registerCounterReceiver();

        bindTimerService();
        registerLocationProvidersChangedStatusReceiver();
        initStetho();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG_SERVICE, "*****************  MainActivity - onDestroy() ***************** ");
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG_SERVICE, "MainActivity - onBackPressed()");
        if (AppUtils.isFragmentCurrent("FragmentLoad_", getSupportFragmentManager())) return;

        int fragmentsInStack = getSupportFragmentManager().getBackStackEntryCount();

        if (fragmentsInStack == 0) {
            if (appService != null) appService.setRequestStopService();
            else Log.i(TAG_SERVICE, "appService == null");
        }

        super.onBackPressed();
    }

    public FragmentController getFragmentController() {
        if (this.fragmentController == null) {
            this.fragmentController = new FragmentController(
                    this,
                    getSupportFragmentManager(),
                    R.id.container,
                    animShowFragment,
                    animHideFragment);
        }

        return fragmentController;
    }

    @Click(R.id.labelToolbarDevice)
    void clickLabelToolbarDevice() {
        Animators.animateButtonClick(labelToolbarDevice);
        showFragmentDevices();
    }

    public void startTimerService(int taskType) {
        Log.i(TAG_SERVICE, "MainActivity - startService()");

        this.timerRequestType = taskType;

        if (!isMyServiceRunning(AppService.class)) {
            appService = new AppService();
            startService(new Intent(this, appService.getClass()));
        }

        if (!mBounded) {
            Log.i(TAG_SERVICE, "MainActivity - startService() - mBounded == FALSE");
            bindService(new Intent(this, AppService.class), mConnection, BIND_AUTO_CREATE);
        } else {
            Log.i(TAG_SERVICE, "MainActivity - startService() - mBounded == TRUE");
            appService.start(timerRequestType);
        }
    }

    public void bindTimerService() {
        Log.i(TAG_SERVICE, "MainActivity - bindToTimerService()");
        if (isMyServiceRunning(AppService.class)) {
            if (!mBounded) {
                Log.i(TAG_SERVICE, "MainActivity - bindToTimerService() - mBounded == FALSE");
                bindService(new Intent(this, AppService.class), mConnection, BIND_AUTO_CREATE);
            } else {
                Log.i(TAG_SERVICE, "MainActivity - bindToTimerService() - mBounded == TRUE");
            }
        }
    }

    public void unbindTimerService() {
        Log.i(TAG_SERVICE, "MainActivity - unbindTimerService()");

        if (!mBounded) {
            Log.i(TAG_SERVICE, "MainActivity - unbindTimerService() - mBounded == FALSE -> nelze odpojit");
            Log.i(TAG_SERVICE, "MainActivity - unbindTimerService() - appService == null : " + (appService == null));
            return;
        }

        try {
            unbindService(mConnection);
            mBounded = false;
        } catch (IllegalArgumentException e) {
            Log.i(TAG_SERVICE, "MainActivity - unbindTimerService(): unbindService -> IllegalArgumentException");
            Log.i(TAG_SERVICE, e.getMessage());
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG_SERVICE, "MainActivity - isMyServiceRunning() - TRUE");
                return true;
            }
        }
        Log.i(TAG, "MyFirebaseMessagingService - isMyServiceRunning() - FALSE");
        return false;
    }

    @Override
    public void onServiceStatusChecked(final int serviceStatus, final int gpsStatus) {
        checkingServiceStatusInProgress = false;

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
    public void callCanceled() {

    }

    public void appendLog(String log, boolean show) {
        Log.i(TAG, log);
        if (show) updateAppLog(log);
    }

    public void appendLog(String log, boolean show, int colorRes) {
        Log.i(TAG, log);
        if (show) updateAppLog(log, colorRes);
    }

    private void initLog() {
        if (this.appLog == null) this.appLog = new AppLog();
        if (this.adapterLog == null) this.adapterLog = new AdapterLog(this);
        recyclerViewLog.setAdapter(this.adapterLog);
        recyclerViewLog.setLayoutManager(new LinearLayoutManager(this));
        if (this.appLog.getItemsCount() > 0)
            recyclerViewLog.scrollToPosition(this.adapterLog.getItemCount() - 1);
    }

    public void updateAppLog(String log) {
        if (this.adapterLog == null) initLog();
        if (this.appLog == null) return;
        this.appLog.addLog(log);
        this.adapterLog.notifyDataSetChanged();
        recyclerViewLog.scrollToPosition(this.adapterLog.getItemCount() - 1);
    }

    public void updateAppLog(String log, int colorRes) {
        if (this.adapterLog == null) initLog();
        if (this.appLog == null) return;
        this.appLog.addLog(log, colorRes);
        this.adapterLog.notifyDataSetChanged();
        recyclerViewLog.scrollToPosition(this.adapterLog.getItemCount() - 1);
    }

    @Click(R.id.imgMap)
    void clickBtnMap() {
        Animators.animateButtonClick(imgMap);
        if (!checkOnline(false)) return;

        isRequestLocation = true;
        isRequestStopServiceAfterWork = !isServiceStarted;

        if (labelGpsStatus != null) labelGpsStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.VISIBLE);
        if (progressGps != null) progressGps.setVisibility(View.VISIBLE);

        if (isWaitingForResponseFromFcm) return;
        startTimerService(SERVICE_TIMER_TYPE_LOCATION);
        sendRequestToFcm(FCM_REQUEST_TYPE_LOCATION,null);
    }

    @Click(R.id.imgToken)
    void clickBtnToken() {
        Animators.animateButtonClick(imgToken);
        showFragmentToken(appPrefs.fcmToken().get());
    }

    @Click(R.id.imgCheckService)
    void clickCheckService() {
        Animators.animateButtonClick(imgCheckService);
        if (!checkOnline(false)) return;

        if (labelServiceStatus != null) labelServiceStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        if (labelCountDownService != null) labelCountDownService.setVisibility(View.VISIBLE);
        if (progressService != null) progressService.setVisibility(View.VISIBLE);

        if (isWaitingForResponseFromFcm) return;
        startTimerService(SERVICE_TIMER_TYPE_SERVICE);
        sendRequestToFcm(FCM_REQUEST_TYPE_SERVICE_STATUS, null);
    }

    @Click(R.id.imgStartStopService)
    void clickStartStopService() {
        Animators.animateButtonClick(imgStartStopService);
        if (!checkOnline(false)) return;

        if (labelServiceStatus != null) labelServiceStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        if (labelCountDownService != null) labelCountDownService.setVisibility(View.VISIBLE);
        if (progressService != null) progressService.setVisibility(View.VISIBLE);

        if (isWaitingForResponseFromFcm) return;
        startTimerService(SERVICE_TIMER_TYPE_SERVICE);
        sendRequestToFcm(isServiceStarted ? FCM_REQUEST_TYPE_SERVICE_STOP : FCM_REQUEST_TYPE_SERVICE_START, null);
    }

    @Click(R.id.imgStartStopGps)
    void clickBtnStartStopGps() {
        Animators.animateButtonClick(imgStartStopGps);
        if (!checkOnline(false)) return;

        if (labelGpsStatus != null) labelGpsStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.VISIBLE);
        if (progressGps != null) progressGps.setVisibility(View.VISIBLE);

        if (isWaitingForResponseFromFcm) return;
        startTimerService(SERVICE_TIMER_TYPE_GPS);
        sendRequestToFcm(isGpsStarted ? FCM_REQUEST_TYPE_GPS_STOP : FCM_REQUEST_TYPE_GPS_START, null);
    }

    @Click(R.id.imgSettings)
    void clickSaveToDb() {
        Animators.animateButtonClick(imgSettings);
        if (checkOnline(false)) showFragmentSaveToDb();
    }

    @Click(R.id.imgShowData)
    void clickShowData() {
        Animators.animateButtonClick(imgShowData);
        if (checkOnline(false)) getAllCheckedPositions(null);
    }

    @Click(R.id.imgAlarm)
    void clickBtnAlarm() {
        Animators.animateButtonClick(imgAlarm);
        if (!checkOnline(false)) return;

            if (isWaitingForResponseFromFcm || isAlarmStarted) {
            appendLog(
                    "Čekám na odpověď serveru. Do té doby není možné odesílat další požadavky",
                    true,
                    R.color.colorMessageInLogWaitingForResponse);
            return;
        }

        if (isAlarmStarted) {
            appendLog(
                    "Dokud neskončí spuštěný alarm, není možné spustit další",
                    true,
                    R.color.colorMessageInLogWaitingForResponse);
            return;
        }

        DialogYesNo.createDialog(this)
                .setTitle("Alarm")
                .setMessage("Chceš na vzdáleném zařízení přehrát alarm?")
                .setListener(new OnYesNoDialogSelectedListener() {
                    @Override
                    public void onYesSelected() {
                        Animators.animateButtonClick(imgAlarm);
                        sendRequestToFcm(FCM_REQUEST_TYPE_ALARM, null);
                        imgAlarm.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_alarm_disabled));
                        progressAlarm.setVisibility(View.VISIBLE);
                        startTimerService(SERVICE_TIMER_TYPE_ALARM);
                        appendLog("Odeslán požadavek na alarm...", true);
                    }

                    @Override
                    public void onNoSelected() {
                    }
                }).show();
    }

    @Click(R.id.imgCall)
    void clickBtnCall() {
        Animators.animateButtonClick(imgCall);
        if (!checkOnline(false)) return;

        if (isWaitingForResponseFromFcm || isAlarmStarted) {
            appendLog(
                    "Čekám na odpověď serveru. Do té doby není možné odesílat další požadavky",
                    true,
                    R.color.colorMessageInLogWaitingForResponse);
            return;
        }

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
                            sendRequestToFcm(FCM_REQUEST_TYPE_CALL, data);

                            imgCall.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_call_disabled));
                            progressCall.setVisibility(View.VISIBLE);
                            startTimerService(SERVICE_TIMER_TYPE_CALL);
                            appendLog("Odeslán požadavek na příchozí hovor...", true);
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

    public void sendRequestToFcm(int requestType, RequestToFcmData requestToFcmData) {
        checkOnline(false);
        String token = appPrefs.remoteToken().get();

        if (token == null) {
            DialogInfo.createDialog(this).setTitle("Token").setMessage("Zvol vzdálené zařízení").show();
            return;
        }

        if (token.equals("")) {
            DialogInfo.createDialog(this).setTitle("Token").setMessage("Zvol vzdálené zařízení").show();
            return;
        }

        appendLog("sendRequestToFcm: " + AppUtils.requestTypeToString(requestType), true);

        if (isWaitingForResponseFromFcm) {
            appendLog(
                    "Čekám na odpověď serveru. Do té doby není možné odesílat další požadavky",
                    true,
                    R.color.colorMessageInLogWaitingForResponse);
            return;
        }

        if (requestToFcmData == null) {
            requestToFcmData = new RequestToFcmData(MainActivity.appPrefs.fcmToken().get(), requestType);
        }

        RequestToFcm requestToFcm = new RequestToFcm(
                token,
                requestToFcmData);

        Log.i(TAG, requestToFcm.toString());

        ApiFcm apiFcm = ControllerFcm.getRetrofitInstance().create(ApiFcm.class);
        Call<ResponseBody> callFcm = apiFcm.sendRequestToFcm(requestToFcm);

        callFcm.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                appendLog("Response code: " + response.code(), true);

                if (response.isSuccessful()) {
                    Log.i(TAG, "isSuccessful: TRUE");
                    appendLog("Čekám na odpověď vzdáleného zařízení...", true);

                    try {
                        Log.i(TAG, "response: " + response.body().string());
                    } catch (IOException e) {
                        Log.i(TAG, "response: IOException: ");
                        e.printStackTrace();
                    }
                } else {
                    stopTimerTask();

                    closeFragmentLoad(new OnFragmentLoadClosedListener() {
                        @Override
                        public void onFragmentLoadClosed() {
                            try {
                                appendLog("Odeslání požadavku se nezdařilo: " + response.errorBody().string(), true);
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
                appendLog("Odeslání požadavku se nezdařilo.", true);
                Log.i(TAG, "onFailure: ");
                Log.i(TAG, t.getMessage());

                stopTimerTask();

                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                    @Override
                    public void onFragmentLoadClosed() {
                    }
                });
            }
        });
    }

    public void checkServiceStatus() {
        Log.i(TAG, "serviceStopedReceiver - checkServiceStatus()");

        if (labelServiceStatus != null) labelServiceStatus.setAlpha(ALPHA_VISIBILITY_GONE);
        if (labelCountDownService != null) labelCountDownService.setVisibility(View.VISIBLE);
        if (progressService != null) progressService.setVisibility(View.VISIBLE);

        checkingServiceStatusInProgress = true;
        startTimerService(SERVICE_TIMER_TYPE_SERVICE);
        sendRequestToFcm(FCM_REQUEST_TYPE_SERVICE_STATUS, null);
    }

    @UiThread
    public void showFrgmentLoad(String labelText, OnFragmentLoadShowedListener listener) {
        Bundle args = new Bundle();
        args.putString("labelText", labelText);
        getFragmentController().showFragment(FRAGMENT_LOAD, args, true);

        /*
        FragmentLoad fragmentLoad = (FragmentLoad) getSupportFragmentManager().findFragmentByTag("FragmentLoad_");

        if (fragmentLoad == null) {
            fragmentLoad = FragmentLoad_.builder().labelText(labelText).taskType(taskType).build();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(animShowFragment, animHideFragment, animShowFragment, animHideFragment);
            fragmentTransaction.add(R.id.container, fragmentLoad, fragmentLoad.getClass().getSimpleName());
            fragmentTransaction.addToBackStack("FragmentLoad_");
            fragmentTransaction.commitAllowingStateLoss();
        } else {
            int beCount = getSupportFragmentManager().getBackStackEntryCount();
            getSupportFragmentManager().popBackStack("FragmentLoad_", 0);
            fragmentLoad.setLabel(labelText);
        }
        */

        if (listener != null) listener.onFragmentLoadShowed();
    }

    @UiThread
    public void closeFragmentLoad(OnFragmentLoadClosedListener listener) {
        if (isFragmentLoadShowed()) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED))
                getSupportFragmentManager().popBackStack();
        }

        if (listener != null) listener.onFragmentLoadClosed();
    }

    public boolean isFragmentLoadShowed() {
        int beCount = getSupportFragmentManager().getBackStackEntryCount();
        if (beCount == 0) return false;
        return getSupportFragmentManager().getBackStackEntryAt(beCount - 1).getName().equals(FRAGMENT_LOAD);
    }

    public void showFragmentMap2(NewLocation newLocation, String marker) {
        Bundle args = new Bundle();
        args.putParcelable("location", newLocation);
        args.putString("marker", marker);
        getFragmentController().showFragment(FRAGMENT_MAP, args, true);
    }

    public void showFragmentToken(String token) {
        Bundle args = new Bundle();
        args.putString("token", token);
        getFragmentController().showFragment(FRAGMENT_TOKEN, args, true);
    }

    public void showFragmentSaveToDb() {
        getFragmentController().showFragment(FRAGMENT_SAVE_TO_DB, null, true);
    }

    public void showCheckedPositions() {
        Bundle args = new Bundle();
        args.putParcelableArrayList("itemsCheckedPositions", itemsCheckedPositions);
        getFragmentController().showFragment(FRAGMENT_CHECKED_POSITIONS, args, true);
    }

    public void showCheckedPositionDetails(PositionChecked positionChecked) {
        Bundle args = new Bundle();
        args.putParcelable("positionChecked", positionChecked);
        getFragmentController().showFragment(FRAGMENT_CHECKED_POSITION_DETAIL, args, true);
    }

    public void showFragmentDevices() {
        getFragmentController().showFragment(FRAGMENT_DEVICES, null, true);
    }

    public void showFragmentDeviceDetail(Device device) {
        Bundle args = new Bundle();
        args.putParcelable("device", device);
        getFragmentController().showFragment(FRAGMENT_DEVICE_DETAIL, args, true);
    }

    private void registerLocationReceiver() {
        Log.i(TAG, "registerLocationReceiver()");

        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "LocacionReceiver() - onReceive()");
                appendLog("Odpověď : POLOHA", true, R.color.colorMessageInLogResponse);
                stopTimerTask();
                isRequestLocation = false;

                if (isRequestStopServiceAfterWork) {
                    sendRequestToFcm(FCM_REQUEST_TYPE_SERVICE_STOP, null);
                    isRequestStopServiceAfterWork = false;
                }

                if (labelCountDownGps != null) labelCountDownGps.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.GONE);
                if (progressGps != null) progressGps.setVisibility(View.GONE);
                if (isGpsStarted) labelGpsStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);

                if (intent == null) return;

                if (intent.hasExtra(KEY_LOCATION_DISABLED)) {
                    updateMessage(intent.getStringExtra(KEY_MESSAGE));

                    DialogInfo.createDialog(MainActivity.this)
                            .setTitle("Info")
                            .setMessage("Na sledovaném zařízení není povoleno zjišťování polohy")
                            .show();
                    return;
                }

                processBatteryData(intent.getStringExtra(KEY_BATTERY_PERCENTAGES), intent.getStringExtra(KEY_BATTERY_PLUGGED));
                updateBatteryStatus();

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

                            labelLastGpsUpdated.setText(DateTimeUtils.getDateTime(newLocation.getDate()));
                            updateAddress(AppUtils.getAddressForLocation(MainActivity.this, newLocation.getLatitude(), newLocation.getLongitude()));

                            Log.i(TAG, "LocacionReceiver() - onReceive() - newLocation:");
                            Log.i(TAG, newLocation.toString());

                            FragmentMap fragmentMap = (FragmentMap) getSupportFragmentManager().findFragmentByTag("FragmentMap2");

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

        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(ACTION_LOCATION_BROADCAST));
    }

    public void registerServiceStatusCheckedReceiver() {
        serviceStatusCheckedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "serviceStatusCheckedReceiver - onReceive()");
                appendLog("Odpověď : STAV SLUŽBY", true, R.color.colorMessageInLogResponse);

                if (labelCountDownService != null)
                    labelCountDownService.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                if (labelCountDownService != null) labelCountDownService.setVisibility(View.GONE);

                if (progressService != null) progressService.setVisibility(View.GONE);

                if (!isRequestLocation) {
                    if (labelCountDownGps != null)
                        labelCountDownGps.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                    if (labelCountDownGps != null) labelCountDownGps.setVisibility(View.GONE);

                    if (progressGps != null) progressGps.setVisibility(View.GONE);

                    stopTimerTask();
                }

                if (isServiceStarted) labelServiceStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);
                if (isGpsStarted) labelGpsStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);

                if (intent == null) return;

                processBatteryData(intent.getStringExtra(KEY_BATTERY_PERCENTAGES), intent.getStringExtra(KEY_BATTERY_PLUGGED));
                updateBatteryStatus();

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
                appendLog("Odpověď : NASTAVNENÍ UPRAVENO", true, R.color.colorMessageInLogResponse);

                stopTimerTask();

                closeFragmentLoad(new OnFragmentLoadClosedListener() {
                    @Override
                    public void onFragmentLoadClosed() {
                        FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SAVE_TO_DB);
                        if (fragmentSaveToDb == null) return;
                        if (fragmentSaveToDb != null) fragmentSaveToDb.afterLoaded();
                        //AppUtils.showRequestTimerError(MainActivity.this);
                    }
                });

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
                appendLog("Odpověď : NASTAVENÍ NAČTENO", true, R.color.colorMessageInLogResponse);

                int savingToDatabaseEnabled = 0;
                long autoCheckedPositionSavingInterval = -1;
                int maxCountOfLocationChecked = -1;
                int timeUnit = TIME_UNIT_SECONDS;
                long locationsInterval = LOCATION_DEFAULT_INTERVAL;
                int locationsIntervalTimeUnit = TIME_UNIT_SECONDS;
                stopTimerTask();

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

                    FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SAVE_TO_DB);
                    if (fragmentSaveToDb == null) return;
                    if (fragmentSaveToDb != null) fragmentSaveToDb.afterLoaded();
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
                appendLog("Odpověď : ZPRÁVA", true, R.color.colorMessageInLogResponse);

                int actionCode = ACTION_MESSAGE_CODE_NONE;

                try {
                    actionCode = Integer.parseInt(intent.getStringExtra(KEY_ACTION_MESSAGE_CODE));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                processMessageActionCode(actionCode, intent.getStringExtra(KEY_MESSAGE));
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter(ACTION_SHOW_MESSAGE));
    }

    public void registerCounterReceiver() {
        Log.i(TAG_SERVICE, "MainActivity - registerCounterReceiver()");
        counterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG_SERVICE, "counterReceiver - onReceive()");

                int timerType = intent.getIntExtra(KEY_SERVICE_TIMER_TYPE, SERVICE_TIMER_TYPE_NONE);
                long progress = intent.getLongExtra(KEY_SERVICE_PROGRESS, SERVICE_COUNTDOWN_IS_OVER);

                Log.i(TAG_SERVICE, "progress: " + progress);

                if (progress == SERVICE_COUNTDOWN_IS_OVER || progress == SERVICE_COUNTDOWN_IS_OFF) {
                    stopTimerTask();
                }

                //Po nezdařeném získaní stavu zařízení (vypršení času pro získání stavu)
                if (progress == SERVICE_COUNTDOWN_IS_OFF) {
                    batteryPercentages = -1;
                    updateBatteryStatus();
                    labelServiceStatus.setAlpha(ALPHA_VISIBILITY_GONE);
                    labelGpsStatus.setAlpha(ALPHA_VISIBILITY_GONE);
                }

                switch (timerType) {
                    case SERVICE_TIMER_TYPE_NONE:
                        break;
                    case SERVICE_TIMER_TYPE_SERVICE:
                        if (progress != SERVICE_COUNTDOWN_IS_OVER && progress != SERVICE_COUNTDOWN_IS_OFF) {
                            if (labelCountDownService != null)
                                labelCountDownService.setVisibility(View.VISIBLE);
                            if (progressService != null)
                                progressService.setVisibility(View.VISIBLE);
                            labelCountDownService.setText("" + progress / 1000);
                        } else {
                            if (labelCountDownService != null)
                                labelCountDownService.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                            if (labelCountDownService != null)
                                labelCountDownService.setVisibility(View.GONE);
                            if (progressService != null) progressService.setVisibility(View.GONE);

                            if (isServiceStarted)
                                labelServiceStatus.setAlpha(ALPHA_VISIBILITY_VISIBLE);
                            AppUtils.showRequestTimerError(MainActivity.this);
                            appendLog("Nepodařilo se zpracovat požadavek v nastaveném časovém limitu", true);
                        }
                        break;
                    case SERVICE_TIMER_TYPE_GPS:
                        if (progress != SERVICE_COUNTDOWN_IS_OVER && progress != SERVICE_COUNTDOWN_IS_OFF) {
                            if (labelCountDownGps != null)
                                labelCountDownGps.setVisibility(View.VISIBLE);
                            if (progressGps != null) progressGps.setVisibility(View.VISIBLE);
                            labelCountDownGps.setText("" + progress / 1000);
                        } else {
                            if (labelCountDownGps != null)
                                labelCountDownGps.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                            if (labelCountDownGps != null)
                                labelCountDownGps.setVisibility(View.GONE);
                            if (progressGps != null) progressGps.setVisibility(View.GONE);

                            AppUtils.showRequestTimerError(MainActivity.this);
                            appendLog("Nepodařilo se zpracovat požadavek v nastaveném časovém limitu", true);
                        }
                        break;
                    case SERVICE_TIMER_TYPE_LOCATION:
                        if (progress != SERVICE_COUNTDOWN_IS_OVER && progress != SERVICE_COUNTDOWN_IS_OFF) {
                            if (labelCountDownGps != null)
                                labelCountDownGps.setVisibility(View.VISIBLE);
                            if (progressGps != null) progressGps.setVisibility(View.VISIBLE);
                            labelCountDownGps.setText("" + progress / 1000);
                        } else {
                            if (labelCountDownGps != null)
                                labelCountDownGps.setText("" + (MAX_TIME_FOR_WAITING_FCM_RESPONSE + 1000) / 1000);
                            if (labelCountDownGps != null)
                                labelCountDownGps.setVisibility(View.GONE);
                            if (progressGps != null) progressGps.setVisibility(View.GONE);

                            AppUtils.showRequestTimerError(MainActivity.this);
                            appendLog("Nepodařilo se zpracovat požadavek v nastaveném časovém limitu", true);

                            isRequestLocation = false;
                        }
                        break;
                    case SERVICE_TIMER_TYPE_SETTINGS:
                        final FragmentSaveToDb fragmentSaveToDb = (FragmentSaveToDb) getSupportFragmentManager().findFragmentByTag(FRAGMENT_SAVE_TO_DB);

                        if (progress != SERVICE_COUNTDOWN_IS_OVER && progress != SERVICE_COUNTDOWN_IS_OFF) {
                            FragmentLoad fragmentLoad = (FragmentLoad) getSupportFragmentManager().findFragmentByTag("FragmentLoad_");

                            if (fragmentLoad != null) {
                                fragmentLoad.updateLabel("Stahuji nastavení (" + (progress / 1000) + ")");
                            }
                        } else {
                            closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                @Override
                                public void onFragmentLoadClosed() {
                                    if (fragmentSaveToDb == null) return;
                                    if (!AppUtils.isFragmentCurrent(FRAGMENT_SAVE_TO_DB, getSupportFragmentManager())) return;

                                    fragmentSaveToDb.updateViewsAfterSendClick(true);
                                    fragmentSaveToDb.updateViewsAfterLoadClick(true);

                                    AppUtils.showRequestTimerError(MainActivity.this);
                                    appendLog("Nepodařilo se zpracovat požadavek v nastaveném časovém limitu", true);
                                }
                            });
                        }
                        break;
                    case SERVICE_TIMER_TYPE_ALARM:
                        if (progress != SERVICE_COUNTDOWN_IS_OVER && progress != SERVICE_COUNTDOWN_IS_OFF) {
                            if (progressAlarm != null) progressAlarm.setVisibility(View.VISIBLE);
                            if (imgAlarm != null)
                                imgAlarm.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_disabled));
                        } else {
                            if (progressAlarm != null) progressAlarm.setVisibility(View.GONE);
                            if (imgAlarm != null)
                                imgAlarm.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm));

                            AppUtils.showRequestTimerError(MainActivity.this);
                            appendLog("Nepodařilo se zpracovat požadavek v nastaveném časovém limitu", true);
                        }
                        break;
                    case SERVICE_TIMER_TYPE_CALL:
                        if (progress != SERVICE_COUNTDOWN_IS_OVER && progress != SERVICE_COUNTDOWN_IS_OFF) {
                            if (progressCall != null) progressCall.setVisibility(View.VISIBLE);
                            if (imgCall != null)
                                imgCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_disabled));
                        } else {
                            if (progressCall != null) progressCall.setVisibility(View.GONE);
                            if (imgCall != null)
                                imgCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_call));

                            AppUtils.showRequestTimerError(MainActivity.this);
                            appendLog("Nepodařilo se zpracovat požadavek v nastaveném časovém limitu", true);
                        }
                        break;
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(counterReceiver, new IntentFilter(ACTION_SERVICE_PROGRESS));
    }

    private void unregisterLocationReceiver() {
        Log.i(TAG, "unregisterLocationReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
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

    private void unregisterCounterReceiver() {
        Log.i(TAG_SERVICE, "MainActivity - unregisterCounterReceiver()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(counterReceiver);
        } catch (IllegalArgumentException e) {
            Log.i(TAG_SERVICE, "unregisterCounterReceiver(): " + e.getMessage());
        }
    }

    private void updateBatteryStatus() {
        imgBattery.setImageDrawable(getResources().getDrawable(AppUtils.getImageResForBattery(this.batteryPercentages, this.batteryIsPlugged)));
        labelBattery.setText(batteryPercentages >= 0 ? ("" + batteryPercentages + "%") : "?");
    }

    private void updateMessage(String message) {
        if (message != null) appendLog(message, true, R.color.colorMessageInLog);
    }

    private void updateAddress(Address address) {
        labelAddress.setText(AppUtils.addressToString(address));
    }

    public void getAllCheckedPositions(final OnAllCheckedPositionsLoadedListener listener) {
        ApiDatabase api = ControllerDatabase.getRetrofitInstance().create(ApiDatabase.class);
        final Call<ResponseAllCheckedPositions> call = api.getAllCheckedPositions(appPrefs.remoteDeviceId().get());

        showFrgmentLoad("Stahuji data", new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                call.enqueue(new Callback<ResponseAllCheckedPositions>() {
                    @Override
                    public void onResponse(Call<ResponseAllCheckedPositions> call, Response<ResponseAllCheckedPositions> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                                /*
                                ResponseBody responseBody = response.body();
                                try {
                                    Log.i(TAG, responseBody.string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                */

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
        final Call<ResponseDeletePosition> call = api.deleteAllCheckedPositions(appPrefs.remoteDeviceId().get());

        showFrgmentLoad("Mazání všech dat...", new OnFragmentLoadShowedListener() {
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

    private void processBatteryData(String percentages, String status) {
        this.batteryPercentages = -1;
        this.batteryIsPlugged = false;

        this.batteryIsPlugged = !status.equals("0");

        try {
            this.batteryPercentages = (int) Float.parseFloat(percentages);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void processMessageActionCode(int actionCode, String message) {
        switch (actionCode) {
            case ACTION_MESSAGE_CODE_ALARM_START:
                appendLog(message, true);
                isAlarmStarted = true;
                imgAlarm.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_alarm_red));
                progressAlarm.setVisibility(View.GONE);
                stopTimerTask();
                break;
            case ACTION_MESSAGE_CODE_ALARM_STOP:
                appendLog(message, true);
                isAlarmStarted = false;
                imgAlarm.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_alarm));
                break;
            case ACTION_MESSAGE_CODE_CALL_REQUEST:
                appendLog(message, true);
                imgCall.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_call));
                progressCall.setVisibility(View.GONE);
                stopTimerTask();

                DialogInfo.createDialog(this)
                        .setTitle("PŘÍCHOZÍ HOVOR")
                        .setMessage("ČEKEJ NA PŘÍCHOZÍ HOVOR ZE VZDÁLENÉHO ZAŘÍZENÍ!")
                        .show();
                break;
            case ACTION_MESSAGE_CODE_CALL_ERROR:
                appendLog(message, true);
                imgCall.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_call));
                progressCall.setVisibility(View.GONE);
                stopTimerTask();
                break;
        }
    }

    private void stopTimerTask() {
        isWaitingForResponseFromFcm = false;
        timerRequestType = SERVICE_TIMER_TYPE_NONE;
        if (appService != null) appService.setRequestStopService();
        else Log.i(TAG_SERVICE, "appService == null");
        unbindTimerService();
    }

    private void getRegisteredDevices(final OnRegisteredDevicesLoadedListener listener) {
        ApiDatabase api = ControllerDatabase.getRetrofitInstance().create(ApiDatabase.class);
        final Call<ResponseAllDevices> call = api.getAllDevices();

        showFrgmentLoad("Stahuji seznam registrovaných zařízení", new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                call.enqueue(new Callback<ResponseAllDevices>() {
                    @Override
                    public void onResponse(Call<ResponseAllDevices> call, final Response<ResponseAllDevices> response) {
                        if (response.isSuccessful()) {

                            /*
                            try {
                                Log.i(TAG_DB, response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            */

                            final ResponseAllDevices responseAllDevices = response.body();

                            if (responseAllDevices.getMessage() != null) {
                                DialogInfo.createDialog(MainActivity.this).setTitle("Chyba").setMessage(responseAllDevices.getMessage()).show();
                                return;
                            }

                            String thisAndroidId = appPrefs.androidId().get();
                            ArrayList<Device> toReturn = new ArrayList<>();

                            if (responseAllDevices.getDevices() != null && thisAndroidId != null) {
                                //Pokud je toto zařízení také registrováno druhou aplikací do sledovaných,
                                //nebude mít samo sebe v seznamu dostupných zařízení pro sledování
                                for (ResponseDevice responseDevice : responseAllDevices.getDevices()) {
                                    if (!responseDevice.getAndroid_id().equals(thisAndroidId)) {
                                        toReturn.add(responseDevice.toDevice());
                                        if (responseDevice.getId() == appPrefs.remoteDeviceId().getOr(-1))
                                            toReturn.get(toReturn.size() - 1).setCurrent(true);
                                    }
                                }
                            }

                            registeredDevices = new ArrayList<>(toReturn);

                            closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                @Override
                                public void onFragmentLoadClosed() {
                                    if (listener != null) listener.onRegisteredDevicesLoaded();
                                    appendLog("Seznam registrovaných zařízení úspěšně stažen", true);
                                    appendLog("Seznam zařízení: " + responseAllDevices.toString(), true);
                                }
                            });
                        } else {
                            try {
                                Log.i(TAG_DB, response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                @Override
                                public void onFragmentLoadClosed() {
                                    if (listener != null) listener.onRegisteredDevicesLoaded();
                                    appendLog("Nepodařilo se stáhnout seznam registrovaných zařízení. (CODE " + response.code() + ")", true, R.color.colorMessageInLog);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseAllDevices> call, final Throwable t) {
                        closeFragmentLoad(new OnFragmentLoadClosedListener() {
                            @Override
                            public void onFragmentLoadClosed() {
                                if (listener != null) listener.onRegisteredDevicesLoaded();
                                appendLog("Nepodařilo se stáhnout seznam registrovaných zařízení. " + t.getMessage(), true, R.color.colorMessageInLog);
                            }
                        });
                    }
                });
            }
        });
    }

    CountDownTimer timer = new CountDownTimer(500, 500) {
        @Override
        public void onTick(long millisUntilFinished) {}

        @Override
        public void onFinish() {
            longClick = true;
            vibrate(80);
        }
    };

    float x1, y1, x2, y2;
    boolean cancelClick = false;
    boolean longClick = false;
    public void processDeviceClick(MotionEvent e, RecyclerView recyclerView) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = e.getAxisValue(MotionEvent.AXIS_X);
                y1 = e.getAxisValue(MotionEvent.AXIS_Y);
                timer.start();
                break;
            case MotionEvent.ACTION_MOVE:
                x2 = e.getAxisValue(MotionEvent.AXIS_X);
                y2 = e.getAxisValue(MotionEvent.AXIS_Y);

                if (Math.abs(x1 - x2) > 50 || Math.abs(y1 - y2) > 50) {
                    cancelClick = true;
                    timer.cancel();
                    Log.i("tag_motionevent", "ACTION_MOVE - cancel click");
                }
                break;
            case MotionEvent.ACTION_UP:
                if (cancelClick) {
                    cancelClick = false;
                    return;
                }

                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildAdapterPosition(view);
                Log.i("tag_motionevent", "POZICE: " + position);

                if (position < 0) return;

                if (longClick) {
                    longClick = false;
                    showFragmentDeviceDetail(registeredDevices.get(position));
                    return;
                }

                timer.cancel();
                x2 = e.getAxisValue(MotionEvent.AXIS_X);
                y2 = e.getAxisValue(MotionEvent.AXIS_Y);

                appendLog("spinner - onItemSelected: Name: " + registeredDevices.get(position).getName(), false, R.color.colorMessageInLog);
                appPrefs.edit().remoteDeviceId().put(registeredDevices.get(position).getId()).apply();
                appPrefs.edit().remoteToken().put(registeredDevices.get(position).getToken()).apply();

                labelToolbarDevice.setText(registeredDevices.get(position).getName());

                for (int i = 0, count = registeredDevices.size(); i < count; i ++) {
                    if (i != position) registeredDevices.get(i).setCurrent(false);
                    else registeredDevices.get(position).setCurrent(true);
                }

                onBackPressed();
                if (!checkingServiceStatusInProgress) checkServiceStatus();
                break;
        }
    }

    private int getDevicePositionInListById(int id) {
        if (registeredDevices == null) return -1;
        if (registeredDevices.isEmpty()) return -1;

        for (int i = 0, count = registeredDevices.size(); i < count; i ++) {
            if (registeredDevices.get(i).getId() == id) return i;
        }

        return -1;
    }

    public ArrayList<Device> getRegisteredDevices() {
        return registeredDevices;
    }

    public int getDevicePositionInList(int deviceId) {
        if (registeredDevices == null) return -1;
        if (registeredDevices.isEmpty()) return -1;

        for (int i = 0, count = registeredDevices.size(); i < registeredDevices.size(); i ++) {
            if (registeredDevices.get(i).getId() == deviceId) return i;
        }

        return -1;
    }

    public void vibrate(long length) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(length, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(length);
        }
    }

    public boolean checkOnline(final boolean closeIfOffline) {
        if (!AppUtils.isOnline(this)) {
            DialogInfo.createDialog(this)
                    .setTitle("Chyba")
                    .setMessage("Není dostupné připojení k internetu." + (closeIfOffline ? " Aplikace bude ukončena" : ""))
                    .setListener(new DialogInfo.OnDialogClosedListener() {
                        @Override
                        public void onDialogClosed() {
                            if (closeIfOffline) finish();
                        }
                    }).show();
            return false;
        }

        return true;
    }
}
