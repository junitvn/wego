package com.lamnn.wego.screen.conversation;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.data.model.UserMessage;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.data.remote.ChatService;
import com.lamnn.wego.utils.APIUtils;
import com.lamnn.wego.utils.comparator.GroupTimeStampComparator;
import com.lamnn.wego.utils.comparator.UserTimeStampComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationPresenter implements ConversationContract.Presenter {
    private Context mContext;
    private ConversationContract.View mView;
    private FirebaseFirestore mFirestore;
    private ChatService mChatService;

    public ConversationPresenter(Context context, ConversationContract.View view) {
        mContext = context;
        mView = view;
        mFirestore = FirebaseFirestore.getInstance();
        mChatService = APIUtils.getChatService();
    }

    @Override
    public void getConversationGroupData(final String groupId) {
        mFirestore.collection("group_message").whereEqualTo("group_id", groupId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) return;
                List<GroupMessage> groupMessages = new ArrayList<>();
                GroupMessage groupMessage;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(doc.getData());
                    groupMessage = gson.fromJson(jsonElement, GroupMessage.class);
                    Timestamp timestamp = (Timestamp) doc.getData().get("time_stamp");
                    groupMessage.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                    groupMessages.add(groupMessage);
                }
                Collections.sort(groupMessages, new GroupTimeStampComparator());
                mView.showConversation(groupMessages);
            }
        });
    }

    @Override
    public void sendGroupMessage(GroupMessage groupMessage) {
        mChatService.sendGroupMessage(groupMessage).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    mView.sendMessageSuccess();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                mView.sendMessageFail();
            }
        });
    }

    @Override
    public void sendUserMessage(UserMessage userMessage) {
        mChatService.sendUserMessage(userMessage).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    mView.sendMessageSuccess();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                mView.sendMessageFail();
            }
        });
    }

    @Override
    public void createUserChannel(UserChannel userChannel) {
        userChannel.setUserId(FirebaseAuth.getInstance().getUid());
        mChatService.createUserChannel(userChannel).enqueue(new Callback<UserChannel>() {
            @Override
            public void onResponse(Call<UserChannel> call, Response<UserChannel> response) {
                mView.updateUserChannel(response.body());
            }

            @Override
            public void onFailure(Call<UserChannel> call, Throwable t) {

            }
        });
    }

    @Override
    public void getConversationUserData(String channelId) {
        mFirestore.collection("user_message").whereEqualTo("channel_id", channelId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) return;
                List<UserMessage> userMessages = new ArrayList<>();
                UserMessage userMessage;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(doc.getData());
                    userMessage = gson.fromJson(jsonElement, UserMessage.class);
                    Timestamp timestamp = (Timestamp) doc.getData().get("time_stamp");
                    userMessage.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                    userMessages.add(userMessage);
                }
                Collections.sort(userMessages, new UserTimeStampComparator());
                mView.showUserConversation(userMessages);
            }
        });
    }

}
