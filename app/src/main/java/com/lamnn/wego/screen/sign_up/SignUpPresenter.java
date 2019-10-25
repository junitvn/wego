package com.lamnn.wego.screen.sign_up;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.lamnn.wego.R;

public class SignUpPresenter implements SignUpContract.Presenter {
    private SignUpContract.View mView;
    private FirebaseAuth mAuth;
    private Context mContext;

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
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                mView.onSignUpComplete();
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                mView.onSignUpFail(mContext.getString(R.string.text_message_password));
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                mView.onSignUpFail(mContext.getString(R.string.text_email_badly_formatted));
                            } catch (Exception e) {
                                mView.onSignUpFail(mContext.getString(R.string.text_sign_up_fail));
                            }
                        }
                    }
                });
    }
}
