package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.route.RouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DirectionService {
    @GET("json?avoid=highways")
    Call<RouteResponse> getRoute(@Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("key") String apiKey);
}
