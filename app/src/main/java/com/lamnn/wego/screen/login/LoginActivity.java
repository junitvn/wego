package com.lamnn.wego.screen.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lamnn.wego.R;
import com.lamnn.wego.screen.login.phone.PhoneLoginActivity;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.profile.update.ProfileUpdateActivity;

import java.util.Arrays;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mImagePhoneLogin;
    private ImageView mImageFacebookLogin;
    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private Context mContext;

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
        mContext = this;
        mImagePhoneLogin = findViewById(R.id.image_facebook_login);
        mImagePhoneLogin.setOnClickListener(this);
        mImageFacebookLogin = findViewById(R.id.image_phone_login);
        mImageFacebookLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                handleFacebookAccessToken(accessToken);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(ProfileUpdateActivity.getIntent(mContext));
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
