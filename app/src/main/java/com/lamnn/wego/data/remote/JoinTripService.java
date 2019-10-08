package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JoinTripService {
    @POST("joinTrip")
    Call<Boolean> joinTrip(@Body User user);
}
