package com.lamnn.wego.screen.trip.join_trip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.JoinTripService;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinTripActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    public static final String EXTRA_USER = "EXTRA_USER";
    private Toolbar mToolbar;
    private Button mButtonDone;
    private User mUser;
    private EditText mTextCode;
    private JoinTripService mJoinTripService;
    private String TAG = "JOIN_TRIP_ACTIVITY";
    private ProgressBar mProgressBar;

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, JoinTripActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_trip);
        initView();
        initToolbar();
        receiveData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_join_done:
                joinTrip();
                break;
        }
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

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void joinTrip() {
        String code = mTextCode.getText().toString();
        showLoading();
        mUser.setActiveTrip(code);
        mJoinTripService = APIUtils.getJoinTripService();
        mJoinTripService.joinTrip(mUser).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body() == null || response.body() == false) {
                    hideLoading();
                    Toast.makeText(JoinTripActivity.this, getString(R.string.text_id_trip_error), Toast.LENGTH_SHORT).show();
                } else {
                    hideLoading();
                    Toast.makeText(JoinTripActivity.this, getString(R.string.text_success), Toast.LENGTH_SHORT).show();
                    startActivity(MapsActivity.getIntent(getApplicationContext()));
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
            }
        });
    }

    private void initView() {
        mTextCode = findViewById(R.id.text_enter_code);
        mTextCode.setOnEditorActionListener(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.text_join_a_trip));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mButtonDone = mToolbar.findViewById(R.id.btn_join_done);
        mButtonDone.setOnClickListener(this);
    }

    private void receiveData() {
        mUser = new User();
        mUser = getIntent().getExtras().getParcelable(EXTRA_USER);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            joinTrip();
        }
        return false;
    }
}
