package com.lamnn.wego.screen.login.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.LoginContract;
import com.lamnn.wego.screen.login.LoginPresenter;

public class VerifyLoginActivity extends AppCompatActivity implements View.OnClickListener, LoginContract.View {

    public static final String EXTRA_VERIFICATION_ID = "EXTRA_VERIFICATION_ID";
    public static final String EXTRA_PHONE_NUMBER = "EXTRA_PHONE_NUMBER";
    private Button mButtonNext;
    private TextView mTextPhoneNumber;
    private TextView mTextVerifyCode;
    private TextView mTextResendCode;
    private String mPhoneNumber;
    private String mVerificationId;
    private Toolbar mToolbar;
    private LoginPresenter mPresenter;

    public static Intent getIntent(Context context, String verificationId, String phoneNumber) {
        Intent intent = new Intent(context, VerifyLoginActivity.class);
        intent.putExtra(EXTRA_VERIFICATION_ID, verificationId);
        intent.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_login);
        getDataFromIntent();
        initView();
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Verify");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
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

    private void getDataFromIntent() {
        Intent intent = getIntent();
        mPhoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
        mVerificationId = intent.getStringExtra(EXTRA_VERIFICATION_ID);
    }

    private void initView() {
        mButtonNext = findViewById(R.id.btn_next);
        mButtonNext.setOnClickListener(this);
        mTextPhoneNumber = findViewById(R.id.text_phone_number);
        mTextPhoneNumber.setText(mPhoneNumber);
        mTextVerifyCode = findViewById(R.id.text_verify_code);
        mTextResendCode = findViewById(R.id.text_resend_code);
        mTextResendCode.setOnClickListener(this);
        mPresenter = new LoginPresenter(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                String code = mTextVerifyCode.getText().toString();
                mPresenter.verifyPhoneNumberWithCode(this, mVerificationId, code);
                break;
            case R.id.text_resend_code:

                break;
        }
    }

    @Override
    public void onLoginSuccess(String message) {

    }

    @Override
    public void onLoginFailure(String message) {

    }
}
