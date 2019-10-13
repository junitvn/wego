package com.lamnn.wego.screen.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.phone.PhoneLoginActivity;
import com.lamnn.wego.screen.map.MapsActivity;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mImagePhoneLogin;
    private ImageView mImageFacebookLogin;
    private LoginButton mLoginButton;
    private LoginContract.Presenter mPresenter;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkLogin();
        initView();
    }

    private void checkLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(MapsActivity.getIntent(this));
        }
    }

    private void initView() {
        mImagePhoneLogin = findViewById(R.id.image_facebook_login);
        mImagePhoneLogin.setOnClickListener(this);
        mImageFacebookLogin = findViewById(R.id.image_phone_login);
        mImageFacebookLogin.setOnClickListener(this);
        mPresenter = new LoginPresenter();
        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        mPresenter.loginWithFacebook(this, mLoginButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mPresenter.handleActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_phone_login:
                startActivity(PhoneLoginActivity.getIntent(this));
                break;

            case R.id.image_facebook_login:
                mLoginButton.performClick();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }
}
