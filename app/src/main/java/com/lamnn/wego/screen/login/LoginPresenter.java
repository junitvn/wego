package com.lamnn.wego.screen.login;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lamnn.wego.screen.login.phone.VerifyLoginActivity;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.profile.detail.ProfileDetailActivity;
import com.lamnn.wego.screen.profile.update.ProfileUpdateActivity;

import java.util.concurrent.TimeUnit;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private Activity mActivity;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mPhoneNumber;

    public LoginPresenter(LoginContract.View view) {
        this.mView = view;
    }

    @Override
    public void loginWithPhoneNumber(Activity activity, String phoneNumber) {
        init();
        mPhoneNumber = phoneNumber;
        mActivity = activity;
        if (phoneNumber.isEmpty()) {
            mView.onLoginFailure("Please enter your phone number!");
        } else {
            FirebaseApp.initializeApp(activity);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    activity,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
        }
    }

    @Override
    public void verifyPhoneNumberWithCode(Activity activity, String verificationId, String code) {
        mActivity = activity;
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void init() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                //TODO handle exception
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mActivity.startActivity(VerifyLoginActivity.getIntent(mActivity, verificationId, mPhoneNumber));
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            mActivity.startActivity(ProfileUpdateActivity.getIntent(mActivity));
                        } else {

                        }
                    }
                });
    }

}
