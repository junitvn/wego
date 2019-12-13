package com.lamnn.wego.screen.trip.setting_trip;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingTripPresenter implements SettingTripContract.Presenter {
    private Context mContext;
    private SettingTripContract.View mView;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private TripService mTripService;

    public SettingTripPresenter(Context context, SettingTripContract.View view) {
        mContext = context;
        mView = view;
        mTripService = APIUtils.getTripService();
    }

    @Override
    public void outTrip(UserLocation userLocation) {
        mTripService.outTrip(userLocation).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                hideLoading();
                mContext.startActivity(MapsActivity.getIntent(mContext.getApplicationContext()));
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    @Override
    public void updateTrip(Trip trip) {
        mTripService.updateTrip(trip).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                hideLoading();
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {

            }
        });
    }

    @Override
    public void showLoading() {
        mView.showLoading();
    }

    @Override
    public void hideLoading() {
        mView.hideLoading();
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

    @Override
    public void copyIdTripToClipboard(String code) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("TRIP_CODE", code);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(mContext, "Copied " + code + " to clipboard", Toast.LENGTH_SHORT).show();
    }
}
