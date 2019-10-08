package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UpdateLocationService {
    @POST("updateLocation")
    Call<List<User>> updateLocation(@Body User user);
}
