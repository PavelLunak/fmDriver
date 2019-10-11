package com.example.fmdriver.retrofit;

import com.example.fmdriver.retrofit.responses.ResponseAllCheckedPositions;
import com.example.fmdriver.retrofit.responses.ResponseDeletePosition;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiDatabase {

    @GET("checked_positions.php?all_checked_positions")
    Call<ResponseAllCheckedPositions> getAllCheckedPositions(@Query("fm") int fm);

    @GET("checked_positions.php")
    Call<ResponseBody> deleteCheckedPosition(@Query("delete_checked_position") String id);

    @GET("checked_positions.php?delete_all_checked_positions")
    Call<ResponseDeletePosition> deleteAllCheckedPositions();
}
