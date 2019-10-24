package com.lamnn.wego.screen.sign_up;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignUpPresenter implements SignUpContract.Presenter {
    private SignUpContract.View mView;
    private FirebaseAuth mAuth;
    private Context mContext;
    public static String TAG = "SignUpPresenter";

    public SignUpPresenter(Context context, SignUpContract.View view) {
        mContext = context;
        mView = view;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null) {
                                mView.onSignUpComplete();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Log.d(TAG, "onComplete: " + e);
                                mView.onSignUpFail("Password should be at least 6 characters");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.d(TAG, "onComplete: " + e);
                                mView.onSignUpFail("The email address is badly formatted");
                            } catch (Exception e) {
                                Log.d(TAG, "onComplete: " + e);
                                mView.onSignUpFail("Sign up failure. Check your connection");
                            }
                        }
                    }
                });
    }
}
