package com.lamnn.wego.screen.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.lamnn.wego.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.lamnn.wego.screen.login.phone.PhoneLoginActivity;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnLoginWithPhone;
    private Button mBtnLoginWithFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mBtnLoginWithFacebook = findViewById(R.id.loginWithFacebook);
        mBtnLoginWithFacebook.setOnClickListener(this);
        mBtnLoginWithPhone = findViewById(R.id.loginWithPhone);
        mBtnLoginWithPhone.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginWithPhone:
                startActivity(PhoneLoginActivity.getIntent(this));
                break;

            case R.id.loginWithFacebook:
                break;
        }
    }
}
