package com.lamnn.wego.screen.details.info_user;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
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
import com.lamnn.wego.data.remote.EventService;
import com.lamnn.wego.utils.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.utils.AppUtils.CAR_ICON_URI;
import static com.lamnn.wego.utils.AppUtils.GAS_ICON_URI;

public class InfoUserPresenter implements InfoUserContract.Presenter {
    private Context mContext;
    private InfoUserContract.View mView;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public InfoUserPresenter(Context context, InfoUserContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void getEventData(UserLocation userLocation) {
        mView.showLoading();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("trip_id", userLocation.getUser().getActiveTrip())
                .whereEqualTo("user_id", userLocation.getUser().getUid())
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
                            Timestamp timestamp = (Timestamp) doc.getData().get("time_stamp");
                            event.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                            event.setEventId(doc.getId());
                            if (!event.getStatus().equals("deleted")) {
                                events.add(event);
                            }
                        }
                        mView.showEvents(events);
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void getListMember(final List<String> users, final String type) {
        final List<UserLocation> userLocations = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            mFirestore.collection("user_location")
                    .document(users.get(i))
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException e) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(doc.getData());
                            UserLocation userLocation = gson.fromJson(jsonElement, UserLocation.class);
                            Timestamp timestamp = (Timestamp) doc.getData().get("time_stamp");
                            userLocation.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                            userLocations.add(userLocation);
                            if (userLocations.size() == users.size()) {
                                mView.showMemberPopup(userLocations, type);
                            }
                        }
                    });
        }
    }

    @Override
    public void updateStatus(Event event, String status) {
        mView.showLoading();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(event.getEventId())
                .update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mView.hideLoading();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void createQuickEvent(UserLocation userLocation, String type) {
        mView.showLoading();
        Event event = new Event();
        event.setUser(userLocation.getUser());
        event.setUserId(userLocation.getUid());
        event.setTripId(userLocation.getUser().getActiveTrip());
        event.setLocation(userLocation.getLocation());
        event.setTitle(type.equals("car")
                ? mContext.getString(R.string.text_car_broken)
                : mContext.getString(R.string.text_out_of_gas));
        event.setStatus("waiting");
        List<String> photos = new ArrayList<>();
        if (type.equals("car")) {
            photos.add(CAR_ICON_URI);
        } else {
            photos.add(GAS_ICON_URI);
        }
        event.setPhotos(photos);
        EventService eventService = APIUtils.getEventService();
        eventService.createEvent(event).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                mView.hideLoading();
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                mView.hideLoading();
            }
        });
    }

    public void getUserLocationData(UserLocation userLocation) {
        mFirestore.collection("user_location").document(userLocation.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Gson gson = new Gson();
                        JsonElement jsonElement = gson.toJsonTree(doc.getData());
                        UserLocation userLocation = gson.fromJson(jsonElement, UserLocation.class);
                        Timestamp timestamp = (Timestamp) doc.getData().get("time_stamp");
                        userLocation.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                        String status = "";
                        String type = "";
                        Event event = new Event();
                        if (userLocation.getEventStatuses() != null) {
                            for (EventStatus eventStatus : userLocation.getEventStatuses()) {
                                if (eventStatus.getTripId().equals(userLocation.getUser().getActiveTrip())) {
                                    if (!eventStatus.getStatus().equals("going")) {
                                        type = eventStatus.getStatus();
                                        event = eventStatus.getEvent();
                                        if (eventStatus.getStatus().equals("coming")) {
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
                        getEventData(userLocation);
                    } else {
                    }
                } else {
                }
            }
        });
    }
}
