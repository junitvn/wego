package com.lamnn.wego.screen.profile.search_user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.screen.profile.detail.ProfileDetailActivity;

import java.util.List;

import static com.lamnn.wego.screen.info.info_member.InfoMemberActivity.EXTRA_USER;

public class SearchUserActivity extends AppCompatActivity implements View.OnClickListener, SearchUserContract.View, SearchUserAdapter.OnUserFoundItemClickListener, TextWatcher {

    private EditText mEditTextSearchUser;
    private ImageView mImageViewClearText;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mTextViewNotFound;
    private List<User> mUsers;
    private SearchUserAdapter mAdapter;
    private long mLastTextEdit = 0;
    private final long delay = 1000;
    private Handler mHandler;
    private SearchUserPresenter mPresenter;
    private User mUser;

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, SearchUserActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        initView();
        initToolbar();
        receiveData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_search_friend_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);
        mHandler = new Handler();
        mTextViewNotFound = findViewById(R.id.text_user_not_found);
        mPresenter = new SearchUserPresenter(this, this);
        mProgressBar = findViewById(R.id.progress_bar_loading);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.text_my_account));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        mEditTextSearchUser = toolbar.findViewById(R.id.edit_search_user);
        mEditTextSearchUser.addTextChangedListener(this);
        mImageViewClearText = toolbar.findViewById(R.id.image_clear_text);
        mImageViewClearText.setOnClickListener(this);
    }

    private void receiveData() {
        User user;
        if (getIntent().getExtras() != null) {
            user = getIntent().getExtras().getParcelable(EXTRA_USER);
            if (user != null) {
                mUser = user;
            }
        }
    }

    private Runnable mInputFinishChecker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (mLastTextEdit + delay - 500)) {
                mPresenter.search(mEditTextSearchUser.getText().toString());
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_clear_text:
                mEditTextSearchUser.setText("");
                mUsers.clear();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void showFoundUser(List<User> users) {
        mUsers = users;
        mAdapter = new SearchUserAdapter(this, mUsers, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        if (users.size() == 0) {
            mTextViewNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onUserFoundClick(User user) {
        startActivity(ProfileDetailActivity.getIntent(this, user));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        mTextViewNotFound.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mHandler.removeCallbacks(mInputFinishChecker);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            mLastTextEdit = System.currentTimeMillis();
            mHandler.postDelayed(mInputFinishChecker, delay);
        }
    }
}
