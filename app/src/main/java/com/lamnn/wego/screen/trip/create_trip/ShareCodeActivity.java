package com.lamnn.wego.screen.trip.create_trip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.utils.APIUtils;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareCodeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_TRIP = "EXTRA_TRIP";
    private Toolbar mToolbar;
    private Button mButtonDone;
    private Trip mTrip;
    private TripService mTripService;
    private TextView mTextCode;
    private ProgressBar mProgressBar;

    public static Intent getIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, ShareCodeActivity.class);
        intent.putExtra(EXTRA_TRIP, trip);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_code);
        initView();
        receiveData();
        initToolbar();
        generateCode();
    }

    private void generateCode() {
        String code = generateRandomIntIntRange();
        mTextCode.setText(code);
        mTrip.setCode(code);
    }

    private void receiveData() {
        mTrip = new Trip();
        Intent intent = getIntent();
        mTrip = intent.getExtras().getParcelable(EXTRA_TRIP);
    }

    private void initView() {
        mTextCode = findViewById(R.id.text_code);
        mProgressBar = findViewById(R.id.progress_bar_loading);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.text_invite_friends));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonDone = mToolbar.findViewById(R.id.btn_create_done);
        mButtonDone.setOnClickListener(this);
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
                //TODO create trip and update db => Go to Maps
                createTrip();
                break;
        }
    }

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void createTrip() {
        showLoading();
        mTripService = APIUtils.getTripService();
        mTripService.createTrip(mTrip).enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                Log.d("Created trip!", response + " ");
                hideLoading();
                startActivity(MapsActivity.getIntent(getApplicationContext()));
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.d("FAIL_HIHI", t + " ");
            }
        });
    }

    public static String generateRandomIntIntRange() {
        Random r = new Random();
        int min = 100000;
        int max = 999999;
        int res = r.nextInt((max - min) + 1) + min;
        return res + "";
    }
}
