package com.lamnn.wego.screen.login;

import android.app.Activity;

public class LoginInterator implements LoginContract.Intractor{
    private LoginContract.onLoginListener mOnLoginListener;

    public LoginInterator(LoginContract.onLoginListener onLoginListener) {
        this.mOnLoginListener = onLoginListener;
    }


    @Override
    public void performLoginWithPhoneNumber(Activity activity, String phoneNumber) {

    }
}
