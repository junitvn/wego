package com.lamnn.wego.screen.info.info_member;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.EventStatus;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.data.remote.UserService;
import com.lamnn.wego.utils.APIUtils;
import com.lamnn.wego.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.utils.AppUtils.KEY_COMING_USERS;
import static com.lamnn.wego.utils.AppUtils.KEY_EVENTS;
import static com.lamnn.wego.utils.AppUtils.KEY_TIME_STAMP;
import static com.lamnn.wego.utils.AppUtils.KEY_TRIP_ID;
import static com.lamnn.wego.utils.AppUtils.KEY_USER_ID;
import static com.lamnn.wego.utils.AppUtils.KEY_USER_LOCATION;
import static com.lamnn.wego.utils.AppUtils.KEY_WAITING_USERS;
import static com.lamnn.wego.utils.AppUtils.STATUS_DELETED;
import static com.lamnn.wego.utils.AppUtils.STATUS_GOING;
import static com.lamnn.wego.utils.AppUtils.TYPE_COMING;
import static com.lamnn.wego.utils.AppUtils.TYPE_WAITING;

public class InfoMemberPresenter implements InfoMemberContract.Presenter {
    private Context mContext;
    private UserLocation mMemberUserLocation;
    private UserLocation mLoggedUserLocation;
    private InfoMemberContract.View mView;
    private FirebaseFirestore mFirestore;

