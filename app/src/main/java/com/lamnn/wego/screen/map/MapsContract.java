package com.lamnn.wego.screen.map;

import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;

import java.util.List;

public class MapsContract {
    public interface View{
        void showUserData(User user);
        void showTrips(List<Trip> trips);
        void showActiveTrip(Trip trip);
        void initMarkers(List<User> users);
        void updateMarkers(List<User> users);
        void showLoading();
        void hideLoading();
    }
    public interface Presenter{
        void getUserData(Boolean isUpdateTrip);
        void getTrips();
        void getActiveTrip(String code);
        void getListMember(String code, Boolean isExistMarker);
        void updateStatus(String status);
        void switchTrip(String activeTrip);
    }
}
