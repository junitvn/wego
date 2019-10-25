package com.lamnn.wego.screen.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.phone.PhoneLoginActivity;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.sign_up.SignUpActivity;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginContract.View {
    private ImageView mImagePhoneLogin;
    private ImageView mImageFacebookLogin;
    private LoginButton mLoginButton;
    private LoginContract.Presenter mPresenter;
    private Button mButtonSignIn;
    private EditText mEditTextUserName, mEditTextPassword;
    private TextView mTextViewSignUp;

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
        mPresenter = new LoginPresenter(this);
        mLoginButton = findViewById(R.id.login_button);
        mButtonSignIn = findViewById(R.id.btn_sign_in);
        mButtonSignIn.setOnClickListener(this);
        mLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        mPresenter.loginWithFacebook(this, mLoginButton);
        mTextViewSignUp = findViewById(R.id.text_sign_up);
        mTextViewSignUp.setOnClickListener(this);
        mEditTextUserName = findViewById(R.id.text_username);
        mEditTextPassword = findViewById(R.id.text_password);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mPresenter.handleActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                mPresenter.signIn(mEditTextUserName.getText().toString().trim(),
                        mEditTextPassword.getText().toString());
                break;
            case R.id.image_phone_login:
                startActivity(PhoneLoginActivity.getIntent(this));
                break;

            case R.id.image_facebook_login:
                mLoginButton.performClick();
                break;
            case R.id.text_sign_up:
                startActivity(SignUpActivity.getIntent(this));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    @Override
    public void onLoginSuccess(String message) {
        checkLogin();
    }

    @Override
    public void onLoginFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
