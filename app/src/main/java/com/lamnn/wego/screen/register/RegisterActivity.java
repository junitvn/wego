package com.lamnn.wego.screen.register;

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

import com.lamnn.wego.R;
import com.lamnn.wego.screen.map.MapsActivity;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View, View.OnClickListener {

    private Toolbar mToolbar;
    private Button mButtonSignUp;
    private EditText mEditTextUserName, mEditTextPassword;
    private RegisterContract.Presenter mPresenter;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initToolbar();
        initView();
    }

    private void initView() {
        mEditTextPassword = findViewById(R.id.text_password);
        mEditTextUserName = findViewById(R.id.text_username);
        mButtonSignUp = findViewById(R.id.btn_sign_up);
        mButtonSignUp.setOnClickListener(this);
        mPresenter = new RegisterPresenter(this, this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
    }

    @Override
    public void onSignUpComplete() {
        startActivity(MapsActivity.getIntent(this));
    }

    @Override
    public void onSignUpFail(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                mPresenter.signUp(mEditTextUserName.getText().toString(),
                        mEditTextPassword.getText().toString());
                break;
        }
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
}
