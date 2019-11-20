package com.lamnn.wego.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.screen.details.info_user.InfoUserActivity;

public class ComingNotificationReceiver extends BroadcastReceiver {
    private static final String EXTRA_EVENT = "EXTRA_EVENT";
    private Event mEvent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            mEvent = intent.getExtras().getParcelable(EXTRA_EVENT);
            UserLocation userLocation = new UserLocation();
            userLocation.setUid(mEvent.getUserId());
            context.startActivity(InfoUserActivity.getIntent(context, userLocation));
        }
    }
}
