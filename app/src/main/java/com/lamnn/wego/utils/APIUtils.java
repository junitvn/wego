package com.lamnn.wego.utils;

import com.lamnn.wego.data.remote.DirectionService;
import com.lamnn.wego.data.remote.EventService;
import com.lamnn.wego.data.remote.JoinTripService;
import com.lamnn.wego.data.remote.PlaceService;
import com.lamnn.wego.data.remote.RetrofitClient;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.data.remote.UpdateLocationService;
import com.lamnn.wego.data.remote.UserService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class APIUtils {
    public static final String PLACE_BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/";
    public static final String DIRECTION_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    public static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    public static final String FUNCTION_BASE_URL = "https://us-central1-wego-af401.cloudfunctions.net/";


    public static PlaceService getPlaceService() {
        return RetrofitClient.getClient(PLACE_BASE_URL).create(PlaceService.class);
    }

    public static DirectionService getDirectionService() {
        RetrofitClient.reset();
        return RetrofitClient.getClient(DIRECTION_BASE_URL).create(DirectionService.class);
    }

    public static TripService getTripService() {
        RetrofitClient.reset();
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(logging)
//                .build();
        return RetrofitClient.getClient(FUNCTION_BASE_URL).create(TripService.class);
    }

    public static UpdateLocationService getUpdateLocationService() {
        RetrofitClient.reset();
        return RetrofitClient.getClient(FUNCTION_BASE_URL).create(UpdateLocationService.class);
    }

    public static JoinTripService getJoinTripService() {
        RetrofitClient.reset();
        return RetrofitClient.getClient(FUNCTION_BASE_URL).create(JoinTripService.class);
    }
    public static UserService getUserService() {
        RetrofitClient.reset();
        return RetrofitClient.getClient(FUNCTION_BASE_URL).create(UserService.class);
    }
    public static EventService getEventService() {
        RetrofitClient.reset();
        return RetrofitClient.getClient(FUNCTION_BASE_URL).create(EventService.class);
    }

}
