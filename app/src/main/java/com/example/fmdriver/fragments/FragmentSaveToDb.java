package com.example.fmdriver.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.customViews.DialogInfo;
import com.example.fmdriver.listeners.OnFragmentLoadClosedListener;
import com.example.fmdriver.listeners.OnFragmentLoadShowedListener;
import com.example.fmdriver.retrofit.objects.RequestSettingsSaveIntoDatabase;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;


public class FragmentSaveToDb extends Fragment implements AppConstants, View.OnClickListener {

    Switch switchEnableSave;
    EditText etInterval, etIntervalPositions, etCount;
    CheckBox chbInfinity;
    TextView btnStartSave, btnLoadSettings;
    ProgressBar progressSave, progressLoad;

    RadioButton
            rbIntervalSeconds,
            rbIntervalMinutes,
            rbIntervalHours,
            rbIntervalPositionsSeconds,
            rbIntervalPositionsMinutes,
            rbIntervalPositionsHours;

    MainActivity activity;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_save_to_db, container, false);

        switchEnableSave = (Switch) rootView.findViewById(R.id.switchEnableSave);
        etInterval = (EditText) rootView.findViewById(R.id.etInterval);
        etIntervalPositions = (EditText) rootView.findViewById(R.id.etIntervalPositions);
        etCount = (EditText) rootView.findViewById(R.id.etCount);
        rbIntervalSeconds = (RadioButton) rootView.findViewById(R.id.rbIntervalSeconds);
        rbIntervalMinutes = (RadioButton) rootView.findViewById(R.id.rbIntervalMinutes);
        rbIntervalHours = (RadioButton) rootView.findViewById(R.id.rbIntervalHours);
        rbIntervalPositionsSeconds = (RadioButton) rootView.findViewById(R.id.rbIntervalPositionsSeconds);
        rbIntervalPositionsMinutes = (RadioButton) rootView.findViewById(R.id.rbIntervalPositionsMinutes);
        rbIntervalPositionsHours = (RadioButton) rootView.findViewById(R.id.rbIntervalPositionsHours);
        chbInfinity = (CheckBox) rootView.findViewById(R.id.chbInfinity);
        btnStartSave = (TextView) rootView.findViewById(R.id.btnStartSave);
        btnLoadSettings = (TextView) rootView.findViewById(R.id.btnLoadSettings);
        progressSave = (ProgressBar) rootView.findViewById(R.id.progressSave);
        progressLoad = (ProgressBar) rootView.findViewById(R.id.progressLoad);

        btnLoadSettings.setOnClickListener(this);
        btnStartSave.setOnClickListener(this);
        chbInfinity.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (activity.isWaitingForResponseFromFcm) return;

        activity.showFrgmentLoad("Stahuji nastavení", new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                activity.startTimerService(SERVICE_TIMER_TYPE_SETTINGS);
                activity.sendRequestToFcm(FCM_REQUEST_TYPE_LOAD_SETTINGS, null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadSettings:
                loadSettings();
                break;
            case R.id.chbInfinity:
                etCount.setEnabled(!chbInfinity.isChecked());
                break;
            case R.id.btnStartSave:
                saveSettings();
                break;
        }
    }

    private void loadSettings() {
        if (activity.isWaitingForResponseFromFcm) return;
        Animators.animateButtonClick(btnLoadSettings);
        updateViewsAfterLoadClick(false);
        activity.startTimerService(AppConstants.SERVICE_TIMER_TYPE_SETTINGS);
        activity.sendRequestToFcm(FCM_REQUEST_TYPE_LOAD_SETTINGS, null);
    }

    private void saveSettings() {
        if (activity.isWaitingForResponseFromFcm) return;
        Animators.animateButtonClick(btnStartSave);
        updateViewsAfterSendClick(false);

        long interval = 0;
        int count = 0;
        int timeUnit = TIME_UNIT_SECONDS;

        long intervalPositons = 0;
        int timeUnitPositons = TIME_UNIT_SECONDS;

        try {
            if (rbIntervalHours.isChecked()) {
                interval = Long.parseLong(etInterval.getText().toString()) * 60 * 60 * 1000;
                timeUnit = TIME_UNIT_HOURS;
            } else if (rbIntervalMinutes.isChecked()) {
                interval = Long.parseLong(etInterval.getText().toString()) * 60 * 1000;
                timeUnit = TIME_UNIT_MINUTES;
            } else if (rbIntervalSeconds.isChecked()) {
                interval = Long.parseLong(etInterval.getText().toString()) * 1000;
                timeUnit = TIME_UNIT_SECONDS;
            }

            if (chbInfinity.isChecked()) count = COUNT_OF_LOCATIONS_INFINITY;
            else count = Integer.parseInt(etCount.getText().toString());


            if (rbIntervalPositionsHours.isChecked()) {
                intervalPositons = Long.parseLong(etIntervalPositions.getText().toString()) * 60 * 60 * 1000;
                timeUnitPositons = TIME_UNIT_HOURS;
            } else if (rbIntervalPositionsMinutes.isChecked()) {
                intervalPositons = Long.parseLong(etIntervalPositions.getText().toString()) * 60 * 1000;
                timeUnitPositons = TIME_UNIT_MINUTES;
            } else if (rbIntervalPositionsSeconds.isChecked()) {
                intervalPositons = Long.parseLong(etIntervalPositions.getText().toString()) * 1000;
                timeUnitPositons = TIME_UNIT_SECONDS;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            DialogInfo.createDialog(activity).setTitle("Chyba").setMessage("NumberFormatException").show();
            updateViewsAfterLoadClick(true);
            updateViewsAfterSendClick(true);
            return;
        }

        MainActivity.appPrefs.edit().savingToDatabaseEnabled().put(switchEnableSave.isChecked())
                .autoCheckedPositionSavingInterval().put(interval)
                .maxCountOfLocationChecked().put(count)
                .timeUnit().put(timeUnit)
                .locationInterval().put(intervalPositons)
                .locationIntervalTimeUnit().put(timeUnitPositons)
                .apply();

        RequestSettingsSaveIntoDatabase request = new RequestSettingsSaveIntoDatabase(
                MainActivity.appPrefs.fcmToken().get(),
                FCM_REQUEST_TYPE_SETTINGS_DATABASE,
                switchEnableSave.isChecked() ? 1 : 0,
                interval,
                count,
                timeUnit,
                intervalPositons,
                timeUnitPositons);

        activity.startTimerService(AppConstants.SERVICE_TIMER_TYPE_SETTINGS);
        activity.sendRequestToFcm(FCM_REQUEST_TYPE_SETTINGS_DATABASE, request);
    }

    public void afterLoaded() {
        activity.closeFragmentLoad(new OnFragmentLoadClosedListener() {
            @Override
            public void onFragmentLoadClosed() {
                updateViewsAfterSendClick(true);
                updateViewsAfterLoadClick(true);
                updatedata();
            }
        });
    }

    public void updateViewsAfterSendClick(boolean showBtn) {
        if (btnStartSave != null) btnStartSave.setBackgroundColor(activity.getResources().getColor(showBtn ?  R.color.colorPrimary : R.color.colorBtnDisabled));
        if (btnLoadSettings != null) btnLoadSettings.setBackgroundColor(activity.getResources().getColor(showBtn ?  R.color.colorPrimary : R.color.colorBtnDisabled));

        if (progressSave != null) progressSave.setVisibility(showBtn ? View.INVISIBLE : View.VISIBLE);
        if (progressLoad != null) progressLoad.setVisibility(View.INVISIBLE);
    }

    public void updateViewsAfterLoadClick(boolean showBtn) {
        if (btnLoadSettings != null) btnLoadSettings.setBackgroundColor(activity.getResources().getColor(showBtn ?  R.color.colorPrimary : R.color.colorBtnDisabled));
        if (btnStartSave != null) btnStartSave.setBackgroundColor(activity.getResources().getColor(showBtn ?  R.color.colorPrimary : R.color.colorBtnDisabled));

        if (progressLoad != null) progressLoad.setVisibility(showBtn ? View.INVISIBLE : View.VISIBLE);
        if (progressSave != null) progressSave.setVisibility(View.INVISIBLE);
    }

    public void updatedata() {
        if (switchEnableSave != null) switchEnableSave.setChecked(MainActivity.appPrefs.savingToDatabaseEnabled().get());

        switch (MainActivity.appPrefs.timeUnit().get()) {
            case TIME_UNIT_HOURS:
                if (rbIntervalHours != null) rbIntervalHours.setChecked(true);
                if (etInterval != null) etInterval.setText("" + MainActivity.appPrefs.autoCheckedPositionSavingInterval().get() / 1000 / 60 / 60);
                break;
            case TIME_UNIT_MINUTES:
                if (rbIntervalMinutes != null) rbIntervalMinutes.setChecked(true);
                if (etInterval != null) etInterval.setText("" + MainActivity.appPrefs.autoCheckedPositionSavingInterval().get() / 1000 / 60);
                break;
            case TIME_UNIT_SECONDS:
                if (rbIntervalSeconds != null) rbIntervalSeconds.setChecked(true);
                if (etInterval != null) etInterval.setText("" + MainActivity.appPrefs.autoCheckedPositionSavingInterval().get() / 1000);
                break;
        }

        switch (MainActivity.appPrefs.locationIntervalTimeUnit().get()) {
            case TIME_UNIT_HOURS:
                if (rbIntervalPositionsHours != null) rbIntervalPositionsHours.setChecked(true);
                if (etIntervalPositions != null) etIntervalPositions.setText("" + MainActivity.appPrefs.locationInterval().get() / 1000 / 60 / 60);
                break;
            case TIME_UNIT_MINUTES:
                if (rbIntervalPositionsMinutes != null) rbIntervalPositionsMinutes.setChecked(true);
                if (etIntervalPositions != null) etIntervalPositions.setText("" + MainActivity.appPrefs.locationInterval().get() / 1000 / 60);
                break;
            case TIME_UNIT_SECONDS:
                if (rbIntervalPositionsSeconds != null) rbIntervalPositionsSeconds.setChecked(true);
                if (etIntervalPositions != null) etIntervalPositions.setText("" + MainActivity.appPrefs.locationInterval().get() / 1000);
                break;
        }

        if (etCount != null) {
            if (MainActivity.appPrefs.maxCountOfLocationChecked().get() == -2) {
                etCount.setEnabled(false);
                chbInfinity.setChecked(true);
            } else {
                etCount.setEnabled(true);
                chbInfinity.setChecked(false);
                etCount.setText("" + MainActivity.appPrefs.maxCountOfLocationChecked().get());
            }
        }
    }
}
