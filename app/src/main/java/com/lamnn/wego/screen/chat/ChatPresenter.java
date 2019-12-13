package com.lamnn.wego.screen.chat;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lamnn.wego.data.model.GroupChannel;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.data.model.UserMessage;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.data.remote.ChatService;
import com.lamnn.wego.utils.APIUtils;
import com.lamnn.wego.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatPresenter implements ChatContract.Presenter {
    private Context mContext;
    private ChatContract.View mView;
    private FirebaseFirestore mFirestore;
    private ChatService mChatService;
    private String TAG = "TAG";

    public ChatPresenter(Context context, ChatContract.View view) {
        mContext = context;
        mView = view;
        mFirestore = FirebaseFirestore.getInstance();
        mChatService = APIUtils.getChatService();
    }

    @Override
    public void getGroupData(User user) {
        mChatService.getGroupByUser(user).enqueue(new Callback<List<GroupChannel>>() {
            @Override
            public void onResponse(Call<List<GroupChannel>> call, Response<List<GroupChannel>> response) {
                mView.showGroups((ArrayList<GroupChannel>) response.body());
            }

            @Override
            public void onFailure(Call<List<GroupChannel>> call, Throwable t) {

            }
        });
    }

    private void getUserChannelData(User user) {
        mFirestore.collection("user_channel")
                .whereArrayContains("member_uid", user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    List<UserChannel> userChannels = new ArrayList<>();
                    UserChannel userChannel;
                    UserMessage lastMessage;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(doc.getData());
                        userChannel = gson.fromJson(jsonElement, UserChannel.class);
                        if (doc.getData().get("last_message") != null) {
                            JsonElement lastMessageChannel = gson.toJsonTree(doc.getData().get("last_message"));
                            lastMessage = gson.fromJson(lastMessageChannel, UserMessage.class);
                            JsonObject jsonObject = lastMessageChannel.getAsJsonObject();
                            Timestamp timestamp = gson.fromJson(jsonObject.get("time_stamp"), Timestamp.class);
                            lastMessage.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                            userChannel.setLastMessage(lastMessage);
                        }
                        userChannel.setChannelId(doc.getId());
                        userChannels.add(userChannel);
                    }
                    mView.showUserChannels((ArrayList<UserChannel>) userChannels);
                }
            }
        });
    }

    private void getFriendsData(User user) {
        final ArrayList<User> users = new ArrayList<>();
        mFirestore.collection("users")
                .document(user.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(snapshot.getData());
                            final User user = gson.fromJson(jsonElement, User.class);
                            if (user.getFriends() != null && user.getFriends().size() != 0) {
                                for (String uid : user.getFriends()) {
                                    mFirestore.collection("users")
                                            .document(uid)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        Gson gson = new Gson();
                                                        JsonElement jsonElement = gson.toJsonTree(task.getResult().getData());
                                                        User user = gson.fromJson(jsonElement, User.class);
                                                        users.add(user);
                                                    }
                                                    if (users.size() == user.getFriends().size()) {
                                                        mView.showFriends(users);
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                        }
                    }
                });
    }

    @Override
    public void getData(User user) {
        getGroupData(user);
        getUserChannelData(user);
        getFriendsData(user);
    }
}
