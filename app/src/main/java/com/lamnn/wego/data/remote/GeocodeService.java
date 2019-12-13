package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.geocode.GeocodeResponse;
import com.lamnn.wego.data.model.route.RouteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodeService {
    @GET("json?")
    Call<GeocodeResponse> getPlace(@Query("latlng") String latlng,
                                   @Query("key") String apiKey);
}
