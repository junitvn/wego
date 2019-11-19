package com.lamnn.wego.screen.details.info_member;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.Distance;
import com.lamnn.wego.screen.details.info_user.InfoUserActivity;

import java.util.List;

public class InfoMemberActivity extends AppCompatActivity implements View.OnClickListener, InfoMemberContract.View, MemberEventAdapter.OnEventItemClickListener {
    public static final String EXTRA_USER = "EXTRA_USER";
    private Toolbar mToolbar;
    private String mMyUserId;
    private UserLocation mUserLocation;
    private ImageView mImageCall, mImageStatus;
    private RecyclerView mRecyclerViewEvent;
    private MemberEventAdapter mMemberEventAdapter;
    private List<Event> mEvents;
    private ProgressBar mProgressBar;
    private InfoMemberContract.Presenter mPresenter;
    private TextView mTextViewEventStatus;
    private ConstraintLayout mLayoutUserStatus;
    private ImageView mImageViewRefresh, mImageViewGoToEvent;
    private Event mEventFromStatus;

    public static Intent getIntent(Context context, UserLocation userLocation) {
        Intent intent = new Intent(context, InfoMemberActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_USER, userLocation);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_member);
        mPresenter = new InfoMemberPresenter(this, this);
        initView();
        receiveData();
        initToolbar();
        mPresenter.getUserLocationData(mUserLocation.getUid());
        mMyUserId = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mUserLocation.getUser().getPhone()));
                startActivity(intent);
                break;
            case R.id.text_event_status_member:
            case R.id.image_go_to_event_member:
                UserLocation userLocation = new UserLocation();
                userLocation.setUid(mEventFromStatus.getUserId());
                if (userLocation.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                    startActivity(InfoUserActivity.getIntent(this, userLocation));
                } else {
                    startActivity(InfoMemberActivity.getIntent(this, userLocation));
                }
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

    private void initView() {
        mRecyclerViewEvent = findViewById(R.id.recycler_event);
        mRecyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewEvent.setHasFixedSize(true);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mTextViewEventStatus = findViewById(R.id.text_event_status_member);
        mTextViewEventStatus.setOnClickListener(this);
        mLayoutUserStatus = findViewById(R.id.layout_user_status_member);
        mImageViewGoToEvent = findViewById(R.id.image_go_to_event_member);
        mImageViewGoToEvent.setOnClickListener(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mImageCall = mToolbar.findViewById(R.id.image_call);
        mImageCall.setOnClickListener(this);
        mImageStatus = mToolbar.findViewById(R.id.image_status);
    }

    private void receiveData() {
        mUserLocation = new UserLocation();
        mUserLocation = getIntent().getExtras().getParcelable(EXTRA_USER);
    }


    @Override
    public void onEventItemClick(Event event) {

    }

    @Override
    public void onEventItemLongClick(Event event) {
    }

    @Override
    public void onEventTextWhoComingClick(Event event) {

    }

    @Override
    public void onEventTextWhoWaitingClick(Event event) {

    }

    @Override
    public void onButtonImComingClick(Event event) {
        mPresenter.addComingMember(mMyUserId, event);
    }

    @Override
    public void onButtonImWaitingClick(Event event) {
        mPresenter.addWaitingMember(mMyUserId, event);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUserStatus(UserLocation userLocation, String status, String type, Event event) {
        if (userLocation == null) return;
        mUserLocation = userLocation;
        if (!status.equals("")) {
            mEventFromStatus = event;
            mLayoutUserStatus.setVisibility(View.VISIBLE);
            mTextViewEventStatus.setText(status);
            if (type.equals("coming")) {
                mTextViewEventStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mTextViewEventStatus.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        } else {
            mRecyclerViewEvent.setPadding(0, 0, 0, mToolbar.getHeight());
        }
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void updateUserLocation(UserLocation userLocation) {
        mUserLocation = userLocation;
        if (mUserLocation != null) {
            getSupportActionBar().setTitle(mUserLocation.getUser().getName());
            if (mUserLocation.getStatus().equals("online")) {
                mImageStatus.setImageResource(R.drawable.ic_online);
            }
        }
    }

    @Override
    public void showEvents(List<Event> events) {
        mEvents = events;
        mMemberEventAdapter = new MemberEventAdapter(this, mEvents, this);
        mRecyclerViewEvent.setAdapter(mMemberEventAdapter);
        mMemberEventAdapter.notifyDataSetChanged();
    }
}
