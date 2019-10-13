package com.lamnn.wego.screen.create_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.Photo;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.EventService;
import com.lamnn.wego.screen.info_user.InfoUserActivity;
import com.lamnn.wego.utils.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_USER = "EXTRA_USER";
    private Toolbar mToolbar;
    private User mUser;
    private Event mEvent;
    private EditText mEditTextTitle, mEditTextNote;
    private Button mButtonCreate;
    private static final String TAG = "CREATE EVENT";
    private ProgressBar mProgressBar;

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, CreateEventActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        receiveData();
        initView();
        initToolbar();
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
        mEditTextTitle = findViewById(R.id.text_event_type);
        mEditTextNote = findViewById(R.id.text_event_note);
        mProgressBar = findViewById(R.id.progress_bar_loading);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create new event");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonCreate = mToolbar.findViewById(R.id.button_create_event);
        mButtonCreate.setOnClickListener(this);
    }

    private void receiveData() {
        mUser = new User();
        mUser = getIntent().getExtras().getParcelable(EXTRA_USER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_create_event:
                createEvent();
                break;
        }
    }

    private void createEvent() {
        showLoading();
        mEvent = new Event();
        mEvent.setUser(mUser);
        mEvent.setUserId(mUser.getUid());
        mEvent.setTripId(mUser.getActiveTrip());
        mEvent.setLocation(mUser.getLocation());
        mEvent.setStatus("waiting");
        mEvent.setNote(mEditTextNote.getText().toString());
        mEvent.setTitle(mEditTextTitle.getText().toString());
        EventService eventService = APIUtils.getEventService();
        eventService.createEvent(mEvent).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                hideLoading();
                Toast.makeText(getApplicationContext(), "Created event", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse: Created event" + mEvent.getTitle());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {

            }
        });
    }

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

}
