package com.lamnn.wego.screen.register;

public interface RegisterContract {
    interface View {
        void onSignUpComplete();

        void onSignUpFail(String error);
    }

    interface Presenter {
        void signUp(String username, String password);
    }
}
