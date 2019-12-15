package com.lamnn.wego.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.lamnn.wego.R;
import com.lamnn.wego.broadcast.MessageBroadcastReceiver;
import com.lamnn.wego.broadcast.NotificationReceiver;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.data.model.Invitation;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.UserMessage;
import com.lamnn.wego.screen.conversation.ConversationActivity;
import com.lamnn.wego.screen.info.info_user.InfoUserActivity;
import com.lamnn.wego.utils.GlideApp;

import static com.lamnn.wego.broadcast.MessageBroadcastReceiver.REPLY_ACTION;
import static com.lamnn.wego.screen.info.info_user.InfoUserActivity.CHANNEL_ID;
import static com.lamnn.wego.screen.map.MapsActivity.DISTANCE_CHANNEL_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String KEY_TEXT_REPLY = "key_text_reply";
    public static final String KEY_NOTIFICATION_ID = "KEY_NOTIFICATION_ID";
    public static final String EXTRA_MESSAGE_NOTIFICATION = "EXTRA_MESSAGE_NOTIFICATION";
    public static final String EXTRA_MESSAGE_TYPE = "EXTRA_MESSAGE_TYPE";

    private String EXTRA_BUTTON_DIRECTION = "EXTRA_BUTTON_DIRECTION";
    private String EXTRA_BUTTON_CALL = "EXTRA_BUTTON_CALL";
    private String EXTRA_BUTTON_ALLOW = "EXTRA_BUTTON_ALLOW";
    private String EXTRA_BUTTON_DENY = "EXTRA_BUTTON_DENY";
    private String EXTRA_ACTION = "EXTRA_ACTION";
    private String EXTRA_EVENT = "EXTRA_EVENT";
    private String EXTRA_INVITATION = "EXTRA_INVITATION";
    private NotificationTarget notificationTarget;
    private int NOTIFICATION_ID = 1;
    private User mUser;
    private Event mEvent;
    private UserMessage mUserMessage;
    private GroupMessage mGroupMessage;
    private String mCurrentId;
    private UserLocation mLastMember;
    private Invitation mInvitation;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Gson gson = new Gson();
        mCurrentId = FirebaseAuth.getInstance().getUid();
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getFrom().startsWith("/topics/UM")) {
                mUserMessage = gson.fromJson(remoteMessage.getData().get("message"), UserMessage.class);
                if (!mUserMessage.getSender().getUid().equals(mCurrentId))
                    showMessageNotification("user");
            } else if (remoteMessage.getFrom().startsWith("/topics/GM")) {
                mGroupMessage = gson.fromJson(remoteMessage.getData().get("message"), GroupMessage.class);
                if (!mGroupMessage.getSender().getUid().equals(mCurrentId))
                    showMessageNotification("group");
            } else if (remoteMessage.getFrom().startsWith("/topics/DI")) {
                mLastMember = gson.fromJson(remoteMessage.getData().get("last_member"), UserLocation.class);
                showDistanceNotification();
            } else if (remoteMessage.getFrom().startsWith("/topics/IN")) {
                mInvitation = gson.fromJson(remoteMessage.getData().get("invitation"), Invitation.class);
                showInvitationNotification();
            } else {
                mUser = gson.fromJson(remoteMessage.getData().get("user"), User.class);
                mEvent = gson.fromJson(remoteMessage.getData().get("event"), Event.class);
                Log.d("OK", "onMessageReceived: ok");
                String diff = remoteMessage.getData().get("difference");
                if (!mCurrentId.equals(mEvent.getUserId())
                        && diff.equals("")) {
                    showNotification();
                }
                if (mCurrentId.equals(mEvent.getUserId())
                        && mEvent.getStatus().equals("waiting")) {
                    if (mEvent.getComingUsers() != null && mEvent.getComingUsers().size() > 0) {
                        showComingNotification();
                    }
                }
            }
        }
        if (remoteMessage.getNotification() != null) {

        }
    }

    private void showInvitationNotification() {
        RemoteViews collapseView = new RemoteViews(getPackageName(), R.layout.notification_invitation);
        Intent clickIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        clickIntent.putExtra(EXTRA_INVITATION, mInvitation);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
                0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intentActionDirection = new Intent(getApplicationContext(), NotificationReceiver.class);
        intentActionDirection.putExtra(EXTRA_ACTION, EXTRA_BUTTON_ALLOW);
        intentActionDirection.putExtra(EXTRA_INVITATION, mInvitation);
        PendingIntent pendingIntentActionDirection =
                PendingIntent.getBroadcast(getApplicationContext(),
                        1, intentActionDirection, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentActionCall = new Intent(getApplicationContext(), NotificationReceiver.class);
        intentActionCall.putExtra(EXTRA_ACTION, EXTRA_BUTTON_DENY);
        intentActionCall.putExtra(EXTRA_INVITATION, mInvitation);
        PendingIntent pendingIntentActionCall =
                PendingIntent.getBroadcast(getApplicationContext(),
                        2, intentActionCall, PendingIntent.FLAG_UPDATE_CURRENT);

        collapseView.setTextViewText(R.id.text_noti_title, mInvitation.getCreator().getName() + getString(R.string.text_intvite_you_to_join) + mInvitation.getTrip().getName());
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fox)
                .setCustomContentView(collapseView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .addAction(R.drawable.ic_fox, getString(R.string.action_join), pendingIntentActionDirection)
                .addAction(R.drawable.ic_bee, getString(R.string.action_deny), pendingIntentActionCall)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showMessageNotification(String type) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fox)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (type.equals("user")) {
            notification.setContentTitle(mUserMessage.getSender().getName())
                    .setContentText(mUserMessage.getContent());
        } else {
            notification.setContentTitle(mGroupMessage.getGroupName())
                    .setContentText(mGroupMessage.getSender().getName() + ": " + mGroupMessage.getContent());
        }
        String replyLabel = getString(R.string.type_to_repley);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build();
        PendingIntent resultPendingIntent = getPendingIntent(type);
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_send_black_24dp, getString(R.string.reply_label), resultPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        notification.addAction(replyAction);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);

        mNotificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    private PendingIntent getPendingIntent(String type) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(getApplicationContext(), MessageBroadcastReceiver.class);
            intent.setAction(REPLY_ACTION);
            intent.putExtra(KEY_NOTIFICATION_ID, NOTIFICATION_ID);
            intent.putExtra(EXTRA_MESSAGE_TYPE, type);
            if (type.equals("user"))
                intent.putExtra(EXTRA_MESSAGE_NOTIFICATION, mUserMessage);
            else
                intent.putExtra(EXTRA_MESSAGE_NOTIFICATION, mGroupMessage);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return PendingIntent.getBroadcast(getApplicationContext(), 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            intent = new Intent(getApplicationContext(), ConversationActivity.class);
            intent.setAction(REPLY_ACTION);
            intent.putExtra(KEY_NOTIFICATION_ID, NOTIFICATION_ID);
            intent.putExtra(EXTRA_MESSAGE_TYPE, type);
            if (type.equals("user"))
                intent.putExtra(EXTRA_MESSAGE_NOTIFICATION, mUserMessage);
            else
                intent.putExtra(EXTRA_MESSAGE_NOTIFICATION, mGroupMessage);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return PendingIntent.getActivity(this, 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private void showDistanceNotification() {
        String contentText;
        if (mLastMember.getUid().equals(mCurrentId)) {
            contentText = getString(R.string.caution_distance_me);
        } else {
            contentText = mLastMember.getUser().getName() + getString(R.string.caution_distance_other);
        }
        Notification notification = new NotificationCompat.Builder(this, DISTANCE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fox)
                .setContentTitle(getString(R.string.caution))
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)   // heads-up
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }

    private void showComingNotification() {
        RemoteViews collapseView = new RemoteViews(getPackageName(), R.layout.notification_collapse);
        Intent clickIntent = new Intent(getApplicationContext(), InfoUserActivity.class);
        clickIntent.putExtra(EXTRA_EVENT, mEvent);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(this,
                0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        collapseView.setTextViewText(R.id.text_noti_title, mEvent.getTitle());
        collapseView.setTextViewText(R.id.text_noti_info, mEvent.getComingUsers().size() + getString(R.string.text_friends_coming));
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fox)
                .setCustomContentView(collapseView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();
        notification.contentIntent = clickPendingIntent;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showNotification() {
        RemoteViews collapseView = new RemoteViews(getPackageName(), R.layout.notification_collapse);
        RemoteViews expandedView = new RemoteViews(getPackageName(),
                R.layout.notification_expanded);

        Intent clickIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
        clickIntent.putExtra(EXTRA_EVENT, mEvent);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,
                0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intentActionDirection = new Intent(getApplicationContext(), NotificationReceiver.class);
        intentActionDirection.putExtra(EXTRA_ACTION, EXTRA_BUTTON_DIRECTION);
        intentActionDirection.putExtra(EXTRA_EVENT, mEvent);
        PendingIntent pendingIntentActionDirection =
                PendingIntent.getBroadcast(getApplicationContext(),
                        1, intentActionDirection, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentActionCall = new Intent(getApplicationContext(), NotificationReceiver.class);
        intentActionCall.putExtra(EXTRA_ACTION, EXTRA_BUTTON_CALL);
        intentActionCall.putExtra(EXTRA_EVENT, mEvent);
        PendingIntent pendingIntentActionCall =
                PendingIntent.getBroadcast(getApplicationContext(),
                        2, intentActionCall, PendingIntent.FLAG_UPDATE_CURRENT);

        collapseView.setTextViewText(R.id.text_noti_title, mUser.getName() + " " + getString(R.string.text_noti_need_help));
        switch (mEvent.getStatus()) {
            case "done":
                expandedView.setViewVisibility(R.id.text_view_expanded_done, View.VISIBLE);
                break;
            case "deleted":
                expandedView.setViewVisibility(R.id.text_view_expanded_cancel, View.VISIBLE);
                break;
        }
        expandedView.setTextViewText(R.id.text_view_expanded, mUser.getName() + ": " + mEvent.getTitle());
        expandedView.setImageViewResource(R.id.image_view_expanded, R.drawable.giphy);
        expandedView.setOnClickPendingIntent(R.id.image_view_expanded, clickPendingIntent);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_fox)
                .setCustomContentView(collapseView)
                .setCustomBigContentView(expandedView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .addAction(R.drawable.ic_fox, getString(R.string.action_direction), pendingIntentActionDirection)
                .addAction(R.drawable.ic_bee, getString(R.string.action_call), pendingIntentActionCall)
                .build();
        notificationTarget = new NotificationTarget(
                getApplicationContext(),
                R.id.image_view_expanded,
                expandedView,
                notification,
                NOTIFICATION_ID);
        if (mEvent.getPhotos() != null && mEvent.getPhotos().size() != 0) {
            GlideApp
                    .with(getApplicationContext())
                    .asBitmap()
                    .placeholder(R.drawable.giphy)
                    .load(mEvent.getPhotos().get(0))
                    .into(notificationTarget);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
