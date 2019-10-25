package com.lamnn.wego.screen.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.screen.login.phone.VerifyLoginActivity;
import com.lamnn.wego.screen.map.MapsActivity;

import java.util.concurrent.TimeUnit;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private Activity mActivity;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mPhoneNumber;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private User mUser;

    public LoginPresenter(LoginContract.View view) {
        this.mView = view;
        mAuth = FirebaseAuth.getInstance();
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

    @Override
    public void loginWithFacebook(final Activity activity, LoginButton loginButton) {
        mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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
                Toast.makeText(activity, "mCallbackManager FB error" + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mView.onLoginSuccess("ok");
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthException e) {
                                switch (e.getErrorCode()) {
                                    case "ERROR_INVALID_EMAIL":
                                        mView.onLoginFailure(mActivity.getString(R.string.text_invalid_email));
                                        break;
                                    case "ERROR_USER_NOT_FOUND":
                                        mView.onLoginFailure(mActivity.getString(R.string.text_user_not_found));
                                        break;
                                    case "ERROR_WRONG_PASSWORD":
                                        mView.onLoginFailure(mActivity.getString(R.string.text_wrong_password));
                                        break;
                                    default:
                                        mView.onLoginFailure(mActivity.getString(R.string.text_login_fail));
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mView.onLoginFailure(mActivity.getString(R.string.text_login_fail));
                            }
                        }
                    }
                });
    }

    private void init() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(mActivity, "Phone number is invalid", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(mActivity, "SMS quota has been exceeded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "Check internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mActivity.startActivity(VerifyLoginActivity.getIntent(mActivity, verificationId, mPhoneNumber));
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getUser() != null) {
                                FirebaseUser fUser = task.getResult().getUser();
                                mUser = new User(fUser.getDisplayName(), fUser.getPhoneNumber(), fUser.getEmail(), fUser.getUid());
                                mActivity.startActivity(MapsActivity.getIntent(mActivity));
                            } else {
                                Toast.makeText(mActivity, "Login with phone number error. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(mActivity, "Invalid verify code", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mActivity, "Login failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser fUser = mAuth.getCurrentUser();
                            mUser = new User(fUser.getDisplayName(), fUser.getPhoneNumber(), fUser.getEmail(), fUser.getUid());
                            mActivity.startActivity(MapsActivity.getIntent(mActivity));
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(mActivity, "Authentication failed" + e,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
