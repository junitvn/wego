package com.lamnn.wego.screen.info.info_user;

import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.UserLocation;

import java.util.List;

public interface InfoUserContract {
    interface View {
        void showLoading();

        void hideLoading();

        void showEvents(List<Event> events);

        void showUserStatus(UserLocation userLocation, String status, String type, Event event);

        void showMemberPopup(List<UserLocation> userLocations, String type);
    }

    interface Presenter {
        void updateStatus(Event event, String status);

        void createQuickEvent(UserLocation userLocation, String type);

        void getEventData(UserLocation userLocation);

        void getListMember(List<String> waitingUsers, String type);
    }
}
