package com.lamnn.wego.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.lamnn.wego.R;
import com.lamnn.wego.broadcast.ComingNotificationReceiver;
import com.lamnn.wego.broadcast.NotificationReceiver;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.screen.details.info_user.InfoUserActivity;
import com.lamnn.wego.utils.GlideApp;

import static com.lamnn.wego.screen.details.info_user.InfoUserActivity.CHANNEL_ID;
import static com.lamnn.wego.screen.details.info_user.InfoUserActivity.EXTRA_USER;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String ACTION_NOTIFICATION_BUTTON_CLICK = "ACTION_NOTIFICATION_BUTTON_CLICK";
    private String EXTRA_BUTTON_CLICKED = "EXTRA_BUTTON_CLICKED";
    private String EXTRA_BUTTON_DIRECTION = "EXTRA_BUTTON_DIRECTION";
    private String EXTRA_BUTTON_CALL = "EXTRA_BUTTON_CALL";
    private String EXTRA_ACTION = "EXTRA_ACTION";
    private String EXTRA_EVENT = "EXTRA_EVENT";
    private NotificationTarget notificationTarget;
    private int NOTIFICATION_ID = 1;
    private User mUser;
    private Event mEvent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Gson gson = new Gson();
            mUser = gson.fromJson(remoteMessage.getData().get("user"), User.class);
            mEvent = gson.fromJson(remoteMessage.getData().get("event"), Event.class);
            Log.d("OK", "onMessageReceived: ok");
            if (!FirebaseAuth.getInstance().getUid().equals(mEvent.getUserId())) {
                showNotification();
            }
            if (FirebaseAuth.getInstance().getUid().equals(mEvent.getUserId())
                    && mEvent.getStatus().equals("waiting")) {
                if (mEvent.getComingUsers() != null && mEvent.getComingUsers().size() > 0) {
                    showComingNotification();
                }
            }
        }
        if (remoteMessage.getNotification() != null) {

        }
    }

    private PendingIntent onButtonNotificationClick(@IdRes int id) {
        Intent intent = new Intent(ACTION_NOTIFICATION_BUTTON_CLICK);
        intent.putExtra(EXTRA_BUTTON_CLICKED, id);
        return PendingIntent.getBroadcast(this, id, intent, 0);
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
