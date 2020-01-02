package com.lamnn.wego.screen.event;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.lamnn.wego.screen.profile.update.ProfileUpdateActivity.GALLERY_REQUEST_CODE;
import static com.lamnn.wego.screen.profile.update.ProfileUpdateActivity.TAKE_PHOTO_REQUEST_CODE;
import static com.lamnn.wego.utils.AppUtils.TYPE_WAITING;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, CreateEventContract.View, EventPhotoAdapter.OnPhotoItemClickListener {
    public static final String EXTRA_USER = "EXTRA_USER";
    public static final String EXTRA_EVENT = "EXTRA_EVENT";
    private Toolbar mToolbar;
    private User mUser;
    private UserLocation mUserLocation;
    private Event mEvent;
    private EditText mEditTextTitle, mEditTextNote;
    private TextView mTextViewAddPhoto;
    private ImageView mImageViewAddPhoto;
    private Button mButtonCreate;
    private ProgressBar mProgressBar;
    private AlertDialog mDialog;
    private CreateEventContract.Presenter mPresenter;
    private Activity mActivity;
    private LinearLayout mLinearLayoutAddPhoto;
    private RecyclerView mRecyclerViewPhoto;
    private List<String> mUris;
    private EventPhotoAdapter mPhotoAdapter;
    private Boolean isUpdate;

    public static Intent getIntent(Context context, UserLocation userLocation) {
        Intent intent = new Intent(context, CreateEventActivity.class);
        intent.putExtra(EXTRA_USER, userLocation);
        return intent;
    }

    public static Intent getIntent(Context context, Event event) {
        Intent intent = new Intent(context, CreateEventActivity.class);
        intent.putExtra(EXTRA_EVENT, event);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        mPresenter = new CreateEventPresenter(this, this);
        mActivity = this;
        receiveData();
        initView();
        initToolbar();
        initDialog();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST_CODE:
                    File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.file_name_image));
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), this.getApplicationContext().getPackageName() + ".provider", file);
                    mUris.add(uri.toString());
                    mPhotoAdapter.notifyDataSetChanged();
                    if (mUris.size() == 5) mPresenter.hideAddPhotoLayout();
                    break;
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = null;
                    if (data != null) {
                        selectedImage = data.getData();
                    }
                    if (selectedImage != null) {
                        mUris.add(selectedImage.toString());
                        mPhotoAdapter.notifyDataSetChanged();
                        if (mUris.size() == 5) mPresenter.hideAddPhotoLayout();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initView() {
        mUris = new ArrayList<>();
        mEditTextTitle = findViewById(R.id.text_event_type);
        mEditTextNote = findViewById(R.id.text_event_note);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mTextViewAddPhoto = findViewById(R.id.text_add_photo);
        mTextViewAddPhoto.setOnClickListener(this);
        mImageViewAddPhoto = findViewById(R.id.image_add_photo);
        mImageViewAddPhoto.setOnClickListener(this);
        mLinearLayoutAddPhoto = findViewById(R.id.linear_add_photo);
        mRecyclerViewPhoto = findViewById(R.id.recycler_photo_event);
        mRecyclerViewPhoto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewPhoto.setHasFixedSize(true);
        if (mEvent != null) {
            mEditTextTitle.setText(mEvent.getTitle());
            mEditTextNote.setText(mEvent.getNote());
            mUris = mEvent.getPhotos();
        }
        mPhotoAdapter = new EventPhotoAdapter(this, mUris, this, true);
        mRecyclerViewPhoto.setAdapter(mPhotoAdapter);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonCreate = mToolbar.findViewById(R.id.button_create_event);
        mButtonCreate.setOnClickListener(this);
        if (mEvent != null) {
            getSupportActionBar().setTitle(getString(R.string.text_edit));
            mButtonCreate.setText(getString(R.string.text_done));
        } else
            getSupportActionBar().setTitle(getString(R.string.text_create_new_event));
    }

    private void receiveData() {
        mUserLocation = new UserLocation();
        mUserLocation = getIntent().getExtras().getParcelable(EXTRA_USER);
        mEvent = getIntent().getExtras().getParcelable(EXTRA_EVENT);
        if (mUserLocation == null) {
            isUpdate = true;
            mUser = mEvent.getUser();
        } else {
            isUpdate = false;
            mUser = mUserLocation.getUser();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_create_event:
                if (isUpdate) {
                    updateEvent();
                } else {
                    createEvent();
                }
                break;
            case R.id.image_add_photo:
            case R.id.text_add_photo:
                mDialog.show();
                break;
        }
    }

    private void updateEvent() {
        if (!mEditTextNote.getText().toString().equals("")) {
            mEvent.setNote(mEditTextNote.getText().toString());
        }
        mEvent.setTitle(mEditTextTitle.getText().toString());
        mEvent.setPhotos(mUris);
        mPresenter.updateEvent(mEvent);
    }

    private void createEvent() {
        showLoading();
        mEvent = new Event();
        mEvent.setUser(mUser);
        mEvent.setLocation(mUserLocation.getLocation());
        mEvent.setUserId(mUser.getUid());
        mEvent.setTripId(mUser.getActiveTrip());
        mEvent.setStatus(TYPE_WAITING);
        mEvent.setNote(mEditTextNote.getText().toString());
        mEvent.setTitle(mEditTextTitle.getText().toString());
        mEvent.setPhotos(mUris);
        mPresenter.createEvent(mEvent);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.text_choose_photo));
        String[] ways = {getString(R.string.text_choose_from_gallery), getString(R.string.text_take_photo)};
        builder.setItems(ways, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        mPresenter.choosePhoto(mActivity);
                        break;
                    case 1:
                        mPresenter.takePhoto(mActivity);
                        break;
                }
            }
        });
        mDialog = builder.create();
    }

    @Override
    public void hideAddPhotoLayout() {
        mLinearLayoutAddPhoto.setVisibility(View.GONE);
    }

    @Override
    public void showAddPhotoLayout() {
        mLinearLayoutAddPhoto.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPhotoItemClick(Uri uri) {
    }

    @Override
    public void onRemoveClick(int position) {
        mUris.remove(position);
        if (mUris.size() < 5) showAddPhotoLayout();
        mPhotoAdapter.notifyDataSetChanged();
    }
}
