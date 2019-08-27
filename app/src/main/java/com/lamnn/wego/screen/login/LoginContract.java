package com.lamnn.wego.screen.login;

import android.app.Activity;

public class LoginContract {
    interface View {
        void onLoginSuccess(String message);

        void onLoginFailure(String message);
    }

    interface Presenter {
        void loginWithPhoneNumber(Activity activity, String phoneNumber);
    }

    interface Intractor {
        void performLoginWithPhoneNumber(Activity activity, String phoneNumber);
    }

    interface onLoginListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
