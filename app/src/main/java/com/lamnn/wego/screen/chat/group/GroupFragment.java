package com.lamnn.wego.screen.chat.group;

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
import com.lamnn.wego.data.model.GroupChannel;
import com.lamnn.wego.screen.conversation.ConversationActivity;

import java.util.List;

public class GroupFragment extends Fragment implements GroupAdapter.OnItemGroupCLickListener {

    public static final String BUNDLE_GROUPS = "BUNDLE_GROUPS";
    private RecyclerView mRecyclerView;
    private GroupAdapter mGroupAdapter;
    private List<GroupChannel> mGroupChannels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_chat);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        receiveData();
        return view;
    }

    private void receiveData() {
        if (getArguments() != null) {
            mGroupChannels = getArguments().getParcelableArrayList(BUNDLE_GROUPS);
            mGroupAdapter = new GroupAdapter(getContext(), mGroupChannels, this);
            mRecyclerView.setAdapter(mGroupAdapter);
            mGroupAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onItemClick(GroupChannel groupChannel) {
        startActivity(ConversationActivity.getIntent(getContext(), groupChannel));
    }

    @Override
    public void onItemLongClick(GroupChannel groupChannel) {

    }

}
