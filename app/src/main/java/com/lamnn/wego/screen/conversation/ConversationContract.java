package com.lamnn.wego.screen.conversation;

import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.data.model.UserMessage;

import java.util.List;

public class ConversationContract {
    interface View {
        void showConversation(List<GroupMessage> groupMessages);

        void sendMessageSuccess();

        void sendMessageFail();

        void updateUserChannel(UserChannel userChannel);

        void showUserConversation(List<UserMessage> userMessages);
    }

    interface Presenter {
        void getConversationGroupData(String groupId);

        void sendGroupMessage(GroupMessage groupMessage);

        void sendUserMessage(UserMessage userMessage);

        void createUserChannel(UserChannel userChannel);

        void getConversationUserData(String channelId);
    }
}
