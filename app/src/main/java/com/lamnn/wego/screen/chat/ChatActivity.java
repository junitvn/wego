package com.lamnn.wego.screen.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.GroupChannel;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.screen.chat.friend.FriendFragment;
import com.lamnn.wego.screen.chat.group.GroupFragment;
import com.lamnn.wego.screen.chat.direct.DirectFragment;
import com.lamnn.wego.screen.login.LoginActivity;
import com.lamnn.wego.screen.map.MapsActivity;
import com.lamnn.wego.screen.profile.search_user.SearchUserActivity;
import com.lamnn.wego.screen.profile.update.ProfileUpdateActivity;
import com.lamnn.wego.utils.GlideApp;

import java.util.ArrayList;

import static com.lamnn.wego.screen.chat.group.GroupFragment.BUNDLE_GROUPS;
import static com.lamnn.wego.screen.info.info_member.InfoMemberActivity.EXTRA_USER;

public class ChatActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        ChatContract.View, SwipeRefreshLayout.OnRefreshListener {
    public static final String BUNDLE_USER_CHANNELS = "BUNDLE_USER_CHANNELS";
    public static final String BUNDLE_FRIENDS = "BUNDLE_FRIENDS";
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressBar mProgressBar;
    private ImageView mImageAvatar, mImageViewAddFriend;
    private TextView mTextWelcome;
    private User mUser;
    private ArrayList<GroupChannel> mGroupChannels;
    private ArrayList<UserChannel> mUserChannels;
    private ChatContract.Presenter mPresenter;
    private Fragment mFragment;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mPullToRefresh;
    private ArrayList<User> mFriends;

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        receiveData();
        initToolbar();
        mPresenter = new ChatPresenter(this, this);
        mPresenter.getData(mUser);
        showDirectFragment();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_message:
                showDirectFragment();
                break;
            case R.id.nav_group:
                mToolbar.setTitle(getString(R.string.group_label));
                mFragment = new GroupFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment, "GROUP").commit();
                updateGroupFragmentData();
                break;
            case R.id.nav_friend:
                mToolbar.setTitle(getString(R.string.friend_label));
                mFragment = new FriendFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment, "FRIEND").commit();
                updateFriendFragmentData();
                break;
            case R.id.nav_map:
                startActivity(MapsActivity.getIntent(this));
                break;
            case R.id.nav_people:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mNavigationView.setCheckedItem(R.id.nav_people);
                break;
            case R.id.nav_sign_out:
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.getIntent(this));
                break;
        }
        return true;
    }

    private void showDirectFragment() {
        mToolbar.setTitle(getString(R.string.message_label));
        mFragment = new DirectFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment, "MESSAGE").commit();
        updateUserChannelFragmentData();
    }

    private void updateGroupFragmentData() {
        Bundle bundle = new Bundle();
        if (mGroupChannels != null) {
            bundle.putParcelableArrayList(BUNDLE_GROUPS, mGroupChannels);
        }
        if (mFragment != null) {
            if (mFragment.getArguments() == null) {
                mFragment.setArguments(bundle);
            } else {
                mFragment.getArguments().putParcelableArrayList(BUNDLE_GROUPS, mGroupChannels);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(mFragment);
            fragmentTransaction.attach(mFragment);
            fragmentTransaction.commit();
        }
    }

    private void updateUserChannelFragmentData() {
        Bundle bundle = new Bundle();
        if (mUserChannels != null) {
            bundle.putParcelableArrayList(BUNDLE_USER_CHANNELS, mUserChannels);
        }
        if (mFragment != null) {
            if (mFragment.getArguments() == null) {
                mFragment.setArguments(bundle);
            } else {
                mFragment.getArguments().putParcelableArrayList(BUNDLE_USER_CHANNELS, mUserChannels);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(mFragment);
            fragmentTransaction.attach(mFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    private void updateFriendFragmentData() {
        Bundle bundle = new Bundle();
        if (mFriends != null) {
            bundle.putParcelableArrayList(BUNDLE_FRIENDS, mFriends);
        }
        if (mFragment != null) {
            if (mFragment.getArguments() == null) {
                mFragment.setArguments(bundle);
            } else {
                mFragment.getArguments().putParcelableArrayList(BUNDLE_FRIENDS, mFriends);
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(mFragment);
            fragmentTransaction.attach(mFragment);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_avatar:
            case R.id.text_nav_welcome:
                startActivity(ProfileUpdateActivity.getIntent(this, mUser));
                break;
            case R.id.image_add_friend:
                startActivity(SearchUserActivity.getIntent(this, mUser));
                break;
        }
    }

    private void initView() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mDrawerLayout = findViewById(R.id.drawer_layout_chat);
        mNavigationView = findViewById(R.id.navigation_chat);
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        mImageAvatar = headerView.findViewById(R.id.image_avatar);
        mImageAvatar.setOnClickListener(this);
        mTextWelcome = headerView.findViewById(R.id.text_nav_welcome);
        mTextWelcome.setOnClickListener(this);
        mPullToRefresh = findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setOnRefreshListener(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        mImageViewAddFriend = mToolbar.findViewById(R.id.image_add_friend);
        mImageViewAddFriend.setOnClickListener(this);
    }

    private void receiveData() {
        User user;
        if (getIntent().getExtras() != null) {
            user = getIntent().getExtras().getParcelable(EXTRA_USER);
            if (user != null) {
                mUser = user;
                showUserData(user);
            }
        }
    }

    private void showUserData(User user) {
        mTextWelcome.setText(user.getName());
        if (user.getPhotoUri() != null) {
            GlideApp.with(getApplicationContext())
                    .load(user.getPhotoUri())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageAvatar);
        }
    }

    @Override
    public void showGroups(ArrayList<GroupChannel> groupChannels) {
        mGroupChannels = groupChannels;
        updateGroupFragmentData();
        mPullToRefresh.setRefreshing(false);
    }

    @Override
    public void showUserChannels(ArrayList<UserChannel> userChannels) {
        mUserChannels = userChannels;
        updateUserChannelFragmentData();
        mPullToRefresh.setRefreshing(false);
    }

    @Override
    public void showFriends(ArrayList<User> users) {
        mFriends = users;
        updateFriendFragmentData();
    }

    @Override
    public void onRefresh() {
        if (mUser != null) {
            mPresenter.getData(mUser);
        } else {
            mPullToRefresh.setRefreshing(false);
        }

    }
}
