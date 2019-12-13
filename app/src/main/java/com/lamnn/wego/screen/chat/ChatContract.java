package com.lamnn.wego.screen.chat;

import com.lamnn.wego.data.model.GroupChannel;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;

import java.util.ArrayList;

public class ChatContract {
    interface View {
        void showGroups(ArrayList<GroupChannel> groupChannels);

        void showUserChannels(ArrayList<UserChannel> userChannels);

        void showFriends(ArrayList<User> users);
    }

    interface Presenter {
        void getGroupData(User user);

        void getData(User user);
    }
}
