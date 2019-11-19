package com.lamnn.wego.screen.map;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.ClusterMarker;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.data.remote.UserService;
import com.lamnn.wego.utils.APIUtils;
import com.lamnn.wego.utils.ClusterManagerRenderer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsPresenter implements MapsContract.Presenter {
    private Context mContext;

    private MapsContract.View mView;
    private GoogleMap mMap;
    private static final String TAG = "MAP_ACTIVITY_TAG";
    private User mUser = null;
    private TripService mTripService;
    private UserService mUserService;
    private List<ClusterMarker> mClusterMarkers;
    private ClusterManager<ClusterMarker> mClusterManager;
    private ClusterManagerRenderer mClusterManagerRenderer;

    public MapsPresenter(Context context, MapsContract.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void getUserData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(auth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    mView.showErrorMessage(mContext.getString(R.string.text_something_went_wrong));
                    Log.d(TAG, "onEvent: snapshot user data");
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
                        getTrips();
                        if (mUser.getActiveTrip() != null && !mUser.getActiveTrip().equals("")) {
                            getActiveTrip(mUser.getActiveTrip());
                        }
                    }
                } else {
                    mView.showErrorMessage(mContext.getString(R.string.text_something_went_wrong));
                    Log.d(TAG, "onEvent: get user data");
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
                List<Trip> trips;
                trips = response.body();
                mView.showTrips(trips);
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                Log.d(TAG, "onFailure: get trips");
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
                Trip trip = response.body();
                mView.showActiveTrip(trip);
                if (!trip.getCode().equals("")) {
                    getListMember(response.body().getCode(), true);
                }
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
        mTripService.getListMember(user).enqueue(new Callback<List<UserLocation>>() {
            @Override
            public void onResponse(Call<List<UserLocation>> call, Response<List<UserLocation>> response) {
                Log.d(TAG, "onResponse: ");
                initMarkers(response.body());
            }

            @Override
            public void onFailure(Call<List<UserLocation>> call, Throwable t) {

            }
        });
    }

    private void updateMarkers(List<UserLocation> userLocations) {
//        if()
        Log.d(TAG, "updateMarkers: " + mClusterMarkers.size());
        if (mClusterMarkers != null) {
            for (final ClusterMarker clusterMarker : mClusterMarkers) {
                try {
                    for (UserLocation userLocation : userLocations) {
                        if (clusterMarker.getUserLocation().getUser().getUid().equals(userLocation.getUid())) {
                            LatLng latLng = new LatLng(userLocation.getLocation().getLat(), userLocation.getLocation().getLng());
                            clusterMarker.setPosition(latLng);
                            clusterMarker.setUserLocation(userLocation);
                            mClusterManagerRenderer.setUpdateMarker(clusterMarker);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "_addMarkers: Exception" + e.getMessage());
                }
            }
        }
    }

    @Override
    public void updateStatus(String status) {
        UserLocation userLocation = new UserLocation();
        userLocation.setUid(FirebaseAuth.getInstance().getUid());
        userLocation.setStatus(status);
        mUserService = APIUtils.getUserService();
        mUserService.updateStatus(userLocation).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
//                mView.showErrorMessage(mContext.getString(R.string.text_something_went_wrong));
                Log.d(TAG, "onFailure: update status");
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
                //Do nothing!
                mView.hideLoading();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                mView.showErrorMessage(mContext.getString(R.string.text_something_went_wrong));
                Log.d(TAG, "onFailure: switch trip");
                mView.hideLoading();
            }
        });
    }


    public void initMarkers(List<UserLocation> userLocations) {
        if (mMap != null) {
            mMap.clear();
        }
        mClusterMarkers = new ArrayList<>();
        mClusterManager = new ClusterManager<>(mContext, mMap);
        mClusterManagerRenderer = new ClusterManagerRenderer(mContext, mMap, mClusterManager);
        mClusterManager.setRenderer(mClusterManagerRenderer);
        if (userLocations != null) {
            for (UserLocation userLocation : userLocations) {
                try {
                    ClusterMarker clusterMarker = new ClusterMarker(
                            new LatLng(userLocation.getLocation().getLat(), userLocation.getLocation().getLng()),
                            userLocation
                    );
                    clusterMarker.setUserLocation(userLocation);
                    mClusterManager.addItem(clusterMarker);
                    mClusterMarkers.add(clusterMarker);
                    mClusterManagerRenderer.setUpdateMarker(clusterMarker);
                } catch (Exception e) {
                    Log.d(TAG, "_addMarkers: Exception" + e.getMessage());
                }
            }
        }
        mClusterManager.cluster();
        mView.showListUserCircle(userLocations);
    }

    @Override
    public void initMarker(List<UserLocation> userLocations, GoogleMap map) {
        if (map != null) {
            mMap = map;
            mClusterMarkers = new ArrayList<>();
            mClusterManager = new ClusterManager<>(mContext, map);
            mClusterManagerRenderer = new ClusterManagerRenderer(mContext, map, mClusterManager);
            mClusterManager.setRenderer(mClusterManagerRenderer);
            if (userLocations != null) {
                for (UserLocation userLocation : userLocations) {
                    try {
                        ClusterMarker clusterMarker = new ClusterMarker(
                                new LatLng(userLocation.getLocation().getLat(), userLocation.getLocation().getLng()),
                                userLocation
                        );
                        clusterMarker.setUserLocation(userLocation);
                        mClusterManager.addItem(clusterMarker);
                        mClusterMarkers.add(clusterMarker);
                        mClusterManagerRenderer.setUpdateMarker(clusterMarker);
                    } catch (Exception e) {
                        Log.d(TAG, "_addMarkers: Exception" + e.getMessage());
                    }
                }
            }
            mClusterManager.cluster();
//            mView.showListUserCircle(userLocations);
        }
    }

    @Override
    public void initUserLocation(final UserLocation userLocation) {
        mUserService = APIUtils.getUserService();
        mUserService.initUserLocation(userLocation).enqueue(new Callback<UserLocation>() {
            @Override
            public void onResponse(Call<UserLocation> call, Response<UserLocation> response) {
                Log.d(TAG, "onResponse: init user location");
                for (ClusterMarker clusterMarker : mClusterMarkers) {
                    if (clusterMarker.getUserLocation().getUid().equals(FirebaseAuth.getInstance().getUid())) {
                        clusterMarker.setUserLocation(userLocation);
                        mClusterManagerRenderer.setUpdateMarker(clusterMarker);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserLocation> call, Throwable t) {
                Log.d(TAG, "onFailure: init user location" + t.getMessage());
            }
        });
    }

    @Override
    public void showUserItemCircle(UserLocation userLocation) {
        for (ClusterMarker clusterMarker : mClusterMarkers) {
            Marker marker = mClusterManagerRenderer.getMarker(clusterMarker);
            UserLocation tag = (UserLocation) marker.getTag();
            if (userLocation.getUid().equals(tag.getUid())) {
                marker.showInfoWindow();
            }
        }
    }

    @Override
    public void updateMarker(List<UserLocation> userLocations) {
        updateMarkers(userLocations);
    }

    @Override
    public void showAllMember() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (mClusterMarkers != null) {
            for (ClusterItem item : mClusterMarkers) {
                builder.include(item.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 300;
            // use animateCamera if animation is required
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        }
    }
}

