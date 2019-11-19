package com.lamnn.wego.screen.trip.setting_trip;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.MyTimeStamp;

public class SettingTripPresenter implements SettingTripContract.Presenter {
    private Context mContext;
    private SettingTripContract.View mView;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    public SettingTripPresenter(Context context, SettingTripContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void outTrip() {

    }

    @Override
    public void updateTrip() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void updateUserLocation(UserLocation userLocation) {
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
                        mView.updateUserLocation(userLocation);
                    } else {
                    }
                } else {
                }
            }
        });
    }
}
