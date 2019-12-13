package com.lamnn.wego.screen.profile.search_user;

import com.lamnn.wego.data.model.User;

import java.util.List;

public class SearchUserContract {
    interface View {
        void showFoundUser(List<User> users);

        void showLoading();

        void hideLoading();
    }

    interface Presenter {
        void search(String query);
    }
}
