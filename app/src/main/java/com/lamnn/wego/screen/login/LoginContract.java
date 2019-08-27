package com.lamnn.wego.screen.login;

import android.app.Activity;

public class LoginContract {
    public interface View {
        void onLoginSuccess(String message);

        void onLoginFailure(String message);
    }

    public interface Presenter {
        void loginWithPhoneNumber(Activity activity, String phoneNumber);

        void verifyPhoneNumberWithCode(Activity activity, String verificationId, String code);
    }
}
