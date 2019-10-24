package com.lamnn.wego.screen.map;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.data.remote.UserService;
import com.lamnn.wego.utils.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsPresenter implements MapsContract.Presenter {
    private MapsContract.View mView;
    private static final String TAG = "MAP_ACTIVITY_TAG";
    private User mUser = null;
    private TripService mTripService;
    private UserService mUserService;

    public MapsPresenter(MapsContract.View view) {
        mView = view;
    }

    @Override
    public void getUserData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(auth.getUid());
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    try {
//
//                        Gson gson = new Gson();
//                        JsonElement jsonElement = gson.toJsonTree(snapshot.getData());
//                        mUser = gson.fromJson(jsonElement, User.class);
//                        Timestamp timestamp = (Timestamp) snapshot.getData().get("time_stamp");
//                        mUser.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
//                        mView.showUserData(mUser);
//                        if (mUser.getActiveTrip() != null) {
//                            getActiveTrip(mUser.getActiveTrip());
//                            getListMember(mUser.getActiveTrip(), false);
//                        }
//                    } catch (Exception e) {
//                        Log.d(TAG, "LOI TO VL" + e.toString());
//                    }
//                    Log.d(TAG, "Current data: " + snapshot.getData());
//                } else {
//                    Log.d(TAG, "Current data: null");
//                }
//            }
//
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: " + e);
//            }
//        });
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "onEvent: " + e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Gson gson = new Gson();
                    JsonElement jsonElement = gson.toJsonTree(snapshot.getData());
                    mUser = gson.fromJson(jsonElement, User.class);
                    if (mUser.getFirstTime()) {
                        mView.navigateToUpdateProfile(mUser);
                    } else {
                        mView.showUserData(mUser);
                    }
                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    @Override
    public void getTrips() {
        String uid = FirebaseAuth.getInstance().getUid();
        User user = new User(uid);
        mTripService = APIUtils.getTripService();
        mTripService.getMyTrips(user).enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                Log.d(TAG, "onResponse: ");
                List<Trip> trips;
                trips = response.body();
                mView.showTrips(trips);
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    @Override
    public void getActiveTrip(String code) {
        User user = new User();
        user.setActiveTrip(code);
        mTripService = APIUtils.getTripService();
        mTripService.getTrip(user).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                mView.showActiveTrip(response.body());
                mView.hideLoading();
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {

            }
        });
    }

    @Override
    public void getListMember(String code, final Boolean isExistMarker) {
        User user = new User();
        user.setActiveTrip(code);
        mTripService = APIUtils.getTripService();
        mTripService.getListMember(user).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (isExistMarker) {
                    mView.updateMarkers(response.body());
                    mView.hideLoading();
                } else {
                    mView.initMarkers(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }

    @Override
    public void updateStatus(String status) {
        User user = new User();
        user.setUid(FirebaseAuth.getInstance().getUid());
//        user.setStatus(status);
        mUserService = APIUtils.getUserService();
        mUserService.updateStatus(user).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }

    @Override
    public void switchTrip(final String activeTrip) {
        mView.showLoading();
        User user = new User();
        user.setUid(FirebaseAuth.getInstance().getUid());
        user.setActiveTrip(activeTrip);
        mTripService = APIUtils.getTripService();
        mTripService.switchTrip(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                getUserData();
                getTrips();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "onFailure: swictrips" + t);
            }
        });
    }


}

