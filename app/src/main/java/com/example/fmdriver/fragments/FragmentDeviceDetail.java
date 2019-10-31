package com.example.fmdriver.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fmdriver.MainActivity;
import com.example.fmdriver.R;
import com.example.fmdriver.customViews.DialogInfo;
import com.example.fmdriver.customViews.DialogTextInput;
import com.example.fmdriver.listeners.OnFragmentLoadClosedListener;
import com.example.fmdriver.listeners.OnFragmentLoadShowedListener;
import com.example.fmdriver.listeners.OnInputInsertedListener;
import com.example.fmdriver.objects.Device;
import com.example.fmdriver.retrofit.ApiDatabase;
import com.example.fmdriver.retrofit.ControllerDatabase;
import com.example.fmdriver.retrofit.responses.ResponseUpdateDevice;
import com.example.fmdriver.utils.AppConstants;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@EFragment(R.layout.fragment_device_detail)
public class FragmentDeviceDetail extends Fragment implements AppConstants {

    @ViewById
    TextView
            labelDeviceName,
            labelDeviceDescription,
            labelDate,
            labelAndroidId,
            labelDeviceId,
            labelToken;

    @ViewById
    ImageView imgEditDescription;

    MainActivity activity;

    @FragmentArg
    Device device;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            activity = (MainActivity) context;
        }
    }

    @AfterViews
    void afterViews() {
        labelDeviceName.setText(device.getName());
        labelDeviceDescription.setText(device.getDescription());
        labelDate.setText(device.getDate());
        labelAndroidId.setText(device.getDeviceIdentification().getAndroidId());
        labelDeviceId.setText(device.getDeviceIdentification().getDeviceId());
        labelToken.setText(device.getToken());
    }

    @Click(R.id.imgEditDescription)
    void clickImgEditDescription() {
        DialogTextInput.createDialog(activity)
                .setTitle("Popis zařízení")
                .setMessage("Vlož popis zařízení:")
                .setInput(device.getDescription())
                .setListenerInput(new OnInputInsertedListener() {
                    @Override
                    public void onInputInserted(String input) {
                        ApiDatabase api = ControllerDatabase.getRetrofitInstance().create(ApiDatabase.class);
                        final Call<ResponseUpdateDevice> call = api.updateDeviceDescription("" + device.getId(), input);

                        activity.showFrgmentLoad("Odesílám změny...", 0, new OnFragmentLoadShowedListener() {
                            @Override
                            public void onFragmentLoadShowed() {
                                call.enqueue(new Callback<ResponseUpdateDevice>() {
                                    @Override
                                    public void onResponse(Call<ResponseUpdateDevice> call, final Response<ResponseUpdateDevice> response) {
                                        if (response.isSuccessful()) {

                                            /*
                                            try {
                                                Log.i(TAG_DB, response.body().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            */

                                            final ResponseUpdateDevice responseUpdateDevice = response.body();

                                            if (responseUpdateDevice.getMessage() != null) {
                                                DialogInfo.createDialog(activity).setTitle("Chyba").setMessage(responseUpdateDevice.getMessage()).show();
                                                return;
                                            }

                                            final Device updatedDevice = responseUpdateDevice.getResponseDevice().toDevice();
                                            int updatedDevicePositionInList = activity.getDevicePositionInList(updatedDevice.getId());

                                            if (updatedDevicePositionInList > -1)
                                                activity.getRegisteredDevices().set(updatedDevicePositionInList, updatedDevice);


                                            activity.closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                                @Override
                                                public void onFragmentLoadClosed() {
                                                    activity.appendLog("Aktualizace popisu proběhla v pořádku", true);
                                                    update(updatedDevice);

                                                    FragmentDevices fragmentDevices = (FragmentDevices) activity.fragmentManager.findFragmentByTag("FragmentDevices_");
                                                    if (fragmentDevices != null) fragmentDevices.getAdapter().notifyDataSetChanged();
                                                }
                                            });
                                        } else {
                                            try {
                                                Log.i(TAG_DB, response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            activity.closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                                @Override
                                                public void onFragmentLoadClosed() {
                                                    activity.appendLog("Nepodařilo se aktualizovat popis zařízení. (CODE " + response.code() + ")", true, R.color.colorMessageInLog);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseUpdateDevice> call, final Throwable t) {
                                        activity.closeFragmentLoad(new OnFragmentLoadClosedListener() {
                                            @Override
                                            public void onFragmentLoadClosed() {
                                                activity.appendLog("Nepodařilo se aktualizovat popis zařízení." + t.getMessage(), true, R.color.colorMessageInLog);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }).show();
    }

    public void update(Device device) {
        this.device = device;
        afterViews();
    }
}
