package com.lamnn.wego.screen.login;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.facebook.login.widget.LoginButton;

public class LoginContract {
    public interface View {
        void onLoginSuccess(String message);

        void onLoginFailure(String message);
    }

    public interface Presenter {
        void loginWithPhoneNumber(Activity activity, String phoneNumber);

        void verifyPhoneNumberWithCode(Activity activity, String verificationId, String code);

        void loginWithFacebook(Activity activity, LoginButton loginButton);

        void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data);

        void signIn(String email, String password);
    }
}
