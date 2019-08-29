package com.lamnn.wego.screen.profile.update;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.phone.PhoneLoginActivity;

public class ProfileUpdateActivity extends AppCompatActivity implements UpdateProfileContract.View {
    private Toolbar mToolbar;
    private ImageView mImageAvatar;
    private EditText mTextPhone;
    private EditText mTextName;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ProfileUpdateActivity .class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        initView();
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My account");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
    }

    private void initView() {
        mImageAvatar = findViewById(R.id.image_avatar);
        mTextName = findViewById(R.id.text_name);
        mTextPhone = findViewById(R.id.text_phone_profile);
    }

    @Override
    public void showProfile() {

    }

    @Override
    public void showError() {

    }
}
