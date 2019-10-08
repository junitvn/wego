package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("updateUser")
    Call<User> updateUser(@Body User user);

    @POST("updateStatus")
    Call<Boolean> updateStatus(@Body User user);
}
