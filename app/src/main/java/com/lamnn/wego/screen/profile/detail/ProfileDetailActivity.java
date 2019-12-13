package com.lamnn.wego.screen.profile.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.screen.profile.update.ProfileUpdateActivity;
import com.lamnn.wego.screen.profile.update.UpdateProfilePresenter;
import com.lamnn.wego.utils.GlideApp;

import static com.lamnn.wego.screen.trip.join_trip.JoinTripActivity.EXTRA_USER;


public class ProfileDetailActivity extends AppCompatActivity implements View.OnClickListener, DetailContract.View {

    private TextView mTextViewName, mTextViewPhoneNumber;
    private ImageView mImageViewAvatar;
    private Button mButtonAddFriend;
    private User mUser;
    private DetailPresenter mPresenter;
    private Boolean mIsFriend;

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, ProfileDetailActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        receiveData();
        initToolbar();
        initView();
        showUserData();
        mPresenter.getUserData(mUser.getUid());
    }

    private void receiveData() {
        mUser = new User();
        mUser = getIntent().getExtras().getParcelable(EXTRA_USER);
    }

    private void showUserData() {
        if (mUser == null) return;
        mTextViewName.setText(mUser.getName());
        mTextViewPhoneNumber.setText(mUser.getPhone());
        if (mUser.getPhotoUri() != null) {
            GlideApp.with(getApplicationContext())
                    .load(mUser.getPhotoUri())
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.ic_user)
                    .into(mImageViewAvatar);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.text_my_account));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_left_arrow);
    }

    private void initView() {
        mImageViewAvatar = findViewById(R.id.image_avatar);
        mImageViewAvatar.setOnClickListener(this);
        mTextViewName = findViewById(R.id.text_name_user);
        mTextViewPhoneNumber = findViewById(R.id.text_phone_number);
        mButtonAddFriend = findViewById(R.id.button_add_friend);
        mButtonAddFriend.setOnClickListener(this);
        mPresenter = new DetailPresenter(this, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_friend:
                if (mIsFriend) {
                    mPresenter.removeFriend(mUser.getUid());
                } else
                    mPresenter.addFriend(mUser.getUid());
                break;
        }
    }

    @Override
    public void updateRelationship(Boolean isFriend) {
        mIsFriend = isFriend;
        if (isFriend) {
            mButtonAddFriend.setText(getString(R.string.unfriend));
            mButtonAddFriend.setTextColor(getResources().getColor(R.color.colorPrimary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mButtonAddFriend.setBackground(getDrawable(R.drawable.button_unfriend));
            }
        } else {
            mButtonAddFriend.setTextColor(getResources().getColor(R.color.colorTextHint));
            mButtonAddFriend.setText(getString(R.string.add_friend));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mButtonAddFriend.setBackground(getDrawable(R.drawable.button_primary));
            }
        }
    }
}
