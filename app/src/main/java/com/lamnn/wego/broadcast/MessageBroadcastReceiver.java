package com.lamnn.wego.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserMessage;
import com.lamnn.wego.data.remote.ChatService;
import com.lamnn.wego.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.screen.info.info_user.InfoUserActivity.CHANNEL_ID;
import static com.lamnn.wego.service.FirebaseMessagingListenerService.EXTRA_MESSAGE_NOTIFICATION;
import static com.lamnn.wego.service.FirebaseMessagingListenerService.EXTRA_MESSAGE_TYPE;
import static com.lamnn.wego.service.FirebaseMessagingListenerService.KEY_TEXT_REPLY;

public class MessageBroadcastReceiver extends BroadcastReceiver {
    public static final String REPLY_ACTION = "REPLY_ACTION";
    private ChatService mChatService;
    private UserMessage mUserMessage;
    private GroupMessage mGroupMessage;
    private String mType;
    private int NOTIFICATION_ID = 1;
    private Context mContext;
    private String mMessage;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        User user = getCurrentUser();
        mChatService = APIUtils.getChatService();
        if (REPLY_ACTION.equals(intent.getAction())) {
            CharSequence message = getReplyMessage(intent);
            mType = intent.getStringExtra(EXTRA_MESSAGE_TYPE);
            mMessage = message.toString();
            if (mType.equals("user")) {
                mUserMessage = intent.getExtras().getParcelable(EXTRA_MESSAGE_NOTIFICATION);
                mUserMessage.setContent(message.toString());
                mUserMessage.setSender(user);
                sendUserMessage(mUserMessage);
            } else {
                mGroupMessage = intent.getExtras().getParcelable(EXTRA_MESSAGE_NOTIFICATION);
                mGroupMessage.setContent(message.toString());
                mGroupMessage.setSender(user);
                sendGroupMessage(mGroupMessage);
            }
        }
    }

    private User getCurrentUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        User user = new User();
        user.setUid(auth.getUid());
        if (auth.getCurrentUser().getDisplayName() != null) {
            user.setName(auth.getCurrentUser().getDisplayName());
        }
        if (auth.getCurrentUser().getPhotoUrl() != null) {
            user.setPhotoUri(auth.getCurrentUser().getPhotoUrl().toString());
        }
        if (auth.getCurrentUser().getPhoneNumber() != null) {
            user.setPhone(auth.getCurrentUser().getPhoneNumber());
        }
        return user;
    }

    private CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

    private void sendGroupMessage(GroupMessage groupMessage) {
        mChatService.sendGroupMessage(groupMessage).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    notifyWhenSent();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    public void sendUserMessage(UserMessage userMessage) {
        mChatService.sendUserMessage(userMessage).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    notifyWhenSent();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    private void notifyWhenSent() {
        try {
            NotificationCompat.Builder notification;
            notification = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_fox)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(mContext);
            if (mType.equals("user")) {
                notification.setContentTitle(mUserMessage.getSender().getName())
                        .setContentText(mUserMessage.getContent())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(mContext.getString(R.string.text_me) + ": " + mMessage));
            } else {
                notification.setContentTitle(mGroupMessage.getGroupName())
                        .setContentText(mGroupMessage.getSender().getName() + ": " + mGroupMessage.getContent())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(mContext.getString(R.string.text_me) + ": " + mMessage));
            }
            mNotificationManager.notify(NOTIFICATION_ID, notification.build());
        } catch (IllegalMonitorStateException e) {
        }
    }
}
