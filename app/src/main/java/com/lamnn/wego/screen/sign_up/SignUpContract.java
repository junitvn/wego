package com.lamnn.wego.screen.sign_up;

public interface SignUpContract {
    interface View {
        void onSignUpComplete();

        void onSignUpFail(String error);
    }

    interface Presenter {
        void signUp(String username, String password);
    }
}
