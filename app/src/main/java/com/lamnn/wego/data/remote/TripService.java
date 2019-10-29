package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TripService {
    @POST("createTrip")
    Call<Trip> createTrip(@Body Trip trip);

    @POST("updateTrip")
    Call<Trip> updateTrip(@Body Trip trip);

    @POST("outTrip")
    Call<Boolean> outTrip(@Body User user);

    @POST("getMyTrips")
    Call<List<Trip>> getMyTrips(@Body User user);

    @POST("getTrip")
    Call<Trip> getTrip(@Body User user);

    @POST("switchTrip")
    Call<User> switchTrip(@Body User user);

    @POST("getListMember")
    Call<List<UserLocation>> getListMember(@Body User user);
}
