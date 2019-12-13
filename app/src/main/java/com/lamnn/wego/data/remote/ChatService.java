package com.lamnn.wego.data.remote;

import com.lamnn.wego.data.model.GroupChannel;
import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.data.model.UserMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatService {
    @POST("getGroupByUser")
    Call<List<GroupChannel>> getGroupByUser(@Body User user);

    @POST("sendGroupMessage")
    Call<Boolean> sendGroupMessage(@Body GroupMessage groupMessage);

    @POST("sendUserMessage")
    Call<Boolean> sendUserMessage(@Body UserMessage userMessage);

    @POST("createUserChannel")
    Call<UserChannel> createUserChannel(@Body UserChannel userChannel);

}
