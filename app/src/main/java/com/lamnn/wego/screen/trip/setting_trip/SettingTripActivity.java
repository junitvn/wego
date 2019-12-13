package com.lamnn.wego.screen.trip.setting_trip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Point;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.TripSetting;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.screen.trip.create_trip.RouteActivity;
import com.lamnn.wego.screen.trip.create_trip.ShareCodeActivity;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.trip.create_trip.SpecialPointAdapter;
import com.lamnn.wego.utils.APIUtils;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.lamnn.wego.screen.info.info_user.InfoUserActivity.EXTRA_USER_LOCATION;
import static com.lamnn.wego.screen.trip.create_trip.CreateTripActivity.REQUEST_WAYPOINTS;
import static com.lamnn.wego.screen.trip.create_trip.RouteActivity.EXTRA_POINTS;

public class SettingTripActivity extends AppCompatActivity implements View.OnClickListener, SettingTripContract.View, CompoundButton.OnCheckedChangeListener {

    private EditText mTextNameTrip, mTextIdTrip;
    private EditText mEditTextMinDistance, mEditTextTimeToRepeat;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private TextView mTextViewMinDistance, mTextViewTimeToRepeat;
    private Button mButtonDone, mButtonOutTrip;
    private Trip mTrip;
    private UserLocation mUserLocation;
    private ImageView mImageViewCopy;
    private SettingTripContract.Presenter mPresenter;
    private SwitchCompat mSwitchCompat;
    private ArrayList<Point> mPoints;
    private SpecialPointAdapter mPointAdapter;
    private RecyclerView mRecyclerViewPoint;
    private TextView mTextViewEditRoute;

    public static Intent getIntent(Context context, Trip trip, UserLocation userLocation) {
        Intent intent = new Intent(context, SettingTripActivity.class);
        intent.putExtra(ShareCodeActivity.EXTRA_TRIP, trip);
        intent.putExtra(EXTRA_USER_LOCATION, userLocation);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_trip);
        receiveData();
        initView();
        initToolbar();
        mPresenter = new SettingTripPresenter(this, this);
        mPresenter.updateUserLocation(mUserLocation);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create_done:
                updateTrip();
                break;
            case R.id.button_out_trip:
                outTrip();
                break;
            case R.id.image_copy:
                mPresenter.copyIdTripToClipboard(mTrip.getCode());
                break;
            case R.id.text_edit_route:
                startActivityForResult(RouteActivity.getIntent(this, mTrip), REQUEST_WAYPOINTS);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        if (requestCode == REQUEST_WAYPOINTS) {
            ArrayList<Point> points = intent.getParcelableArrayListExtra(EXTRA_POINTS);
            if (mPoints != null) {
                mPointAdapter.updateData(points);
                mTrip.setSpecialPoints(mPoints);
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
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
    public void updateUserLocation(UserLocation userLocation) {
        mUserLocation = userLocation;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        toggleSwitch(isChecked);
    }

    private void initView() {
        mTextNameTrip = findViewById(R.id.text_name_user);
        mTextIdTrip = findViewById(R.id.text_phone_number);
        mTextIdTrip.setEnabled(false);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mButtonOutTrip = findViewById(R.id.button_out_trip);
        mButtonOutTrip.setOnClickListener(this);
        mSwitchCompat = findViewById(R.id.switch_receive_notification);
        mSwitchCompat.setOnCheckedChangeListener(this);
        mTextNameTrip.setText(mTrip.getName());
        mTextIdTrip.setText(mTrip.getCode());
        mEditTextMinDistance = findViewById(R.id.edit_min_distance);
        mEditTextTimeToRepeat = findViewById(R.id.edit_time_to_repeat);
        mTextViewMinDistance = findViewById(R.id.text_min_distance);
        mTextViewTimeToRepeat = findViewById(R.id.text_time_to_repeat);
        mImageViewCopy = findViewById(R.id.image_copy);
        mImageViewCopy.setOnClickListener(this);
        if (mTrip.getTripSetting() != null) {
            mSwitchCompat.setChecked(mTrip.getTripSetting().getReceiveNotification());
            toggleSwitch(mTrip.getTripSetting().getReceiveNotification());
            mEditTextTimeToRepeat.setText(mTrip.getTripSetting().getTimeToRepeat() + "");
            mEditTextMinDistance.setText(mTrip.getTripSetting().getMinDistance() + "");
        }
        mRecyclerViewPoint = findViewById(R.id.recycler_point);
        mRecyclerViewPoint.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewPoint.setHasFixedSize(false);
        mTextViewEditRoute = findViewById(R.id.text_edit_route);
        mTextViewEditRoute.setOnClickListener(this);
        if (mTrip.getSpecialPoints() != null) {
            mPoints = (ArrayList<Point>) mTrip.getSpecialPoints();
            mPointAdapter = new SpecialPointAdapter(this, mPoints);
            mRecyclerViewPoint.setAdapter(mPointAdapter);
            mPointAdapter.notifyDataSetChanged();
        }
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (mTrip != null && !mTrip.getName().equals("")) {
            getSupportActionBar().setTitle(mTrip.getName());
        }
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonDone = mToolbar.findViewById(R.id.btn_create_done);
        mButtonDone.setOnClickListener(this);
    }

    private void outTrip() {
        showLoading();
//        Use "name" as code of selected trip to send request
        mUserLocation.getUser().setName(mTrip.getCode());
        mPresenter.outTrip(mUserLocation);
    }

    private void updateTrip() {
        showLoading();
        mTrip.setName(mTextNameTrip.getText().toString());
        TripSetting tripSetting = new TripSetting();
        if (mEditTextMinDistance.getText().toString() != null) {
            tripSetting.setMinDistance(Long.parseLong(mEditTextMinDistance.getText().toString()));
        } else {
            Toast.makeText(this, "Missing information", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEditTextTimeToRepeat.getText().toString() != null) {
            tripSetting.setTimeToRepeat(Integer.parseInt(mEditTextTimeToRepeat.getText().toString()));
        } else {
            Toast.makeText(this, "Missing information", Toast.LENGTH_SHORT).show();
            return;
        }
        tripSetting.setReceiveNotification(mSwitchCompat.isChecked());
        mTrip.setTripSetting(tripSetting);
        mToolbar.setTitle(mTrip.getName());
        mPresenter.updateTrip(mTrip);
    }

    private void receiveData() {
        mTrip = new Trip();
        mUserLocation = new UserLocation();
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mTrip = intent.getExtras().getParcelable(ShareCodeActivity.EXTRA_TRIP);
            mUserLocation = intent.getExtras().getParcelable(EXTRA_USER_LOCATION);
        }

    }

    private void toggleSwitch(boolean isChecked) {
        int color = isChecked
                ? getResources().getColor(R.color.colorEditTextEnable)
                : getResources().getColor(R.color.colorEditTextDisable);
        mEditTextMinDistance.setFocusable(isChecked);
        mEditTextTimeToRepeat.setFocusable(isChecked);
        mEditTextMinDistance.setFocusableInTouchMode(isChecked);
        mEditTextTimeToRepeat.setFocusableInTouchMode(isChecked);
        mEditTextMinDistance.setTextColor(color);
        mEditTextTimeToRepeat.setTextColor(color);
        mTextViewMinDistance.setTextColor(color);
        mTextViewTimeToRepeat.setTextColor(color);
    }
}
