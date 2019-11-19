package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventService {
    @POST("createEvent")
    Call<Event> createEvent(@Body Event event);

    @POST("updateEvent")
    Call<Event> updateEvent(@Body Event event);

    @POST("getAllEvent")
    Call<List<Event>> getAllEvent(@Body User user);

}
