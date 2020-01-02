package com.lamnn.wego.screen.conversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.GroupChannel;
import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.data.model.UserMessage;

import java.util.ArrayList;
import java.util.List;

import static com.lamnn.wego.screen.info.info_member.InfoMemberActivity.EXTRA_USER;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener, ConversationContract.View {
    public static final String EXTRA_GROUP = "EXTRA_GROUP";
    public static final String EXTRA_USER_CHANNEL = "EXTRA_USER_CHANNEL";
    private RecyclerView mRecyclerViewConversation;
    private EditText mEditTextContent;
    private ImageView mImageViewSend;
    private ConversationContract.Presenter mPresenter;
    private GroupChannel mGroupChannel;
    private UserChannel mUserChannel;
    private GroupMessage mGroupMessage;
    private User mUserPartner;
    private User mSender;
    private UserMessage mUserMessage;
    private ConversationAdapter mConversationAdapter;
    private ConversationUserAdapter mConversationUserAdapter;
    private Toolbar mToolbar;
    private Boolean mIsGroupChat;

    public static Intent getIntent(Context context, GroupChannel groupChannel) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_GROUP, groupChannel);
        return intent;
    }

    public static Intent getIntent(Context context, UserChannel userChannel) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_USER_CHANNEL, userChannel);
        return intent;
    }

    public static Intent getIntent(Context context, User user) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_message);
        mPresenter = new ConversationPresenter(this, this);
        initView();
        initSender();
        receiveData();
        initToolbar();
        initMessage();
        if (mIsGroupChat) {
            mPresenter.getConversationGroupData(mGroupChannel.getTripId());
        } else {
            if (mUserChannel != null) {
                mPresenter.getConversationUserData(mUserChannel.getChannelId());
            }
        }
    }

    private void initSender() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mSender = new User();
        mSender.setUid(auth.getUid());
        if (auth.getCurrentUser().getDisplayName() != null) {
            mSender.setName(auth.getCurrentUser().getDisplayName());
        }
        if (auth.getCurrentUser().getPhotoUrl() != null) {
            mSender.setPhotoUri(auth.getCurrentUser().getPhotoUrl().toString());
        }
        if (auth.getCurrentUser().getPhoneNumber() != null) {
            mSender.setPhone(auth.getCurrentUser().getPhoneNumber());
        }
    }

    private void initMessage() {
        if (mIsGroupChat) {
            mGroupMessage = new GroupMessage();
            mGroupMessage.setGroupId(mGroupChannel.getTripId());
            mGroupMessage.setSender(mSender);
        } else {
            mUserMessage = new UserMessage();
            if (mUserChannel != null) {
                mUserMessage.setChannelId(mUserChannel.getChannelId());
            }
            mUserMessage.setSender(mSender);
        }
    }

    private void initView() {
        mRecyclerViewConversation = findViewById(R.id.recycler_conversation);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerViewConversation.setLayoutManager(linearLayoutManager);
        mRecyclerViewConversation.setHasFixedSize(true);
        mEditTextContent = findViewById(R.id.text_content);
        mImageViewSend = findViewById(R.id.image_send_message);
        mImageViewSend.setOnClickListener(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_left_arrow);
        if (mGroupChannel != null) {
            getSupportActionBar().setTitle(mGroupChannel.getName());
        }
        if (mUserPartner != null) {
            getSupportActionBar().setTitle(mUserPartner.getName());
        }
        if (mUserChannel != null) {
            String partnerName = "";
            for (User user : mUserChannel.getMembers()) {
                if (!user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                    partnerName = user.getName();
                }
            }
            getSupportActionBar().setTitle(partnerName);
        }
    }

    private void receiveData() {
        Intent intent = getIntent();
        mGroupChannel = new GroupChannel();
        mUserChannel = new UserChannel();
        if (intent.getExtras() != null) {
            mGroupChannel = intent.getExtras().getParcelable(EXTRA_GROUP);
            mUserChannel = intent.getExtras().getParcelable(EXTRA_USER_CHANNEL);
            mUserPartner = intent.getExtras().getParcelable(EXTRA_USER);
            if (mGroupChannel != null) {
                mIsGroupChat = true;
            } else {
                mIsGroupChat = false;
            }
            if (mUserPartner != null) {
                mUserChannel = new UserChannel();
                List<String> memberUids = new ArrayList<>();
                memberUids.add(mSender.getUid());
                memberUids.add(mUserPartner.getUid());
                mUserChannel.setMemberUid(memberUids);
                List<User> members = new ArrayList<>();
                members.add(mSender);
                members.add(mUserPartner);
                mUserChannel.setMembers(members);
                mPresenter.createUserChannel(mUserChannel);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_send_message:
                if (mIsGroupChat) {
                    mGroupMessage.setContent(mEditTextContent.getText().toString());
                    mGroupMessage.setGroupName(mGroupChannel.getName());
                    mPresenter.sendGroupMessage(mGroupMessage);
                } else {
                    mUserMessage.setChannelId(mUserChannel.getChannelId());
                    mUserMessage.setContent(mEditTextContent.getText().toString());
                    mPresenter.sendUserMessage(mUserMessage);
                }
                mEditTextContent.setText("");
                break;
        }
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

    @Override
    public void showConversation(List<GroupMessage> groupMessages) {
        mConversationAdapter = new ConversationAdapter(this, groupMessages);
        mRecyclerViewConversation.setAdapter(mConversationAdapter);
        mConversationAdapter.notifyDataSetChanged();
        if (groupMessages.size() > 0)
            mRecyclerViewConversation.smoothScrollToPosition(groupMessages.size() - 1);
    }

    @Override
    public void sendMessageSuccess() {

    }

    @Override
    public void sendMessageFail() {
        Toast.makeText(this, getString(R.string.sent_message_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateUserChannel(UserChannel userChannel) {
        mUserChannel = userChannel;
        String topic = "UM" + userChannel.getChannelId();
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        mPresenter.getConversationUserData(userChannel.getChannelId());
    }

    @Override
    public void showUserConversation(List<UserMessage> userMessages) {
        mConversationUserAdapter = new ConversationUserAdapter(this, userMessages);
        mRecyclerViewConversation.setAdapter(mConversationUserAdapter);
        mConversationUserAdapter.notifyDataSetChanged();
        if (userMessages.size() > 0)
            mRecyclerViewConversation.smoothScrollToPosition(userMessages.size() - 1);
    }
}
