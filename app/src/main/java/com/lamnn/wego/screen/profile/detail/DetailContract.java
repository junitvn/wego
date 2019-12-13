package com.lamnn.wego.screen.profile.detail;

public class DetailContract {
    interface View {
        void updateRelationship(Boolean isFriend);
    }

    interface Presenter {
        void getUserData(String friendId);

        void addFriend(String uid);

        void removeFriend(String uid);
    }
}
