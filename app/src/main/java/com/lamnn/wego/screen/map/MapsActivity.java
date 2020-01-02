package com.lamnn.wego.screen.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.maps.android.PolyUtil;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.RouteResponse;
import com.lamnn.wego.data.model.route.Step;
import com.lamnn.wego.data.remote.UpdateLocationService;
import com.lamnn.wego.screen.chat.ChatActivity;
import com.lamnn.wego.screen.trip.create_trip.CreateTripActivity;
import com.lamnn.wego.screen.info.info_member.InfoMemberActivity;
import com.lamnn.wego.screen.info.info_user.InfoUserActivity;
import com.lamnn.wego.screen.trip.create_trip.share_code.ShareCodeActivity;
import com.lamnn.wego.screen.trip.join_trip.JoinTripActivity;
import com.lamnn.wego.screen.login.LoginActivity;
import com.lamnn.wego.screen.profile.update.ProfileUpdateActivity;
import com.lamnn.wego.screen.trip.setting_trip.SettingTripActivity;
import com.lamnn.wego.service.LocationService;
import com.lamnn.wego.utils.APIUtils;
import com.lamnn.wego.utils.GlideApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.screen.info.info_user.InfoUserActivity.CHANNEL_ID;
import static com.lamnn.wego.screen.info.info_user.InfoUserActivity.EXTRA_USER_LOCATION;
import static com.lamnn.wego.utils.AppUtils.STATUS_ONLINE;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, MapsContract.View, OnMapReadyCallback,
        TripAdapter.OnTripItemClickListener, GoogleMap.OnInfoWindowClickListener, MemberCircleAdapter.OnUserItemClickListener {
    public static final String EXTRA_LOCATION = "EXTRA_LOCATION";
    public static final String DISTANCE_CHANNEL_ID = "DISTANCE_CHANNEL_ID";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MAP_ACTIVITY_TAG";

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ImageView mImageAvatar;
    private ProgressBar mProgressBar;
    private TextView mTextWelcome;
    private GoogleMap mMap;
    private MapsContract.Presenter mPresenter;
    private ImageView mImageViewToggleDropdown, mImageViewRefresh, mImageViewAllMember, mImageViewAddMember;
    private LinearLayout mLinearDropdown;
    private Button mButtonJoin, mButtonCreate;
    private TextView mTextTripName;
    private RecyclerView mRecyclerListTrip, mRecyclerListMember;
    private TripAdapter mTripAdapter;
    private MemberCircleAdapter mMemberCircleAdapter;
    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mCurrentLocation;
    private SupportMapFragment mMapFragment;
    private LocationService mLocationService;
    private User mUser;
    private UserLocation mUserLocation;
    private UserLocation mUserLocationParam;
    private List<UserLocation> mUserLocations;
    private UpdateLocationService mUpdateLocationService;
    boolean doubleBackToExitPressedOnce = false;
    private BroadcastReceiver locationUpdateReceiver;
    private Trip mTrip;
    private ConstraintLayout mLayoutAllMember;
    private ConstraintLayout mLayoutAddMember;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }

    public static Intent getIntent(Context context, UserLocation userLocation) {
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(EXTRA_USER_LOCATION, userLocation);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initView();
        initToolbar();
        getData();
        initMaps();
        getLocationPermission();
        subscribeInvitationChannel();
        createNotificationChannel();
        createDistanceNotificationChannel();
        final Intent locationService = new Intent(this.getApplication(), LocationService.class);
        this.getApplication().startService(locationService);
        initBroadcastReceiver();
        if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }
        receiveData();
    }

    private void subscribeInvitationChannel() {
        String topicInvitation = "IN" + FirebaseAuth.getInstance().getUid();
        FirebaseMessaging.getInstance().subscribeToTopic(topicInvitation);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.updateStatus("online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.mMapFragment != null) {
            this.mMapFragment.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.mMapFragment != null) {
            this.mMapFragment.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showUserData(User user) {
        mUser = new User();
        mUser = user;
        mTextWelcome.setText(user.getName());
        if (user.getPhotoUri() != null) {
            GlideApp.with(getApplicationContext())
                    .load(user.getPhotoUri())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageAvatar);
        }
    }

    @Override
    public void showTrips(List<Trip> trips) {
        if (trips.size() > 0) {
            mLayoutAllMember.setVisibility(View.VISIBLE);
            mLayoutAddMember.setVisibility(View.VISIBLE);
            mImageViewAddMember.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        mTripAdapter = new TripAdapter(this, formattedTrips(trips), this);
        mRecyclerListTrip.setAdapter(mTripAdapter);
        mTripAdapter.notifyDataSetChanged();
    }

    private List<Trip> formattedTrips(List<Trip> trips) {
        if (mUser != null && trips != null) {
            for (Trip trip : trips) {
                if (trip.getCode().equals(mUser.getActiveTrip())) {
                    trip.setActive(true);
                } else trip.setActive(false);
            }
        }
        return trips;
    }

    @Override
    public void showActiveTrip(Trip trip) {
        mTrip = trip;
        if (trip != null) {
            mTextTripName.setText(trip.getName());
        } else {
            mTextTripName.setText(getString(R.string.app_name));
        }
        mPresenter.getDirection(trip);
        mPresenter.initSpecialMarker(trip);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void navigateToUpdateProfile(User user) {
        startActivity(ProfileUpdateActivity.getIntent(this, user));
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showListUserCircle(List<UserLocation> userLocations) {
        mMemberCircleAdapter = new MemberCircleAdapter(this, formatListUser(userLocations), this);
        mUserLocations = userLocations;
        mRecyclerListMember.setAdapter(mMemberCircleAdapter);
        mMemberCircleAdapter.notifyDataSetChanged();
    }

    @Override
    public void drawPoly(RouteResponse routeResponse) {
        PolylineOptions polygonOptions = new PolylineOptions();
        polygonOptions.width(15).color(getResources().getColor(R.color.colorPrimary));
        for (Step step : routeResponse.getRoutes().get(0).getLegs().get(0).getSteps()) {
            polygonOptions.addAll(PolyUtil.decode(step.getPolyline().getPoints()));
        }
        mMap.addPolyline(polygonOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setInfoWindowAdapter(new InfoWindowAdapter(this));
            mMap.setOnInfoWindowClickListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.text_click_back_again), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;

                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_avatar:
            case R.id.text_nav_welcome:
                startActivity(ProfileUpdateActivity.getIntent(this, mUser));
                break;
            case R.id.btn_join:
                startActivity(JoinTripActivity.getIntent(this, mUser));
                toggleDropdown();
                break;
            case R.id.btn_create:
                startActivity(CreateTripActivity
                        .getIntent(this, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
                toggleDropdown();
                break;
            case R.id.image_refresh:
                mPresenter.getUserData();
                mPresenter.getTrips();
                break;
            case R.id.image_circle_all:
                mPresenter.showAllMember();
                break;
            case R.id.image_circle_add:
                startActivity(ShareCodeActivity.getIntent(this, mTrip));
                break;
            case R.id.image_toggle_dropdown:
            case R.id.text_trip_name:
                toggleDropdown();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_map:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mNavigationView.setCheckedItem(R.id.nav_map);
                break;
            case R.id.nav_people:
                startActivity(ChatActivity.getIntent(this, mUser));
                break;
            case R.id.nav_sign_out:
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.getIntent(this));
                break;
        }
        return false;
    }

    private void receiveData() {
        UserLocation userLocation;
        if (getIntent().getExtras() != null) {
            userLocation = getIntent().getExtras().getParcelable(EXTRA_USER_LOCATION);
            if (userLocation != null) {
                mUserLocationParam = userLocation;
                mPresenter.showUserLocation(userLocation);
            }
        }
    }

    private void initBroadcastReceiver() {
        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location newLocation = intent.getParcelableExtra(EXTRA_LOCATION);
                com.lamnn.wego.data.model.Location location
                        = new com.lamnn.wego.data.model.Location(newLocation.getLatitude(), newLocation.getLongitude());
                if (mUserLocation != null) {
                    mUserLocation.setLocation(location);
                }
                if (mUserLocation != null && mUserLocation.getUser() != null) {
                    mUpdateLocationService = APIUtils.getUpdateLocationService();
                    mUpdateLocationService.updateLocation(mUserLocation).enqueue(new Callback<List<UserLocation>>() {
                        @Override
                        public void onResponse(Call<List<UserLocation>> call, Response<List<UserLocation>> response) {
                            mUserLocations = response.body();
                            mPresenter.updateMarker(mUserLocations);
                        }

                        @Override
                        public void onFailure(Call<List<UserLocation>> call, Throwable t) {
                        }
                    });

                }
            }
        };


        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationUpdateReceiver,
                new IntentFilter("LocationUpdated"));
    }

    private void initMaps() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    private void getData() {
        mPresenter.getUserData();
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMaps();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mUserLocation = new UserLocation();
        mUserLocations = new ArrayList<>();
        mUserLocation.setStatus(STATUS_ONLINE);
        mUserLocation.setUid(FirebaseAuth.getInstance().getUid());
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            if (mUser != null) {
                                mUserLocation.setUser(mUser);
                                mUserLocation.setUid(mUser.getUid());
                            }
                            mCurrentLocation = (Location) task.getResult();
                            if (mUserLocation != null && mCurrentLocation != null) {
                                mUserLocation.setLocation(
                                        new com.lamnn.wego.data.model.Location(mCurrentLocation.getLatitude(),
                                                mCurrentLocation.getLongitude()));
                            }
                            if (mCurrentLocation != null) {
                                moveCamera(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            }
                            mUserLocations.add(mUserLocation);
                            mPresenter.initUserLocation(mUserLocation);
                            mPresenter.initMarker(mUserLocations, mMap);
                        } else {
                        }
                    }
                });
            }
        } catch (SecurityException e) {
        }
    }

    private void initView() {
        mPresenter = new MapsPresenter(this, this);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        mImageAvatar = headerView.findViewById(R.id.image_avatar);
        mImageAvatar.setOnClickListener(this);
        mImageViewAllMember = findViewById(R.id.image_circle_all);
        mImageViewAllMember.setOnClickListener(this);
        mTextWelcome = headerView.findViewById(R.id.text_nav_welcome);
        mTextWelcome.setOnClickListener(this);
        mLinearDropdown = findViewById(R.id.linear_dropdown);
        mRecyclerListTrip = findViewById(R.id.recycler_list_trip);
        mRecyclerListTrip.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerListTrip.setHasFixedSize(true);
        mRecyclerListMember = findViewById(R.id.recycler_list_member);
        mRecyclerListMember.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerListMember.setHasFixedSize(true);
        mButtonJoin = findViewById(R.id.btn_join);
        mButtonJoin.setOnClickListener(this);
        mButtonCreate = findViewById(R.id.btn_create);
        mButtonCreate.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mLayoutAllMember = findViewById(R.id.layout_all_member);
        mLayoutAddMember = findViewById(R.id.layout_add_member);
        mImageViewAddMember = findViewById(R.id.image_circle_add);
        mImageViewAddMember.setOnClickListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        mImageViewToggleDropdown = toolbar.findViewById(R.id.image_toggle_dropdown);
        mImageViewToggleDropdown.setOnClickListener(this);
        mTextTripName = toolbar.findViewById(R.id.text_trip_name);
        mTextTripName.setOnClickListener(this);
        mTextTripName.setText(getString(R.string.app_name));
        mImageViewRefresh = toolbar.findViewById(R.id.image_refresh);
        mImageViewRefresh.setOnClickListener(this);
    }

    private void moveCamera(LatLng latLng, float defaultZoom) {
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, defaultZoom));
        }
    }

    private void toggleDropdown() {
        if (mLinearDropdown.getVisibility() == View.GONE) {
            mLinearDropdown.setVisibility(View.VISIBLE);
            mImageViewToggleDropdown.setImageResource(R.drawable.ic_sort_up);
        } else {
            mLinearDropdown.setVisibility(View.GONE);
            mImageViewToggleDropdown.setImageResource(R.drawable.ic_sort_down);
        }
    }


    private List<UserLocation> formatListUser(List<UserLocation> userLocations) {
        if (userLocations != null) {
            for (int i = 0; i < userLocations.size(); i++) {
                if (userLocations.get(i).getUid().equals(FirebaseAuth.getInstance().getUid())) {
                    if (userLocations.get(i).getLocation() != null) {
                        Collections.swap(userLocations, i, 0);
                    }
                }
            }
        }
        return userLocations;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Object markerTag = marker.getTag();
        if (markerTag.getClass() == UserLocation.class) {
            UserLocation userLocation = (UserLocation) markerTag;
            String currentUid = FirebaseAuth.getInstance().getUid();
            if (userLocation != null && userLocation.getUid().equals(currentUid)) {
                startActivity(InfoUserActivity.getIntent(this, userLocation));
            } else {
                startActivity(InfoMemberActivity.getIntent(this, userLocation));
            }
        }
    }

    @Override
    public void onTripItemClick(Trip trip) {
        mPresenter.switchTrip(trip.getCode());
    }

    @Override
    public void onTripSettingClick(Trip trip) {
        startActivity(SettingTripActivity.getIntent(this, trip, mUserLocation));
    }

    @Override
    public void onUserItemClick(UserLocation userLocation) {
        if (userLocation != null && userLocation.getLocation() != null) {
            LatLng latLng = new LatLng(userLocation.getLocation().getLat(), userLocation.getLocation().getLng());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            mPresenter.showUserItemCircle(userLocation);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "lamnn";
            String description = "description chanel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createDistanceNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "lamnn";
            String description = "description chanel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(DISTANCE_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
