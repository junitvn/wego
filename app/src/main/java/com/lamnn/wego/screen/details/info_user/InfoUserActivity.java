package com.lamnn.wego.screen.details.info_user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.screen.details.info_member.InfoMemberActivity;
import com.lamnn.wego.screen.event.create_event.CreateEventActivity;

import java.util.List;

import static com.lamnn.wego.screen.event.create_event.CreateEventActivity.EXTRA_EVENT;

public class InfoUserActivity extends AppCompatActivity implements View.OnClickListener,
        UserEventAdapter.OnEventItemClickListener, InfoUserContract.View {
    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_USER_LOCATION = "EXTRA_USER_LOCATION";
    private Toolbar mToolbar;
    private UserLocation mUserLocation;
    private FloatingActionButton mMenuItemCar, mMenuItemGas, mMenuItemCustom;
    private FloatingActionMenu mMenu;
    private RecyclerView mRecyclerViewEvent;
    private UserEventAdapter mUserEventAdapter;
    private List<Event> mEvents;
    private TextView mTextViewEventStatus;
    private ConstraintLayout mLayoutUserStatus;
    private ImageView mImageViewRefresh, mImageViewGoToEvent;
    private Event mEvent;
    private Event mEventFromStatus;
    public static final String CHANNEL_ID = "lamnn";
    private ProgressBar mProgressBar;
    private InfoUserPresenter mPresenter;

    public static Intent getIntent(Context context, UserLocation userLocation) {
        Intent intent = new Intent(context, InfoUserActivity.class);
        intent.putExtra(EXTRA_USER_LOCATION, userLocation);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        receiveData();
        if (mUserLocation == null && mEvent != null) {
            mUserLocation = new UserLocation();
            User user = new User();
            user.setUid(mEvent.getUserId());
            user.setActiveTrip(mEvent.getTripId());
            mUserLocation.setUser(user);
        }
        initView();
        initToolbar();
        createNotificationChannel();
        mPresenter = new InfoUserPresenter(this, this);
        mPresenter.getUserLocationData(mUserLocation);
    }


    @Override
    public void showEvents(List<Event> events) {
        mEvents = events;
        mUserEventAdapter = new UserEventAdapter(this, mEvents, this);
        mRecyclerViewEvent.setAdapter(mUserEventAdapter);
        mUserEventAdapter.notifyDataSetChanged();
    }

    @Override
    public void showUserStatus(UserLocation userLocation, String status, String type, Event event) {
        if (userLocation == null) return;
        mUserLocation = userLocation;
        FirebaseMessaging.getInstance().subscribeToTopic(mUserLocation.getUser().getActiveTrip());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mMenuItemCar = findViewById(R.id.menu_item_car);
        mMenuItemCar.setOnClickListener(this);
        mMenuItemCar.setImageResource(R.drawable.ic_b);
        mMenuItemGas = findViewById(R.id.menu_item_gas);
        mMenuItemGas.setOnClickListener(this);
        mMenuItemGas.setImageResource(R.drawable.ic_gas_color);
        mMenuItemCustom = findViewById(R.id.menu_item_custom);
        mMenuItemCustom.setOnClickListener(this);
        mMenuItemCustom.setImageResource(R.drawable.ic_edit);
        mMenu = findViewById(R.id.menu);
        mRecyclerViewEvent = findViewById(R.id.recycler_event);
        mRecyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewEvent.setHasFixedSize(true);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mTextViewEventStatus = findViewById(R.id.text_event_status);
        mTextViewEventStatus.setOnClickListener(this);
        mLayoutUserStatus = findViewById(R.id.layout_user_status);
        mImageViewGoToEvent = findViewById(R.id.image_go_to_event);
        mImageViewGoToEvent.setOnClickListener(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (mUserLocation != null) {
            getSupportActionBar().setTitle(getString(R.string.text_me));
        }
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mImageViewRefresh = mToolbar.findViewById(R.id.image_refresh);
        mImageViewRefresh.setOnClickListener(this);
    }

    private void receiveData() {
        mUserLocation = new UserLocation();
        mUserLocation = getIntent().getExtras().getParcelable(EXTRA_USER_LOCATION);
        mEvent = getIntent().getExtras().getParcelable(EXTRA_EVENT);
    }

    @Override
    public void onClick(View v) {
        String type;
        switch (v.getId()) {
            case R.id.menu_item_car:
                type = "car";
                mPresenter.createQuickEvent(mUserLocation, type);
                mMenu.close(false);
                break;
            case R.id.menu_item_gas:
                type = "gas";
                mPresenter.createQuickEvent(mUserLocation, type);
                mMenu.close(false);
                break;
            case R.id.menu_item_custom:
                startActivity(CreateEventActivity.getIntent(this, mUserLocation));
                mMenu.close(false);
                break;
            case R.id.image_refresh:
                mPresenter.getEventData(mUserLocation);
                break;
            case R.id.text_event_status:
            case R.id.image_go_to_event:
                UserLocation userLocation = new UserLocation();
                userLocation.setUid(mEventFromStatus.getUserId());
                startActivity(InfoMemberActivity.getIntent(this, userLocation));
                break;
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

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
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
    public void onButtonEditClick(Event event) {
        startActivity(CreateEventActivity.getIntent(this, event));
    }

    @Override
    public void onButtonDeleteClick(Event event) {
        mPresenter.updateStatus(event, "deleted");
    }

    @Override
    public void onButtonDoneClick(Event event) {
        if (event.getStatus().equals("done")) {
            mPresenter.updateStatus(event, "waiting");
        } else {
            mPresenter.updateStatus(event, "done");
        }
    }
}
