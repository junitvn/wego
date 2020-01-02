package com.lamnn.wego.screen.chat.direct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserChannel;
import com.lamnn.wego.utils.GlideApp;

import java.util.List;

import static com.lamnn.wego.utils.Utils.convertSecondToDataTime;

public class DirectAdapter extends RecyclerView.Adapter<DirectAdapter.ViewHolder> {
    private Context mContext;
    private List<UserChannel> mUserChannels;
    private OnItemUserChannelCLickListener mListener;
    private LayoutInflater mLayoutInflater;

    public DirectAdapter(Context context, List<UserChannel> userChannels, OnItemUserChannelCLickListener listener) {
        mContext = context;
        mUserChannels = userChannels;
        mListener = listener;
    }

    @NonNull
    @Override
    public DirectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_group, parent, false);
        return new DirectAdapter.ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectAdapter.ViewHolder holder, int position) {
        holder.onBindData(mUserChannels.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserChannels == null ? 0 : mUserChannels.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Context mContext;
        private TextView mTextViewName, mTextViewTime, mTextViewLastMessage;
        private ImageView mImageViewNewMessage, mImageViewGroupAvatar, mImageViewDot;
        private CheckBox mCheckBox;
        private OnItemUserChannelCLickListener mListener;
        private UserChannel mUserChannel;

        public ViewHolder(Context context, @NonNull View itemView, OnItemUserChannelCLickListener listener) {
            super(itemView);
            mContext = context;
            mListener = listener;
            mTextViewName = itemView.findViewById(R.id.text_item_group_name);
            mTextViewLastMessage = itemView.findViewById(R.id.text_item_group_last_message);
            mTextViewTime = itemView.findViewById(R.id.text_item_group_time);
            mImageViewNewMessage = itemView.findViewById(R.id.image_item_group_new_massage);
            mImageViewGroupAvatar = itemView.findViewById(R.id.image_item_group_avatar);
            mImageViewDot = itemView.findViewById(R.id.image_dot);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(mUserChannel);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mListener != null) {
                mListener.onItemLongClick(mUserChannel);
            }
            return false;
        }

        private void onBindData(UserChannel userChannel) {
            if (userChannel != null) {
                mUserChannel = userChannel;
                User partner = mUserChannel.getMembers().get(0).getUid().equals(FirebaseAuth.getInstance().getUid())
                        ? mUserChannel.getMembers().get(1)
                        : mUserChannel.getMembers().get(0);
                mTextViewName.setText(partner.getName());
                if (mUserChannel.getLastMessage() != null) {
                    mTextViewLastMessage.setVisibility(View.VISIBLE);
                    mTextViewTime.setVisibility(View.VISIBLE);
                    mImageViewDot.setVisibility(View.VISIBLE);
                    String name = mUserChannel.getLastMessage().getSender().getUid().equals(FirebaseAuth.getInstance().getUid())
                            ? mContext.getString(R.string.you) + ": "
                            : "";
                    String lastMessage = name + mUserChannel.getLastMessage().getContent();
                    String displayMessage = lastMessage.length() < 30 ? lastMessage : lastMessage.substring(0, 29).concat("...");
                    mTextViewLastMessage.setText(displayMessage);
                    mTextViewTime.setText(convertSecondToDataTime(Integer.parseInt(mUserChannel.getLastMessage().getTimeStamp().getSeconds())));
                } else {
                    mTextViewLastMessage.setVisibility(View.INVISIBLE);
                    mTextViewTime.setVisibility(View.INVISIBLE);
                    mImageViewDot.setVisibility(View.INVISIBLE);
                }
                GlideApp.with(mContext)
                        .load(partner.getPhotoUri())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mImageViewGroupAvatar);
            }
        }
    }

    public interface OnItemUserChannelCLickListener {
        void onItemClick(UserChannel userChannel);

        void onItemLongClick(UserChannel userChannel);

    }
}
