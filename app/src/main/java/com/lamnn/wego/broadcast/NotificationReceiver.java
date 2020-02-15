package com.lamnn.wego.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.Invitation;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.remote.JoinTripService;
import com.lamnn.wego.screen.info.info_member.InfoMemberActivity;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.trip.join_trip.JoinTripActivity;
import com.lamnn.wego.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String EXTRA_BUTTON_DIRECTION = "EXTRA_BUTTON_DIRECTION";
    private static final String EXTRA_BUTTON_CALL = "EXTRA_BUTTON_CALL";
    private static final String EXTRA_BUTTON_ALLOW = "EXTRA_BUTTON_ALLOW";
    private static final String EXTRA_BUTTON_DENY = "EXTRA_BUTTON_DENY";
    private static final String EXTRA_ACTION = "EXTRA_ACTION";
    private static final String EXTRA_EVENT = "EXTRA_EVENT";
    private static final String EXTRA_INVITATION = "EXTRA_INVITATION";
    private static final String PACKAGE = "com.google.android.apps.maps";
    private static final String PRE_QUERY = "google.navigation:q=";
    private static final String MODE = "&mode=l&&avoid=thf";
    private Event mEvent;
    private Invitation mInvitation;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            mEvent = intent.getExtras().getParcelable(EXTRA_EVENT);
            mInvitation = intent.getExtras().getParcelable(EXTRA_INVITATION);
            if(mEvent != null){
                UserLocation userLocation = new UserLocation();
                userLocation.setUid(mEvent.getUserId());
                context.startActivity(InfoMemberActivity.getIntent(context, userLocation));
            }
        }
        String action = intent.getStringExtra(EXTRA_ACTION);
        if (action != null) {
            switch (action) {
                case EXTRA_BUTTON_DIRECTION:
                    performAction1(context);
                    break;
                case EXTRA_BUTTON_CALL:
                    performAction2(context);
                    break;
                case EXTRA_BUTTON_ALLOW:
                    joinTrip(context);
                    break;
                case EXTRA_BUTTON_DENY:
                    deny();
                    break;
            }
        }
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    private void deny() {

    }

    private void joinTrip(final Context context) {
        User user = new User();
        user.setActiveTrip(mInvitation.getTrip().getCode());
        user.setUid(mInvitation.getReceiverId());
        JoinTripService joinTripService = APIUtils.getJoinTripService();
        joinTripService.joinTrip(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body() == null || response.body() == false) {
                } else {
                    Intent intent = MapsActivity.getIntent(getApplicationContext());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    private void performAction1(Context context) {
        Uri gmmIntentUri = Uri.parse(PRE_QUERY + mEvent.getLocation().getLat()
                + "," + mEvent.getLocation().getLng() + MODE);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(PACKAGE);
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mapIntent);
    }

    private void performAction2(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tell:" + mEvent.getUser().getPhone()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
