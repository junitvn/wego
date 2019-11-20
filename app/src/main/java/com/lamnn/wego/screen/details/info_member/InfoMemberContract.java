package com.lamnn.wego.screen.details.info_member;

import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.UserLocation;

import java.util.List;

public class InfoMemberContract {
    interface View {

        void updateUserLocation(UserLocation userLocation);

        void showEvents(List<Event> events);

        void hideLoading();

        void showLoading();

        void showUserStatus(UserLocation userLocation, String status, String type, Event event);

        void showMemberPopup(List<UserLocation> userLocations, String type);
    }

    interface Presenter {

        void getUserLocationData(String userId);

        void addComingMember(String myUserId, Event event);

        void addWaitingMember(String myUserId, Event event);

        void getListMember(List<String> users, String type);
    }
}
