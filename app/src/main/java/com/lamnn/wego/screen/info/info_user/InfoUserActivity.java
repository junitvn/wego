package com.lamnn.wego.screen.info.info_user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.lamnn.wego.screen.info.info_member.InfoMemberActivity;
import com.lamnn.wego.screen.info.info_member.PopupMemberAdapter;
import com.lamnn.wego.screen.event.CreateEventActivity;
import com.lamnn.wego.screen.map.MapsActivity;

import java.util.List;

import static com.lamnn.wego.screen.event.CreateEventActivity.EXTRA_EVENT;
import static com.lamnn.wego.utils.AppUtils.STATUS_DELETED;
import static com.lamnn.wego.utils.AppUtils.STATUS_DONE;
import static com.lamnn.wego.utils.AppUtils.TYPE_CAR;
import static com.lamnn.wego.utils.AppUtils.TYPE_COMING;
import static com.lamnn.wego.utils.AppUtils.TYPE_GAS;
import static com.lamnn.wego.utils.AppUtils.TYPE_WAITING;

public class InfoUserActivity extends AppCompatActivity implements View.OnClickListener,
        UserEventAdapter.OnEventItemClickListener, InfoUserContract.View, PopupMemberAdapter.OnMemberItemClickListener {
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
    private Dialog mDialog;

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
        mPresenter = new InfoUserPresenter(this, this);
        mPresenter.getUserLocationData(mUserLocation);
        mDialog = new Dialog(this);
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
            if (type.equals(TYPE_COMING)) {
                mTextViewEventStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                mTextViewEventStatus.setTextColor(getResources().getColor(R.color.colorCaution));
            }
        } else {
            mRecyclerViewEvent.setPadding(0, 0, 0, mToolbar.getHeight());
        }
    }

    @Override
    public void showMemberPopup(List<UserLocation> userLocations, String type) {
        mDialog.setContentView(R.layout.member_popup);
        RecyclerView recyclerViewPopupMember = mDialog.findViewById(R.id.recycler_popup_member);
        recyclerViewPopupMember.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPopupMember.setHasFixedSize(true);
        TextView textViewPopupTitle;
        ImageView imageViewClosePopup;
        textViewPopupTitle = mDialog.findViewById(R.id.text_title_popup);
        if (type.equals(TYPE_COMING)) {
            textViewPopupTitle.setText(getString(R.string.people_who_coming));
        } else {
            textViewPopupTitle.setText(getString(R.string.people_who_waiting));
        }
        imageViewClosePopup = mDialog.findViewById(R.id.image_close_popup);
        imageViewClosePopup.setOnClickListener(this);
        PopupMemberAdapter popupMemberAdapter = new PopupMemberAdapter(this, userLocations, this);
        recyclerViewPopupMember.setAdapter(popupMemberAdapter);
        popupMemberAdapter.notifyDataSetChanged();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();
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
                type = TYPE_CAR;
                mPresenter.createQuickEvent(mUserLocation, type);
                mMenu.close(false);
                break;
            case R.id.menu_item_gas:
                type = TYPE_GAS;
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
            case R.id.image_close_popup:
                mDialog.dismiss();
                break;
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
        mPresenter.getListMember(event.getComingUsers(), TYPE_COMING);
    }

    @Override
    public void onEventTextWhoWaitingClick(Event event) {
        mPresenter.getListMember(event.getWaitingUsers(), TYPE_WAITING);
    }

    @Override
    public void onButtonEditClick(Event event) {
        startActivity(CreateEventActivity.getIntent(this, event));
    }

    @Override
    public void onButtonDeleteClick(Event event) {
        mPresenter.updateStatus(event, STATUS_DELETED);
    }

    @Override
    public void onButtonDoneClick(Event event) {
        if (event.getStatus().equals(STATUS_DONE)) {
            mPresenter.updateStatus(event, TYPE_WAITING);
        } else {
            mPresenter.updateStatus(event, STATUS_DONE);
        }
    }

    @Override
    public void onMemberNameClick(UserLocation userLocation) {
        if (userLocation.getUid().equals(mUserLocation.getUid())) {
            mDialog.dismiss();
        } else {
            startActivity(InfoMemberActivity.getIntent(this, userLocation));
        }
    }

    @Override
    public void onGoToLocationClick(UserLocation userLocation) {
        startActivity(MapsActivity.getIntent(this, userLocation));
    }

    @Override
    public void onCallClick(UserLocation userLocation) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + userLocation.getUser().getPhone()));
        startActivity(intent);
    }
}
