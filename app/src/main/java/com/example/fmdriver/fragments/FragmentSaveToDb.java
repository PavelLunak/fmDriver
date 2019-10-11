package com.example.fmdriver.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
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
import com.example.fmdriver.retrofit.objects.RequestToFcmData;
import com.example.fmdriver.utils.Animators;
import com.example.fmdriver.utils.AppConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_save_to_db)
public class FragmentSaveToDb extends Fragment implements AppConstants {

    MainActivity activity;

    @ViewById
    Switch switchEnableSave;

    @ViewById
    EditText etInterval, etIntervalPositions, etCount;

    @ViewById
    RadioButton
            rbIntervalSeconds,
            rbIntervalMinutes,
            rbIntervalHours,
            rbIntervalPositionsSeconds,
            rbIntervalPositionsMinutes,
            rbIntervalPositionsHours;

    @ViewById
    CheckBox chbInfinity;

    @ViewById
    TextView btnStartSave, btnLoadSettings;

    @ViewById
    ProgressBar progressSave, progressLoad;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @AfterViews
    void afterViews() {
        if (activity.settingsTimer != null) return;

        activity.showFrgmentLoad("Stahuji nastavení", 0, new OnFragmentLoadShowedListener() {
            @Override
            public void onFragmentLoadShowed() {
                activity.startCountDownSettings();
                activity.sendRequestToFcm(FCM_REQUEST_TYPE_LOAD_SETTINGS, true, null);
            }
        });
    }

    @Click(R.id.btnLoadSettings)
    void clickStartLoad() {
        if (activity.settingsTimer != null) return;
        Animators.animateButtonClick(btnLoadSettings);
        updateViewsAfterLoadClick(false);
        activity.startCountDownSettings();
        activity.sendRequestToFcm(FCM_REQUEST_TYPE_LOAD_SETTINGS, true, null);
    }

    @Click(R.id.chbInfinity)
    void clickRbInfinity() {
        etCount.setEnabled(!chbInfinity.isChecked());
    }

    @Click(R.id.btnStartSave)
    void clickStartSave() {
        if (activity.settingsTimer != null) return;
        Animators.animateButtonClick(btnStartSave);
        updateViewsAfterSendClick(false);

        long interval = 0;
        int count = 0;
        int timeUnit = TIME_UNIT_SECONDS;

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
        } catch (NumberFormatException e) {
            e.printStackTrace();
            DialogInfo.createDialog(activity).setTitle("Chyba").setMessage("NumberFormatException").show();
            updateViewsAfterLoadClick(true);
            updateViewsAfterSendClick(true);
            return;
        }

        MainActivity.appPrefs.edit().savingToDatabaseEnabled().put(switchEnableSave.isChecked()).apply();
        MainActivity.appPrefs.edit().autoCheckedPositionSavingInterval().put(interval).apply();
        MainActivity.appPrefs.edit().maxCountOfLocationChecked().put(count).apply();
        MainActivity.appPrefs.edit().timeUnit().put(timeUnit).apply();

        RequestSettingsSaveIntoDatabase request = new RequestSettingsSaveIntoDatabase(
                MainActivity.appPrefs.fcmToken().get(),
                FCM_REQUEST_TYPE_SETTINGS_DATABASE,
                switchEnableSave.isChecked() ? 1 : 0,
                interval,
                count,
                timeUnit);

        activity.startCountDownSettings();
        activity.sendRequestToFcm(FCM_REQUEST_TYPE_SETTINGS_DATABASE, true, request);
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
