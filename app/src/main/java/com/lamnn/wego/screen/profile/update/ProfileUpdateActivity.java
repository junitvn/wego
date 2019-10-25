package com.lamnn.wego.screen.profile.update;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.request.RequestOptions;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.utils.GlideApp;

import java.io.File;

import static com.lamnn.wego.screen.join_trip.JoinTripActivity.EXTRA_USER;

public class ProfileUpdateActivity extends AppCompatActivity implements UpdateProfileContract.View, View.OnClickListener {
    public static final int TAKE_PHOTO_REQUEST_CODE = 100;
    public static final int GALLERY_REQUEST_CODE = 200;
    private ImageView mImageAvatar;
    private Button mButtonSave;
    private EditText mTextPhone;
    private EditText mTextName;
    private AlertDialog mDialog;
    private UpdateProfileContract.Presenter mPresenter;
    private Activity mActivity;
    private User mUser;
    private ProgressBar mProgressBar;

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, ProfileUpdateActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        receiveData();
        initToolbar();
        initView();
        initDialog();
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

    private void receiveData() {
        mUser = new User();
        mUser = getIntent().getExtras().getParcelable(EXTRA_USER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST_CODE:
                    File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.file_name_image));
                    Uri uri = FileProvider.getUriForFile(getApplicationContext(), this.getApplicationContext().getPackageName() + ".provider", file);
                    mUser.setPhotoUri(uri.toString());
                    mImageAvatar.setImageURI(uri);
                    mButtonSave.setVisibility(View.VISIBLE);
                    break;
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = null;
                    if (data != null) {
                        selectedImage = data.getData();
                    }
                    if (selectedImage != null) {
                        mUser.setPhotoUri(selectedImage.toString());
                    }
                    mImageAvatar.setImageURI(selectedImage);
                    mButtonSave.setVisibility(View.VISIBLE);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.text_my_account));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mButtonSave = toolbar.findViewById(R.id.btn_save_profile);
        mButtonSave.setOnClickListener(this);
    }

    private void initView() {
        mActivity = this;
        mImageAvatar = findViewById(R.id.image_avatar);
        mImageAvatar.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress_bar_loading);
        mTextName = findViewById(R.id.text_name_trip);
        mTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mButtonSave.setVisibility(View.VISIBLE);
            }
        });
        mTextPhone = findViewById(R.id.text_id_trip);
        mPresenter = new UpdateProfilePresenter(getApplicationContext(), this);
        showProfile(mUser);
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    public void showProfile(User user) {
        if (user == null) return;
        mTextName.setText(user.getName() == null ? getString(R.string.text_null) : user.getName());
        mTextPhone.setText(user.getPhone() == null ? getString(R.string.text_null) : user.getPhone());
        if (user.getPhotoUri() != null) {
            GlideApp.with(this)
                    .load(user.getPhotoUri())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageAvatar);
        }
    }

    @Override
    public void showError() {

    }

    @Override
    public void showSaveButton() {
        mButtonSave.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSaveButton() {
        mButtonSave.setVisibility(View.GONE);
    }

    @Override
    public void showUpdatedProfile(User user) {
        mUser = user;
        showProfile(user);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_avatar:
                mDialog.show();
                break;
            case R.id.btn_save_profile:
                mUser.setName(mTextName.getText().toString());
                mUser.setPhone(mTextPhone.getText().toString());
                mPresenter.updateProfile(mUser);
                showLoading();
                break;
        }
    }
}
