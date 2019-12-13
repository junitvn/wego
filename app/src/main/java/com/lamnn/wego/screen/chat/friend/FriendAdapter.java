package com.lamnn.wego.screen.chat.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.utils.GlideApp;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<User> mUsers;
    private OnFriendItemClickListener mListener;

    public FriendAdapter(Context context, List<User> users, OnFriendItemClickListener listener) {
        mContext = context;
        mUsers = users;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private ImageView mImageViewAvatar, mImageViewMessage, mImageViewCall;
        private TextView mTextViewName;
        private OnFriendItemClickListener mListener;
        private User mUser;

        public ViewHolder(Context context, @NonNull View itemView, OnFriendItemClickListener listener) {
            super(itemView);
            mContext = context;
            mListener = listener;
            mImageViewAvatar = itemView.findViewById(R.id.image_item_user_found_avatar);
            mTextViewName = itemView.findViewById(R.id.text_item_user_found_name);
            mImageViewCall = itemView.findViewById(R.id.image_call_to_friend);
            mImageViewCall.setOnClickListener(this);
            mImageViewMessage = itemView.findViewById(R.id.image_message_to_friend);
            mImageViewMessage.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        private void onBindData(User user) {
            mUser = user;
            if (user == null) return;
            mTextViewName.setText(user.getName());
            if (user.getPhotoUri() != null) {
                GlideApp.with(mContext)
                        .load(user.getPhotoUri())
                        .apply(RequestOptions.circleCropTransform())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .placeholder(R.drawable.ic_user)
                        .into(mImageViewAvatar);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_call_to_friend:
                    mListener.onFriendItemCallClick(mUser);
                    break;
                case R.id.image_message_to_friend:
                    mListener.onFriendItemMessageCLick(mUser);
                    break;
                default:
                    mListener.onFriendItemClick(mUser);
                    break;
            }
        }
    }

    public interface OnFriendItemClickListener {
        void onFriendItemClick(User user);

        void onFriendItemCallClick(User user);

        void onFriendItemMessageCLick(User user);
    }
}
