package com.lamnn.wego.screen.trip.create_trip.share_code;

import com.lamnn.wego.data.model.Trip;
import com.lamnn.wego.data.model.User;

import java.util.ArrayList;

public class ShareCodeContract {
    interface View {
        void showUserFriend(ArrayList<User> users);
    }

    interface Presenter {
        void getUserFriends();

        void copyIdTripToClipboard(String code);

        void onInviteStatusClick(User user, Trip trip);
    }
}
