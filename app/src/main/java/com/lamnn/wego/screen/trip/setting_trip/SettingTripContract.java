package com.lamnn.wego.screen.trip.setting_trip;

import com.lamnn.wego.data.model.UserLocation;

public class SettingTripContract {
    interface View {
        void showLoading();

        void hideLoading();

        void updateUserLocation(UserLocation userLocation);
    }

    interface Presenter {
        void outTrip();

        void updateTrip();

        void showLoading();

        void hideLoading();

        void updateUserLocation(UserLocation userLocation);
    }
}
