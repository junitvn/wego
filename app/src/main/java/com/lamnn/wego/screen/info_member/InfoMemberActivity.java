package com.lamnn.wego.screen.info_member;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.screen.info_user.UserEventAdapter;

import java.util.ArrayList;
import java.util.List;

public class InfoMemberActivity extends AppCompatActivity implements View.OnClickListener, UserEventAdapter.OnEventItemClickListener {
    public static final String EXTRA_USER = "EXTRA_USER";
    private Toolbar mToolbar;
    private User mUser;
    private ImageView mImageCall, mImageStatus;
    private RecyclerView mRecyclerViewEvent;
    private UserEventAdapter mUserEventAdapter;
    private List<Event> mEvents;
    private ProgressBar mProgressBar;

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, InfoMemberActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_member);
        initView();
        receiveData();
        initToolbar();
        getEventData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mUser.getPhone()));
                startActivity(intent);
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

    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (mUser != null) {
            getSupportActionBar().setTitle(mUser.getName());
        }
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mImageCall = mToolbar.findViewById(R.id.image_call);
        mImageCall.setOnClickListener(this);
        mImageStatus = mToolbar.findViewById(R.id.image_status);
        if (mUser != null && mUser.getStatus().equals("online")) {
            mImageStatus.setImageResource(R.drawable.ic_online);
        }
    }

    private void receiveData() {
        mUser = new User();
        mUser = getIntent().getExtras().getParcelable(EXTRA_USER);
    }

    private void getEventData() {
        showLoading();
//        EventService eventService = APIUtils.getEventService();
//        eventService.getAllEvent(mUser).enqueue(new Callback<List<Event>>() {
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
                .whereEqualTo("trip_id", mUser.getActiveTrip())
                .whereEqualTo("user_id", mUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
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
    public void onEventItemClick(Event event) {

    }

    @Override
    public void onEventItemLongClick(Event event) {
        updateEvent(event);
    }

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
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
                        hideLoading();
                        Toast.makeText(InfoMemberActivity.this, "Changed status", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoading();
                    }
                });
    }
}
