package com.lamnn.wego.screen.trip.create_trip;

import com.lamnn.wego.data.model.Trip;

public class CreateTripContract {
    interface View {
        void showLoading();

        void hideLoading();

    }

    interface Presenter {
        void createTrip(Trip trip);

        void showLoading();

        void hideLoading();
    }
}
