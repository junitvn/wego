package com.lamnn.wego.screen.login.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.LoginContract;
import com.lamnn.wego.screen.login.LoginPresenter;


public class PhoneLoginActivity extends AppCompatActivity implements View.OnClickListener, LoginContract.View {
    private EditText mTextPhoneNumber;
    private Button mButtonLoginWithPhoneNumber;
    private LoginPresenter mPresenter;
    private Toolbar mToolbar;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PhoneLoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        FirebaseApp.initializeApp(this);
        initView();
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.text_sign_in_with_phone));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                mPresenter.loginWithPhoneNumber(this, mTextPhoneNumber.getText().toString());
                break;
        }

    }


    private void initView() {
        mTextPhoneNumber = findViewById(R.id.text_phone_number);
        mButtonLoginWithPhoneNumber = findViewById(R.id.btn_next);
        mButtonLoginWithPhoneNumber.setOnClickListener(this);
        mPresenter = new LoginPresenter(this);
    }

    @Override
    public void onLoginSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
