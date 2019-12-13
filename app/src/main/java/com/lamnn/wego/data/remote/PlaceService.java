package com.lamnn.wego.data.remote;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.PlaceResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceService {
    @GET("json?query=hanoi+city+point+of+interest&language=vi&key=AIzaSyAGVGhyzB1hQcXpFmg9QCP6JMI8Qp-768Y")
    Call<PlaceResponse> getPlaces();

    @GET("json?language=vi")
    Call<PlaceResponse> searchPlace(@Query("query") String query,
                                    @Query("key") String key);

}

