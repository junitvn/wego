package com.lamnn.wego.screen.chat.group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.GroupChannel;

import java.util.List;

import static com.lamnn.wego.utils.Utils.convertSecondToDataTime;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context mContext;
    private List<GroupChannel> mGroupChannels;
    private OnItemGroupCLickListener mListener;
    private LayoutInflater mLayoutInflater;

    public GroupAdapter(Context context, List<GroupChannel> groupChannels, OnItemGroupCLickListener listener) {
        mContext = context;
        mGroupChannels = groupChannels;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_group, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(mGroupChannels.get(position));
    }

    @Override
    public int getItemCount() {
        return mGroupChannels == null ? 0 : mGroupChannels.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Context mContext;
        private TextView mTextViewName, mTextViewTime, mTextViewLastMessage;
        private ImageView mImageViewNewMessage, mImageViewGroupAvatar;
        private CheckBox mCheckBox;
        private OnItemGroupCLickListener mListener;
        private GroupChannel mGroupChannel;

        public ViewHolder(Context context, @NonNull View itemView, OnItemGroupCLickListener listener) {
            super(itemView);
            mContext = context;
            mListener = listener;
            mTextViewName = itemView.findViewById(R.id.text_item_group_name);
            mTextViewLastMessage = itemView.findViewById(R.id.text_item_group_last_message);
            mTextViewTime = itemView.findViewById(R.id.text_item_group_time);
            mImageViewNewMessage = itemView.findViewById(R.id.image_item_group_new_massage);
            mImageViewGroupAvatar = itemView.findViewById(R.id.image_item_group_avatar);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(mGroupChannel);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mListener != null) {
                mListener.onItemLongClick(mGroupChannel);
            }
            return false;
        }

        private void onBindData(GroupChannel groupChannel) {
            String topic = "GM" + groupChannel.getTripId();
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            if (groupChannel != null) {
                mGroupChannel = groupChannel;
                mTextViewName.setText(mGroupChannel.getName());
                if (mGroupChannel.getLastMessage() != null) {
                    String name = mGroupChannel.getLastMessage().getSender().getUid().equals(FirebaseAuth.getInstance().getUid())
                            ? mContext.getString(R.string.you)
                            : mGroupChannel.getLastMessage().getSender().getName();
                    String lastMessage = name + ": " + mGroupChannel.getLastMessage().getContent();
                    String displayMessage = lastMessage.length() < 30 ? lastMessage : lastMessage.substring(0, 29).concat("...");
                    mTextViewTime.setText(convertSecondToDataTime(Integer.parseInt(mGroupChannel.getLastMessage().getTimeStamp().getSeconds())));
                    mTextViewLastMessage.setText(displayMessage);
                }
            }
        }
    }

    public interface OnItemGroupCLickListener {
        void onItemClick(GroupChannel groupChannel);

        void onItemLongClick(GroupChannel groupChannel);

    }
}

