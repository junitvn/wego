package com.lamnn.wego.screen.info_user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.data.remote.EventService;
import com.lamnn.wego.screen.create_event.CreateEventActivity;
import com.lamnn.wego.utils.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoUserActivity extends AppCompatActivity implements View.OnClickListener, UserEventAdapter.OnEventItemClickListener {
    public static final String EXTRA_USER = "EXTRA_USER";
    private Toolbar mToolbar;
    private UserLocation mUserLocation;
    private FloatingActionButton mMenuItemCar, mMenuItemGas, mMenuItemCustom;
    private FloatingActionMenu mMenu;
    private static final String TAG = "Info user event";
    private RecyclerView mRecyclerViewEvent;
    private UserEventAdapter mUserEventAdapter;
    private List<Event> mEvents;
    private ImageView mImageViewRefresh;
    public static final String CHANNEL_ID = "lamnn";
    private ProgressBar mProgressBar;

    public static Intent getIntent(Context context, UserLocation userLocation) {
        Intent intent = new Intent(context, InfoUserActivity.class);
        intent.putExtra(EXTRA_USER, userLocation);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);
        receiveData();
        initView();
        initToolbar();
        getEventData();
        createNotificationChannel();
        FirebaseMessaging.getInstance().subscribeToTopic("car");
    }

    private void getEventData() {
        showLoading();
//        EventService eventService = APIUtils.getEventService();
//        eventService.getAllEvent(mUserLocation).enqueue(new Callback<List<Event>>() {
//            @Override
//            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
//                showEvents(response.body());
//                hideLoading();
//            }
//
//            @Override
//            public void onFailure(Call<List<Event>> call, Throwable t) {
//                hideLoading();
//            }
//        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("trip_id", mUserLocation.getUser().getActiveTrip())
                .whereEqualTo("user_id", mUserLocation.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<Event> events = new ArrayList<>();
                        Event event;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(doc.getData());
                            event = gson.fromJson(jsonElement, Event.class);
                            Timestamp timestamp = (Timestamp) doc.getData().get("time_stamp");
                            event.setTimeStamp(new MyTimeStamp(timestamp.getSeconds() + ""));
                            event.setEventId(doc.getId());
                            events.add(event);
                            Log.d(TAG, "onEvent: ");
                        }
                        showEvents(events);
                        hideLoading();
                    }
                });
    }


    private void showEvents(List<Event> events) {
        mEvents = events;
        mUserEventAdapter = new UserEventAdapter(this, mEvents, this);
        mRecyclerViewEvent.setAdapter(mUserEventAdapter);
        mUserEventAdapter.notifyDataSetChanged();
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

    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (mUserLocation != null) {
            getSupportActionBar().setTitle("Me");
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
        mUserLocation = getIntent().getExtras().getParcelable(EXTRA_USER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_item_car:
                createEvent("Car broken");
                mMenu.close(false);
                break;
            case R.id.menu_item_gas:
                createEvent("Out of gas");
                mMenu.close(false);
                break;
            case R.id.menu_item_custom:
                startActivity(CreateEventActivity.getIntent(this, mUserLocation.getUser()));
                mMenu.close(false);
                break;
            case R.id.image_refresh:
                getEventData();
                break;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "lamnn";
            String description = "description chanel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void createEvent(String type) {
        showLoading();
        Event event = new Event();
        event.setUser(mUserLocation.getUser());
        event.setUserId(mUserLocation.getUid());
        event.setTripId(mUserLocation.getUser().getActiveTrip());
        event.setTitle(type);
        event.setStatus("waiting");
        EventService eventService = APIUtils.getEventService();
        eventService.createEvent(event).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                hideLoading();
                Log.d(TAG, "onResponse: Created event CAR");
//                getEventData();
                Toast.makeText(InfoUserActivity.this, "Created event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                hideLoading();
            }
        });
    }

    @Override
    public void onEventItemClick(Event event) {

    }

    @Override
    public void onEventItemLongClick(Event event) {
        updateEvent(event);
    }

    private void updateEvent(Event event) {
        showLoading();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(event.getEventId())
                .update("status", "done")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        hideLoading();
                        Toast.makeText(InfoUserActivity.this, "Changed status", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        hideLoading();
                    }
                });
    }
}
