package com.lamnn.wego.screen.profile.search_user;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.remote.UserService;
import com.lamnn.wego.utils.APIUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchUserPresenter implements SearchUserContract.Presenter {
    private Context mContext;
    private SearchUserContract.View mView;
    private UserService mUserService;

    public SearchUserPresenter(Context context, SearchUserContract.View view) {
        mContext = context;
        mView = view;
        mUserService = APIUtils.getUserService();
    }

    @Override
    public void search(String query) {
        mView.showLoading();
        User user = new User();
        user.setUid(FirebaseAuth.getInstance().getUid());
        user.setName(query);
        mUserService.searchUserByName(user).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.body() != null) {
                    mView.showFoundUser(response.body());
                    mView.hideLoading();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.check_your_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
