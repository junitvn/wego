package com.lamnn.wego.screen.map;

import com.google.android.gms.maps.GoogleMap;
import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.data.model.route.RouteResponse;

import java.util.List;

public class MapsContract {
    public interface View {
        void showUserData(User user);

        void showTrips(List<Trip> trips);

        void showActiveTrip(Trip trip);

        void showLoading();

        void hideLoading();

        void navigateToUpdateProfile(User user);

        void showErrorMessage(String message);

        void showListUserCircle(List<UserLocation> userLocations);

        void drawPoly(RouteResponse routeResponse);
    }

    public interface Presenter {
        void getUserData();

        void getTrips();

        void getActiveTrip(String code);

        void getListMember(String code, Boolean isExistMarker);

        void updateStatus(String status);

        void switchTrip(String activeTrip);

        void initMarker(List<UserLocation> userLocations, GoogleMap map);

        void initUserLocation(UserLocation userLocation);

        void showUserItemCircle(UserLocation userLocation);

        void updateMarker(List<UserLocation> userLocations);

        void showAllMember();

        void showUserLocation(UserLocation userLocation);

        void getDirection(Trip trip);

        void initSpecialMarker(Trip trip);
    }
}
