package com.lamnn.wego.screen.create_trip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Location;
import com.lamnn.wego.data.model.PlaceResponse;
import com.lamnn.wego.data.model.Point;
import com.lamnn.wego.data.model.Result;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.PlaceService;
import com.lamnn.wego.utils.APIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTripActivity extends AppCompatActivity implements View.OnClickListener, PlaceAdapter.OnPlaceClickListener {
    public static final String EXTRA_LAT = "EXTRA_LAT";
    public static final String EXTRA_LNG = "EXTRA_LNG";
    private Toolbar mToolbar;
    private Button mButtonNext;
    private EditText mTextNameTrip, mTextEndPlace;
    private RecyclerView mRecyclerPlaces;
    private PlaceService mPlaceService;
    private PlaceAdapter mPlaceAdapter;
    private Point mStartPoint;
    private Point mEndPoint;
    private Trip mTrip;
    private ProgressBar mProgressBar;

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
        mPlaceService = APIUtils.getPlaceService();
        mTrip = new Trip();
        initView();
        initToolbar();
//        receiveData();
        loadPlaces();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.place_api_key));
        }
    }

    private void receiveData() {
        mStartPoint = new Point();
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra(EXTRA_LAT, 0);
        double lng = intent.getDoubleExtra(EXTRA_LNG, 0);
        mStartPoint.setLocation(new Location(lat, lng));
        mStartPoint.setName("Start point");
    }


    private void loadPlaces() {
        mPlaceService.getPlaces(getString(R.string.place_api_key)).enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                if (response.isSuccessful()) {
                    showPlace(response.body().getResults());
                    Log.d("GET_OK", response.body() + "");
                } else {
                    int statusCode = response.code();
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                Log.d("MainActivity", "error loading from API" + t);
            }

        });
    }

    private void showPlace(List<Result> results) {
        mPlaceAdapter = new PlaceAdapter(this, results, this);
        mRecyclerPlaces.setAdapter(mPlaceAdapter);
        mPlaceAdapter.notifyDataSetChanged();
    }


    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create new trip");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonNext = mToolbar.findViewById(R.id.btn_create_next);
        mButtonNext.setOnClickListener(this);
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mTextNameTrip = findViewById(R.id.text_name_trip);
        mTextEndPlace = findViewById(R.id.text_search_end_place);
        mTextEndPlace.setOnClickListener(this);
        mRecyclerPlaces = findViewById(R.id.recycler_place_hint);
        mRecyclerPlaces.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerPlaces.setHasFixedSize(true);
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
        }
    }

    private void goToSeeDestination() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mTrip.setCreatorId(auth.getUid());
//        mTrip.setStartPoint(mStartPoint);
        Gson gson = new Gson();
        mTrip.setName(mTextNameTrip.getText().toString());
        mTrip.setCreationTime(new Date().toString());
        User user = new User();
        user.setUid(auth.getUid());
        List<String> users = new ArrayList<>();
        users.add(user.getUid());
        mTrip.setMembers(users);
        List<Point> points = new ArrayList<>();
        points.add(mEndPoint);
        mTrip.setSpecialPoints(points);
        startActivity(ShareCodeActivity.getIntent(this, mTrip));
    }

    private void startAutoCompleteActivity() {
        int AUTOCOMPLETE_REQUEST_CODE = 1;
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("vi")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (resultCode == AutocompleteActivity.RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(intent);
//                 place.getName();
//                 place.getAddress();
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(intent);
        } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
            // The user canceled the operation.
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

    @Override
    public void onPlaceClick(Result result) {
        mTextEndPlace.setText(result.getName());
        mEndPoint = new Point();
        Location location = new Location(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
        mEndPoint.setLocation(location);
        mEndPoint.setName(result.getName());
        mTrip.setEndPoint(mEndPoint);
        if (!mTextEndPlace.getText().toString().equals("")) {
            mButtonNext.setVisibility(View.VISIBLE);
        }
    }
}