    public InfoMemberPresenter(Context context, InfoMemberContract.View view) {
        mContext = context;
        mView = view;
        mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void getUserLocationData(String userId) {
        mView.showLoading();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(KEY_USER_LOCATION)
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException e) {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(doc.getData());
                        UserLocation userLocation = gson.fromJson(jsonElement, UserLocation.class);
                        Timestamp timestamp = (Timestamp) doc.getData().get(KEY_TIME_STAMP);
                        userLocation.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                        String status = "";
                        String type = "";
                        Event event = new Event();
                        if (userLocation.getEventStatuses() != null) {
                            for (EventStatus eventStatus : userLocation.getEventStatuses()) {
                                if (eventStatus.getTripId().equals(userLocation.getUser().getActiveTrip())) {
                                    if (!eventStatus.getStatus().equals(STATUS_GOING)) {
                                        type = eventStatus.getStatus();
                                        event = eventStatus.getEvent();
                                        if (eventStatus.getStatus().equals(TYPE_COMING)) {
                                            status = mContext.getString(R.string.going_to) + eventStatus.getEvent().getUser().getName()
                                                    + mContext.getString(R.string.s_place);
                                        } else {
                                            status = mContext.getString(R.string.waiting_for) + eventStatus.getEvent().getUser().getName();
                                        }
                                    }
                                }
                            }
                        }
                        mView.showUserStatus(userLocation, status, type, event);
                        mView.updateUserLocation(userLocation);
                        mMemberUserLocation = userLocation;
                        getEventData();
                    }
                });
        db.collection(KEY_USER_LOCATION)
                .document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException e) {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(doc.getData());
                        UserLocation userLocation = gson.fromJson(jsonElement, UserLocation.class);
                        Timestamp timestamp = (Timestamp) doc.getData().get(KEY_TIME_STAMP);
                        userLocation.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                        mLoggedUserLocation = userLocation;
                        getEventData();
                    }
                });
    }

    private void addMember(String myUserId, Event event, String type, DocumentReference eventRef) {
        if (type.equals(TYPE_COMING)) {
            eventRef.update(KEY_COMING_USERS, FieldValue.arrayUnion(myUserId));
            eventRef.update(KEY_WAITING_USERS, FieldValue.arrayRemove(myUserId));
        } else {
            eventRef.update(KEY_COMING_USERS, FieldValue.arrayRemove(myUserId));
            eventRef.update(KEY_WAITING_USERS, FieldValue.arrayUnion(myUserId));
        }
        addEventToUser(myUserId, event, type);
    }

    private void removeFromOldEventEvent(DocumentReference myEventRef, String myUserId) {
        myEventRef.update(KEY_COMING_USERS, FieldValue.arrayRemove(myUserId));
        myEventRef.update(KEY_WAITING_USERS, FieldValue.arrayRemove(myUserId));
    }

    @Override
    public void addComingMember(final String myUserId, final Event event) {
        final DocumentReference eventRef = mFirestore.collection(KEY_EVENTS).document(event.getEventId());
        if (mLoggedUserLocation.getEventStatuses() != null) {
            for (EventStatus eventStatus : mLoggedUserLocation.getEventStatuses()) {
                if (event.getTripId().equals(eventStatus.getTripId())) {
                    if (eventStatus.getStatus().equals(STATUS_GOING)) {
                        addMember(myUserId, event, TYPE_COMING, eventRef);
                    } else if (event.getEventId().equals(eventStatus.getEvent().getEventId())) {
                        //add coming member
                        if (event.getComingUsers() != null && Utils.checkExistUid(myUserId, event.getComingUsers())) {
                            eventRef.update(KEY_COMING_USERS, FieldValue.arrayRemove(myUserId));
                            addEventToUser(myUserId, event, STATUS_GOING);
                        } else {
                            addMember(myUserId, event, TYPE_COMING, eventRef);
                        }

                    } else {
                        //dialog confirm to join other event
                        // - Yes : add coming
                        // - No: cancel
                        final DocumentReference myEventRef = mFirestore
                                .collection(KEY_EVENTS)
                                .document(eventStatus.getEvent().getEventId());
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(true);
                        builder.setTitle(mContext.getString(R.string.title_alert));
                        builder.setMessage(mContext.getString(R.string.message_alert));
                        builder.setPositiveButton(mContext.getString(R.string.text_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeFromOldEventEvent(myEventRef, myUserId);
                                        addMember(myUserId, event, TYPE_COMING, eventRef);
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                } else {
                    addMember(myUserId, event, TYPE_COMING, eventRef);
                }
            }
        } else { //chưa tham gia event nào
            addMember(myUserId, event, TYPE_COMING, eventRef);
        }
    }

    @Override
    public void addWaitingMember(final String myUserId, final Event event) {
        final DocumentReference eventRef = mFirestore.collection(KEY_EVENTS).document(event.getEventId());
        if (mLoggedUserLocation.getEventStatuses() != null) {
            for (EventStatus eventStatus : mLoggedUserLocation.getEventStatuses()) {
                if (event.getTripId().equals(eventStatus.getTripId())) {
                    if (eventStatus.getStatus().equals(STATUS_GOING)) {
                        addMember(myUserId, event, TYPE_WAITING, eventRef);
                    } else if (event.getEventId().equals(eventStatus.getEvent().getEventId())) {
                        //add waiting member
                        if (event.getWaitingUsers() != null && Utils.checkExistUid(myUserId, event.getWaitingUsers())) {
                            eventRef.update(KEY_WAITING_USERS, FieldValue.arrayRemove(myUserId));
                            addEventToUser(myUserId, event, STATUS_GOING);
                        } else {
                            addMember(myUserId, event, TYPE_WAITING, eventRef);
                        }

                    } else {
                        //dialog confirm to join other event
                        // - Yes : add coming
                        // - No: cancel
                        final DocumentReference myEventRef = mFirestore
                                .collection(KEY_EVENTS)
                                .document(eventStatus.getEvent().getEventId());
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(true);
                        builder.setTitle(mContext.getString(R.string.title_alert));
                        builder.setMessage(mContext.getString(R.string.message_alert));
                        builder.setPositiveButton(mContext.getString(R.string.text_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeFromOldEventEvent(myEventRef, myUserId);
                                        addMember(myUserId, event, TYPE_WAITING, eventRef);
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                } else {
                    addMember(myUserId, event, TYPE_WAITING, eventRef);
                }
            }
        } else { //chưa tham gia event nào
            addMember(myUserId, event, TYPE_WAITING, eventRef);
        }

    }

    @Override
    public void getListMember(final List<String> users, final String type) {
        final List<UserLocation> userLocations = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            mFirestore.collection(KEY_USER_LOCATION)
                    .document(users.get(i))
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException e) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(doc.getData());
                            UserLocation userLocation = gson.fromJson(jsonElement, UserLocation.class);
                            Timestamp timestamp = (Timestamp) doc.getData().get(KEY_TIME_STAMP);
                            userLocation.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                            userLocations.add(userLocation);
                            if(userLocations.size() == users.size()){
                                mView.showMemberPopup(userLocations, type);
                            }
                        }
                    });
        }
    }

    private void addEventToUser(String myUserId, Event event, String eventStatusValue) {
        UserLocation userLocation = new UserLocation();
        userLocation.setUid(myUserId);
        EventStatus eventStatus = new EventStatus(event.getTripId(), eventStatusValue, event);
        List<EventStatus> eventStatuses = new ArrayList<>();
        eventStatuses.add(eventStatus);
        userLocation.setEventStatuses(eventStatuses);
        UserService userService = APIUtils.getUserService();
        userService.addEventToUserLocation(userLocation).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    private void getEventData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(KEY_EVENTS)
                .whereEqualTo(KEY_TRIP_ID, mMemberUserLocation.getUser().getActiveTrip())
                .whereEqualTo(KEY_USER_ID, mMemberUserLocation.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        List<Event> events = new ArrayList<>();
                        Event event;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(doc.getData());
                            event = gson.fromJson(jsonElement, Event.class);
                            Timestamp timestamp = (Timestamp) doc.getData().get(KEY_TIME_STAMP);
                            event.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                            event.setEventId(doc.getId());
                            if (!event.getStatus().equals(STATUS_DELETED)) {
                                events.add(event);
                            }
                        }
                        mView.showEvents(events);
                        mView.hideLoading();
                    }
                });
    }
}
