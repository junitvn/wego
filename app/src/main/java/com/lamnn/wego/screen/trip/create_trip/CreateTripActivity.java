package com.lamnn.wego.screen.trip.create_trip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Location;
import com.lamnn.wego.data.model.place.Point;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.TripSetting;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.screen.trip.create_trip.route.RouteActivity;
import com.lamnn.wego.screen.trip.create_trip.share_code.ShareCodeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.lamnn.wego.screen.trip.create_trip.route.RouteActivity.EXTRA_POINTS;
import static com.lamnn.wego.utils.AppUtils.TYPE_END;
import static com.lamnn.wego.utils.AppUtils.TYPE_START;

public class CreateTripActivity extends AppCompatActivity implements View.OnClickListener, CreateTripContract.View {
    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LNG = "EXTRA_LNG";
    public static final int REQUEST_WAYPOINTS = 101;

    private Toolbar mToolbar;
    private Button mButtonNext;
    private EditText mTextNameTrip;
    private TextView mTextEndPlace, mTextViewAddWaypoint;
    private Point mStartPoint;
    private Point mEndPoint;
    private Trip mTrip;
    private ArrayList<Point> mPoints;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerViewPoint;
    private SpecialPointAdapter mPointAdapter;
    private CreateTripPresenter mPresenter;

    public static Intent getIntent(Context context, double lat, double lng) {
        Intent intent = new Intent(context, CreateTripActivity.class);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        return intent;
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CreateTripActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        mTrip = new Trip();
        initView();
        initToolbar();
        receiveData();
        mPresenter = new CreateTripPresenter(this, this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAGVGhyzB1hQcXpFmg9QCP6JMI8Qp-768Y");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_next:
                goToSeeDestination();
                break;
            case R.id.text_search_end_place:
                if (!mTextEndPlace.getText().toString().equals("")) break;
                else startAutoCompleteActivity();
                break;
            case R.id.text_add_waypoint:
                if (mEndPoint != null) {
                    startActivityForResult(RouteActivity.getIntent(this, mTrip), REQUEST_WAYPOINTS);
                } else {
                    Toast.makeText(this, getString(R.string.choose_the_destination_first), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        switch (requestCode) {
            case REQUEST_WAYPOINTS:
                mPoints = intent.getParcelableArrayListExtra(EXTRA_POINTS);
                if (mPoints != null) {
                    mPointAdapter = new SpecialPointAdapter(this, mPoints);
                    mRecyclerViewPoint.setAdapter(mPointAdapter);
                    mPointAdapter.notifyDataSetChanged();
                    mTrip.setSpecialPoints(mPoints);
                }
                break;
            default:
                if (resultCode == AutocompleteActivity.RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(intent);
                    onPlaceClick(place);
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(intent);
                } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPlaceClick(Place place) {
        mTextEndPlace.setText(place.getName());
        mTextNameTrip.setText(place.getName());
        mEndPoint = new Point();
        mEndPoint.setType(TYPE_END);
        mEndPoint.setLocation(new Location(place.getLatLng().latitude, place.getLatLng().longitude));
        mEndPoint.setName(place.getName());
        mTrip.setEndPoint(mEndPoint);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.text_create_new_trip));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonNext = mToolbar.findViewById(R.id.btn_create_next);
        mButtonNext.setOnClickListener(this);
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mTextNameTrip = findViewById(R.id.text_name_user);
        mTextEndPlace = findViewById(R.id.text_search_end_place);
        mTextEndPlace.setOnClickListener(this);
        mTextViewAddWaypoint = findViewById(R.id.text_add_waypoint);
        mTextViewAddWaypoint.setOnClickListener(this);
        mRecyclerViewPoint = findViewById(R.id.recycler_point);
        mRecyclerViewPoint.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewPoint.setHasFixedSize(false);
    }

    private void receiveData() {
        double lat = getIntent().getExtras().getDouble(EXTRA_LAT);
        double lng = getIntent().getExtras().getDouble(EXTRA_LNG);
        initStartPoint(lat, lng);
    }

    private void initStartPoint(double lat, double lng) {
        mStartPoint = new Point();
        mStartPoint.setLocation(new Location(lat, lng));
        mStartPoint.setType(TYPE_START);
        mTrip.setStartPoint(mStartPoint);
    }

    private void goToSeeDestination() {
        if (mEndPoint == null) {
            Toast.makeText(this, getString(R.string.have_not_choose_destination), Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mTrip.setCode(generateRandomIntIntRange());
        mTrip.setCreatorId(auth.getUid());
        mTrip.setStartPoint(mStartPoint);
        mTrip.setName(mTextNameTrip.getText().toString());
        mTrip.setCreationTime(new Date().toString());
        User user = new User();
        user.setUid(auth.getUid());
        List<String> users = new ArrayList<>();
        users.add(user.getUid());
        mTrip.setMembers(users);
        TripSetting tripSetting = new TripSetting();
        tripSetting.setDefaultValue();
        mTrip.setTripSetting(tripSetting);
        mPresenter.createTrip(mTrip);
    }

    private void startAutoCompleteActivity() {
        int AUTOCOMPLETE_REQUEST_CODE = 1;
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public static String generateRandomIntIntRange() {
        Random r = new Random();
        int min = 100000;
        int max = 999999;
        int res = r.nextInt((max - min) + 1) + min;
        return res + "";
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
