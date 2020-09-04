package com.example.fmdriver.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.fmdriver.utils.AppConstants;

public class AppService extends Service implements AppConstants {

    public class LocalBinderAppSevice extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }

    IBinder mBinder = new LocalBinderAppSevice();

    CountDownTimer countDownTimer;
    long counter = SERVICE_COUNTDOWN_IS_OFF;
    int timerType = SERVICE_TIMER_TYPE_NONE;
    boolean isRequestStopService = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG_SERVICE, "AppService - onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG_SERVICE, "AppService - onBind()");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG_SERVICE, "AppService - onUnbind()");
        Log.i(TAG_SERVICE, "isRequestStopService == " + isRequestStopService);

        if (isRequestStopService) stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG_SERVICE, "***************** AppService - onDestroy() ***************** ");
        super.onDestroy();
    }

    public void start(int taskType) {
        Log.i(TAG_SERVICE, "AppService - startCountDown()");

        //Pokud běží nějaký odpočet, nebude sa zapínat další
        if (timerType != SERVICE_TIMER_TYPE_NONE) {
            Log.i(TAG_SERVICE, "AppService - start() : Již nějaký timer běží -> RETURN");
            return;
        }

        timerType = taskType;

        long delay = timerType == SERVICE_TIMER_TYPE_LOCATION ? MAX_TIME_FOR_WAITING_FCM_RESPONSE_LOCATION_CREATE : MAX_TIME_FOR_WAITING_FCM_RESPONSE;

        countDownTimer = new CountDownTimer(counter == -1 ? delay : counter, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.i(TAG_SERVICE, "AppService - timer: " + millisUntilFinished / 1000);
                counter = millisUntilFinished;
                sendCounterProgress(AppService.this.timerType, AppService.this.counter);
            }

            public void onFinish() {
                Log.i(TAG_SERVICE, "AppService - onFinish()");
                sendCounterProgress(AppService.this.timerType, SERVICE_COUNTDOWN_IS_OVER);
                counter = SERVICE_COUNTDOWN_IS_OFF;
                timerType = SERVICE_TIMER_TYPE_NONE;
            }
        }.start();
    }

    public void stop() {
        Log.i(TAG_SERVICE, "***************** AppService - stop() *****************");
        if (this.countDownTimer != null) this.countDownTimer.cancel();
        this.countDownTimer = null;
        counter = SERVICE_COUNTDOWN_IS_OFF;
        timerType = SERVICE_TIMER_TYPE_NONE;
    }

    private void sendCounterProgress(int timerType, long progress) {
        Intent intent = new Intent(ACTION_SERVICE_PROGRESS);
        intent.putExtra(KEY_SERVICE_TIMER_TYPE, timerType);
        intent.putExtra(KEY_SERVICE_PROGRESS, progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void setRequestStopService() {
        this.isRequestStopService = true;
        stop();
    }
}
