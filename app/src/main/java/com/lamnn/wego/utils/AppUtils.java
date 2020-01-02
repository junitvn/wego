package com.lamnn.wego.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.User;

public class AppUtils {
    public static final String CAR_ICON_URI = "https://firebasestorage.googleapis.com/v0/b/wego-af401.appspot.com/o/event_photo%2Fbreakdown.png?alt=media&token=338e6f81-5f4e-4a02-8263-1bca031cdf81";
    public static final String GAS_ICON_URI = "https://firebasestorage.googleapis.com/v0/b/wego-af401.appspot.com/o/event_photo%2Frefuel.png?alt=media&token=da1ebd4c-01bd-4db2-909a-ec926841f2e2";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_DELETED = "deleted";
    public static final String STATUS_ONLINE = "online";
    public static final String STATUS_GOING = "going";
    public static final String STATUS_CANCEL = "cancel";
    public static final String STATUS_INVITED = "invited";
    public static final String STATUS_JOINED = "joined";

    public static final String TYPE_CAR = "car";
    public static final String TYPE_GAS = "gas";
    public static final String TYPE_COMING = "coming";
    public static final String TYPE_WAITING = "waiting";
    public static final String TYPE_WAYPOINT = "waypoint";
    public static final String TYPE_CHECK_IN = "check-in";
    public static final String TYPE_END = "end";
    public static final String TYPE_START = "start";

    public static final String KEY_USER_CHANNEL = "user_channel";
    public static final String KEY_USER_LOCATION = "user_location";
    public static final String KEY_USERS = "users";
    public static final String KEY_MEMBER_UID = "member_uid";
    public static final String KEY_LAST_MESSAGE = "last_message";
    public static final String KEY_TIME_STAMP = "time_stamp";
    public static final String KEY_EVENTS = "events";
    public static final String KEY_TRIP_ID = "trip_id";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_GROUP_ID = "group_id";
    public static final String KEY_CHANNEL_ID = "channel_id";
    public static final String KEY_GROUP_MESSAGE = "group_message";
    public static final String KEY_USER_MESSAGE = "user_message";
    public static final String KEY_WAITING_USERS = "waiting_users";
    public static final String KEY_COMING_USERS = "coming_users";
    public static final String KEY_STATUS = "status";
    public static final String KEY_FRIENDS = "friends";
    public static final String KEY_INVITATIONS = "invitations";
    public static final String KEY_RECEIVER_ID = "receiver_id";

}
