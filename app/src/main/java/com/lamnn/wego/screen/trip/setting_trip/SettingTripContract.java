package com.lamnn.wego.screen.trip.setting_trip;

import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.UserLocation;

public class SettingTripContract {
    interface View {
        void showLoading();

        void hideLoading();

        void updateUserLocation(UserLocation userLocation);
    }

    interface Presenter {
        void outTrip(UserLocation userLocation);

        void updateTrip(Trip trip);

        void showLoading();

        void hideLoading();

        void updateUserLocation(UserLocation userLocation);

        void copyIdTripToClipboard(String code);
    }
}
