package com.lamnn.wego.screen.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.phone.PhoneLoginActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImagePhoneLogin;
    private ImageView mImageFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();


    }

    private void initView() {
        mImagePhoneLogin = findViewById(R.id.image_facebook_login);
        mImagePhoneLogin.setOnClickListener(this);
        mImageFacebookLogin = findViewById(R.id.image_phone_login);
        mImageFacebookLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_phone_login:
                startActivity(PhoneLoginActivity.getIntent(this));
                break;

            case R.id.image_facebook_login:
                break;
        }
    }
}
