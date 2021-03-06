package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("updateUser")
    Call<User> updateUser(@Body User user);

    @POST("searchUserByName")
    Call<List<User>> searchUserByName(@Body User user);

    @POST("updateStatus")
    Call<Boolean> updateStatus(@Body UserLocation userLocation);

    @POST("addEventToUserLocation")
    Call<Boolean> addEventToUserLocation(@Body UserLocation userLocation);

    @POST("initUserLocation")
    Call<UserLocation> initUserLocation(@Body UserLocation userLocation);
}
