package com.lamnn.wego.screen.trip.create_trip.route;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.PolyUtil;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.place.Geometry;
import com.lamnn.wego.data.model.Location;
import com.lamnn.wego.data.model.place.PlaceResponse;
import com.lamnn.wego.data.model.place.Point;
import com.lamnn.wego.data.model.place.Result;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.geocode.GeocodeResponse;
import com.lamnn.wego.data.model.route.RouteResponse;
import com.lamnn.wego.data.model.route.Step;
import com.lamnn.wego.data.remote.DirectionService;
import com.lamnn.wego.data.remote.GeocodeService;
import com.lamnn.wego.data.remote.PlaceService;
import com.lamnn.wego.utils.APIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.screen.trip.create_trip.share_code.ShareCodeActivity.EXTRA_TRIP;
import static com.lamnn.wego.utils.AppUtils.TYPE_CHECK_IN;
import static com.lamnn.wego.utils.AppUtils.TYPE_WAYPOINT;
import static com.lamnn.wego.utils.Utils.getMarkerIconFromDrawable;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback,
        View.OnClickListener, GoogleMap.OnMapClickListener, TextWatcher,
        SearchPlaceAdapter.OnItemSearchPlaceClickListener, AdapterView.OnItemSelectedListener, GoogleMap.OnInfoWindowClickListener {
    public static final String EXTRA_POINTS = "EXTRA_POINTS";

    private GoogleMap mMap;
    private DirectionService mDirectionService;
    private EditText mEditTextSearch;
    private ImageView mImageViewClearText;
    private ConstraintLayout mConstraintLayoutPlaceInfo;
    private Button mButtonCancel, mButtonAdd;
    private TextView mTextViewName, mTextViewAddress;
    private RecyclerView mRecyclerViewSearchResult;
    private RatingBar mRatingBar;
    private Trip mTrip;
    private Button mButtonDone;
    private ArrayList<Point> mPoints;
    private ArrayList<Marker> mMarkers;
    private Marker mStartMarker, mEndMarker;
    private SearchPlaceAdapter mSearchPlaceAdapter;
    private List<Result> mResults;
    private Toolbar mToolbar;
    private long mLastTextEdit = 0;
    private final long delay = 1000;
    private Handler mHandler;
    private ProgressBar mProgressBar;
    private Spinner mSpinner;
    private Marker mSelectedPlace;
    private Result mResult;
    private Point mSeletedPoint;
    private RouteResponse mRouteResponse;
    private String mType;

    public static Intent getIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, RouteActivity.class);
        intent.putExtra(EXTRA_TRIP, trip);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        receiveData();
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyAGVGhyzB1hQcXpFmg9QCP6JMI8Qp-768Y");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mDirectionService = APIUtils.getDirectionService();
        mMap.setOnMapClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        initMarker();
        initView();
        initToolbar();
        getDirection();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_clear_text_search_place:
                mEditTextSearch.setText("");
                if (mResults != null) {
                    mResults.clear();
                    mSearchPlaceAdapter.notifyDataSetChanged();
                }
                mConstraintLayoutPlaceInfo.setVisibility(View.GONE);
                mRecyclerViewSearchResult.setVisibility(View.GONE);
                break;
            case R.id.button_cancel:
                mSelectedPlace.remove();
                mConstraintLayoutPlaceInfo.setVisibility(View.GONE);
                break;
            case R.id.button_add:
                addSelectedPointToList();
                break;
            case R.id.btn_create_done:
                goBackWithResults();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showDialogToConfirm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogToConfirm() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.title_alert));
        builder.setMessage(getString(R.string.message_alert_save_changes));
        builder.setPositiveButton(getString(R.string.text_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goBackWithResults();
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


    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        rerenderMap();
        getPlaceInfo(latLng);
    }


    @Override
    public void onInfoWindowClick(final Marker marker) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.title_alert));
        builder.setMessage(getString(R.string.message_alert_delete_location));
        builder.setPositiveButton(getString(R.string.text_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Point pointOfMarker = (Point) marker.getTag();
                        mPoints.remove(pointOfMarker);
                        marker.remove();
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mHandler.removeCallbacks(mInputFinishChecker);
    }

    private void searchPlace(CharSequence s) {
        mProgressBar.setVisibility(View.VISIBLE);
        mImageViewClearText.setVisibility(View.GONE);
        mConstraintLayoutPlaceInfo.setVisibility(View.GONE);
        final PlaceService placeService = APIUtils.getPlaceService();
        placeService.searchPlace(s.toString(), getString(R.string.direction_api_key_ver2)).enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                PlaceResponse placeResponse = response.body();
                if (placeResponse.getStatus().equals("OK")) {
                    if (placeResponse.getResults().size() > 0) {
                        mProgressBar.setVisibility(View.GONE);
                        mImageViewClearText.setVisibility(View.VISIBLE);
                        showResults(placeResponse.getResults());
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mLastTextEdit = System.currentTimeMillis();
            mHandler.postDelayed(mInputFinishChecker, delay);
        }
    }

    @Override
    public void onItemSearchPlaceClick(Result result) {
        showPlaceInfo(result);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                mType = TYPE_WAYPOINT;
                break;
            case 1:
                mType = TYPE_CHECK_IN;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initView() {
        mHandler = new Handler();
        mEditTextSearch = findViewById(R.id.edit_search_waypoint);
        mEditTextSearch.addTextChangedListener(this);
        mTextViewName = findViewById(R.id.text_place_name);
        mTextViewAddress = findViewById(R.id.text_address);
        mSpinner = findViewById(R.id.spinner_type);
        String[] items = new String[]{getString(R.string.waypoint), getString(R.string.check_in)};
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mSpinner.setAdapter(stringArrayAdapter);
        mSpinner.setOnItemSelectedListener(this);
        mRatingBar = findViewById(R.id.rating_bar);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mButtonAdd = findViewById(R.id.button_add);
        mButtonAdd.setOnClickListener(this);
        mButtonCancel = findViewById(R.id.button_cancel);
        mButtonCancel.setOnClickListener(this);
        mImageViewClearText = findViewById(R.id.image_clear_text_search_place);
        mImageViewClearText.setOnClickListener(this);
        mRecyclerViewSearchResult = findViewById(R.id.recycler_search_place_result);
        mRecyclerViewSearchResult.setHasFixedSize(false);
        mRecyclerViewSearchResult.setLayoutManager(new LinearLayoutManager(this));
        mConstraintLayoutPlaceInfo = findViewById(R.id.constraint_layout_place_info);
        if (mTrip.getSpecialPoints() != null) {
            mPoints = (ArrayList<Point>) mTrip.getSpecialPoints();
            drawMarkers();
        } else
            mPoints = new ArrayList<>();
    }

    private Runnable mInputFinishChecker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (mLastTextEdit + delay - 500)) {
                searchPlace(mEditTextSearch.getText().toString());
            }
        }
    };

    private void initMarker() {
        Drawable myDrawableA = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_a_location2, null);
        Drawable myDrawableB = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_location, null);
        BitmapDescriptor markerBitmapA = getMarkerIconFromDrawable(myDrawableA);
        BitmapDescriptor markerBitmapB = getMarkerIconFromDrawable(myDrawableB);
        mStartMarker = mMap.addMarker(new MarkerOptions()
                .icon(markerBitmapA)
                .position(new LatLng(mTrip.getStartPoint().getLocation().getLat(), mTrip.getStartPoint().getLocation().getLng()))
                .title(getString(R.string.starting_point)));
        mEndMarker = mMap.addMarker(new MarkerOptions()
                .icon(markerBitmapB)
                .position(new LatLng(mTrip.getEndPoint().getLocation().getLat(), mTrip.getEndPoint().getLocation().getLng()))
                .title(mTrip.getEndPoint().getName()));
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.add_waypoint_title));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonDone = mToolbar.findViewById(R.id.btn_create_done);
        mButtonDone.setOnClickListener(this);
    }

    private void receiveData() {
        mTrip = new Trip();
        Intent intent = getIntent();
        mTrip = intent.getExtras().getParcelable(EXTRA_TRIP);
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextSearch.getWindowToken(), 0);
    }

    private void rerenderMap() {
        initMarker();
        drawPoly(mRouteResponse);
        drawMarkers();
    }

    private void drawPoly(RouteResponse routeResponse) {
        mRouteResponse = routeResponse;
        PolylineOptions polygonOptions = new PolylineOptions();
        polygonOptions.width(15).color(getResources().getColor(R.color.colorPrimary));
        for (Step step : routeResponse.getRoutes().get(0).getLegs().get(0).getSteps()) {
            polygonOptions.addAll(PolyUtil.decode(step.getPolyline().getPoints()));
        }
        mMap.addPolyline(polygonOptions);
        showRoute();
    }

    private void drawMarkers() {
        if (mMarkers == null) mMarkers = new ArrayList<>();
        if (mPoints != null) {
            for (Point point : mPoints) {
                Marker marker = mMap.addMarker(new MarkerOptions().title(point.getName())
                        .position(new LatLng(point.getLocation().getLat(), point.getLocation().getLng())));
                marker.setTag(point);
                if (point.getType().equals(TYPE_WAYPOINT)) {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                } else {
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }
                mMarkers.add(marker);
            }
        }
    }

    private void addSelectedPointToList() {
        mSeletedPoint = new Point();
        mSeletedPoint.setType(mType);
        mSeletedPoint.setLocation(mResult.getGeometry().getLocation());
        mSeletedPoint.setName(mResult.getName());
        mSeletedPoint.setCreatorId(FirebaseAuth.getInstance().getUid());
        mSeletedPoint.setIdTrip(mTrip.getCode());
        mPoints.add(mSeletedPoint);
        Toast.makeText(this, getString(R.string.added_waypoint), Toast.LENGTH_SHORT).show();
        mMap.clear();
        rerenderMap();
    }

    private void showRoute() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mEndMarker.getPosition());
        builder.include(mStartMarker.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 300;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    private void getDirection() {
        String origin = mTrip.getStartPoint().getLocation().getLat() + "," + mTrip.getStartPoint().getLocation().getLng();
        String destination = mTrip.getEndPoint().getLocation().getLat() + "," + mTrip.getEndPoint().getLocation().getLng();
        mDirectionService.getRoute(origin, destination, getString(R.string.direction_api_key_ver2))
                .enqueue(new Callback<RouteResponse>() {
                    @Override
                    public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                        RouteResponse routeResponse = response.body();
                        if (routeResponse.getStatus().equals("REQUEST_DENIED")) {
                        } else {
                            drawPoly(routeResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<RouteResponse> call, Throwable t) {
                    }
                });
    }


    private void getPlaceInfo(LatLng latLng) {
        GeocodeService geocodeService = APIUtils.getGeocodeService();
        String latlng = latLng.latitude + "," + latLng.longitude;
        geocodeService.getPlace(latlng, getString(R.string.direction_api_key_ver2)).enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
                if (response.body().getResults().size() > 0) {
                    getPlaceInfoById(response.body().getResults().get(0).getPlaceId());
                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {

            }
        });
    }

    private void getPlaceInfoById(String id) {
        String placeId = id;
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,
                Place.Field.RATING, Place.Field.OPENING_HOURS, Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        PlacesClient placesClient = Places.createClient(this);
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                Place place = fetchPlaceResponse.getPlace();
                Result result = new Result();
                if (place.getName() != null) {
                    result.setName(place.getName());
                } else {
                    result.setName("");
                }
                if (place.getAddress() != null) {
                    result.setFormattedAddress(place.getAddress());
                } else {
                    result.setFormattedAddress("");
                }
                if (place.getRating() != null) {
                    result.setRating(place.getRating());
                } else {
                    result.setRating(0.0);
                }
                Geometry geometry = new Geometry();
                geometry.setLocation(new Location(place.getLatLng().latitude, place.getLatLng().longitude));
                result.setGeometry(geometry);
                showPlaceInfo(result);
            }
        });
    }

    private void showPlaceInfo(Result result) {
        dismissKeyboard();
        mResult = result;
        LatLng latLng = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
        mSelectedPlace = mMap.addMarker(new MarkerOptions().title(result.getName()).position(latLng));
        mRecyclerViewSearchResult.setVisibility(View.GONE);
        mConstraintLayoutPlaceInfo.setVisibility(View.VISIBLE);
        mTextViewName.setText(result.getName());
        mTextViewAddress.setText(result.getFormattedAddress());
        mRatingBar.setRating(result.getRating().floatValue());
    }

    private void showResults(List<Result> results) {
        mRecyclerViewSearchResult.setVisibility(View.VISIBLE);
        mResults = results;
        mSearchPlaceAdapter = new SearchPlaceAdapter(getApplicationContext(), mResults, this);
        mRecyclerViewSearchResult.setAdapter(mSearchPlaceAdapter);
        mSearchPlaceAdapter.notifyDataSetChanged();
    }

    private void goBackWithResults() {
        Intent returnIntent = new Intent();
        returnIntent.putParcelableArrayListExtra(EXTRA_POINTS, mPoints);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}
