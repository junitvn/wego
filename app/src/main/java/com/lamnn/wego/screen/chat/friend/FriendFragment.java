package com.lamnn.wego.screen.chat.friend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.screen.chat.direct.DirectAdapter;
import com.lamnn.wego.screen.conversation.ConversationActivity;
import com.lamnn.wego.screen.profile.detail.ProfileDetailActivity;

import java.util.List;

import static com.lamnn.wego.screen.chat.ChatActivity.BUNDLE_FRIENDS;

public class FriendFragment extends Fragment implements FriendAdapter.OnFriendItemClickListener {
    private RecyclerView mRecyclerView;
    private FriendAdapter mFriendAdapter;
    private List<User> mFriends;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_friend);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        receiveData();
        return view;
    }

    private void receiveData() {
        if (getArguments() != null) {
            mFriends = getArguments().getParcelableArrayList(BUNDLE_FRIENDS);
            mFriendAdapter = new FriendAdapter(getContext(), mFriends, this);
            mRecyclerView.setAdapter(mFriendAdapter);
            mFriendAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFriendItemClick(User user) {
        startActivity(ProfileDetailActivity.getIntent(getContext(), user));
    }

    @Override
    public void onFriendItemCallClick(User user) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + user.getPhone()));
        startActivity(intent);
    }

    @Override
    public void onFriendItemMessageCLick(User user) {
        startActivity(ConversationActivity.getIntent(getContext(), user));
    }
}
