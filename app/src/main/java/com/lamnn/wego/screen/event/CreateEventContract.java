package com.lamnn.wego.screen.event;

import android.app.Activity;

import com.lamnn.wego.data.model.Event;

public interface CreateEventContract {
    interface View {

        void showLoading();

        void hideLoading();

        void hideAddPhotoLayout();

        void showAddPhotoLayout();
    }

    interface Presenter {
        void choosePhoto(Activity activity);

        void takePhoto(Activity activity);

        void showAddPhotoLayout();

        void hideAddPhotoLayout();

        void createEvent(Event event);

        void showLoading();

        void hideLoading();

        void updateEvent(Event event);
    }
}
