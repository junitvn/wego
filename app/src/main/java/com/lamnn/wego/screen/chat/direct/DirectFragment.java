package com.lamnn.wego.screen.chat.direct;

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
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.screen.conversation.ConversationActivity;

import java.util.List;

public class DirectFragment extends Fragment implements DirectAdapter.OnItemUserChannelCLickListener {

    public static final String BUNDLE_USER_CHANNELS = "BUNDLE_USER_CHANNELS";
    private RecyclerView mRecyclerView;
    private DirectAdapter mDirectAdapter;
    private List<UserChannel> mUserChannels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direct, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_chat);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        receiveData();
        return view;
    }

    private void receiveData() {
        if (getArguments() != null) {
            mUserChannels = getArguments().getParcelableArrayList(BUNDLE_USER_CHANNELS);
            mDirectAdapter = new DirectAdapter(getContext(), mUserChannels, this);
            mRecyclerView.setAdapter(mDirectAdapter);
            mDirectAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(UserChannel userChannel) {
        startActivity(ConversationActivity.getIntent(getContext(), userChannel));
    }

    @Override
    public void onItemLongClick(UserChannel groupChannel) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
