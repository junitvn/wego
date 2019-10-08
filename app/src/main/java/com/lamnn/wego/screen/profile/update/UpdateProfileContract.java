package com.lamnn.wego.screen.profile.update;

import android.app.Activity;

import com.lamnn.wego.data.model.User;

public class UpdateProfileContract {
    public interface View {
        void showError();

        void showSaveButton();

        void showLoading();

        void hideLoading();

        void hideSaveButton();

        void showUpdatedProfile(User user);
    }

    public interface Presenter {

        void choosePhoto(Activity activity);

        void takePhoto(Activity activity);

        void updateProfile(User user);
    }
}
