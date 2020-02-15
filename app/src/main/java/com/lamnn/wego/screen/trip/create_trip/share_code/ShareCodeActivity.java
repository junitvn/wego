package com.lamnn.wego.screen.trip.create_trip.share_code;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.TripService;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.profile.detail.ProfileDetailActivity;
import com.lamnn.wego.utils.APIUtils;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareCodeActivity extends AppCompatActivity implements View.OnClickListener,
        ShareCodeContract.View, InvitationFriendAdapter.OnUserFoundItemClickListener {
    public static final String EXTRA_TRIP = "EXTRA_TRIP";
    private Toolbar mToolbar;
    private Button mButtonDone;
    private Trip mTrip;
    private TripService mTripService;
    private TextView mTextCode;
    private ProgressBar mProgressBar;
    private ShareCodeContract.Presenter mPresenter;
    private ImageView mImageViewCopy;
    private RecyclerView mRecyclerViewInviteFriend;
    private InvitationFriendAdapter mAdapter;
    private ArrayList<User> mUsers;

    public static Intent getIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, ShareCodeActivity.class);
        intent.putExtra(EXTRA_TRIP, trip);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_code);
        receiveData();
        initView();
        initToolbar();
        mPresenter = new ShareCodePresenter(this, this);
        mPresenter.getUserFriends();
    }


    private void receiveData() {
        mTrip = new Trip();
        Intent intent = getIntent();
        mTrip = intent.getExtras().getParcelable(EXTRA_TRIP);
    }

    private void initView() {
        mTextCode = findViewById(R.id.text_code);
        mTextCode.setText(mTrip.getCode());
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mRecyclerViewInviteFriend = findViewById(R.id.recycler_invite_friends);
        mRecyclerViewInviteFriend.setHasFixedSize(false);
        mRecyclerViewInviteFriend.setLayoutManager(new LinearLayoutManager(this));
        mImageViewCopy = findViewById(R.id.image_copy);
        mImageViewCopy.setOnClickListener(this);
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
                createTrip();
                break;
            case R.id.image_copy:
                mPresenter.copyIdTripToClipboard(mTextCode.getText().toString());
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
        startActivity(MapsActivity.getIntent(getApplicationContext()));

//        showLoading();
//        mTripService = APIUtils.getTripService();
//        mTripService.createTrip(mTrip).enqueue(new Callback<Trip>() {
//            @Override
//            public void onResponse(Call<Trip> call, Response<Trip> response) {
//                hideLoading();
//            }
//
//            @Override
//            public void onFailure(Call<Trip> call, Throwable t) {
//            }
//        });
    }

    @Override
    public void showUserFriend(ArrayList<User> users) {
        mUsers = removeParticipatedMember(users);
        mAdapter = new InvitationFriendAdapter(this, mUsers, this);
        mAdapter.setTripId(mTrip.getCode());
        mRecyclerViewInviteFriend.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private ArrayList<User> removeParticipatedMember(ArrayList<User> users) {
        ArrayList<User> removedUsers = users;
        try {
            if (mTrip.getMembers() != null && users != null)
                for (String uid : mTrip.getMembers()) {
                    for (User user : users) {
                        if (uid.equals(user.getUid())) {
                            removedUsers.remove(user);
                        }
                    }
                }
        } catch (ConcurrentModificationException e){
            Toast.makeText(this, getString(R.string.error_when_load_friend), Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(this, getString(R.string.error_when_load_friend), Toast.LENGTH_SHORT).show();
        }

        return removedUsers;
    }

    @Override
    public void onUserFoundClick(User user) {
        startActivity(ProfileDetailActivity.getIntent(getApplicationContext(), user));
    }

    @Override
    public void onInviteStatusClick(User user) {
        mPresenter.onInviteStatusClick(user, mTrip);
    }
}
