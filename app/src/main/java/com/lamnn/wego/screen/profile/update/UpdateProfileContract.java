package com.lamnn.wego.screen.profile.update;

public class UpdateProfileContract {
    public interface View {
        void showProfile();

        void showError();
    }

    public interface Presenter {
        void changeName(String name);

        void changePhoneNumber(String phoneNumber);

        void changeAvatar();
    }
}
