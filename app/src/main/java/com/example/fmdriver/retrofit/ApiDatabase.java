package com.example.fmdriver.retrofit;

import com.example.fmdriver.retrofit.responses.ResponseAllCheckedPositions;
import com.example.fmdriver.retrofit.responses.ResponseAllDevices;
import com.example.fmdriver.retrofit.responses.ResponseDeletePosition;
import com.example.fmdriver.retrofit.responses.ResponseDevice;
import com.example.fmdriver.retrofit.responses.ResponseUpdateDevice;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiDatabase {

    @GET("checked_positions.php?all_checked_positions")
    Call<ResponseAllCheckedPositions> getAllCheckedPositions(@Query("device_id") int deviceId);

    @GET("checked_positions.php")
    Call<ResponseBody> deleteCheckedPosition(@Query("delete_checked_position") String id);

    @GET("checked_positions.php?delete_all_checked_positions")
    Call<ResponseDeletePosition> deleteAllCheckedPositions(@Query("device_id") int deviceId);

    @GET("devices.php?all_devices")
    Call<ResponseAllDevices> getAllDevices();

    @GET("devices.php")
    Call<ResponseAllCheckedPositions> getDevice(@Query("id") String id);

    @GET("devices.php?update_description")
    Call<ResponseUpdateDevice> updateDeviceDescription(@Query("deviceId") String id, @Query("desc") String description);

    @GET("devices.php?delete")
    Call<ResponseAllCheckedPositions> deleteDevice(@Query("id") String id);
}
