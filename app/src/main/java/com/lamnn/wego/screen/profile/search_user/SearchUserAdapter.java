package com.lamnn.wego.screen.profile.search_user;

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

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<User> mUsers;
    private OnUserFoundItemClickListener mListener;

    public SearchUserAdapter(Context context, List<User> users, OnUserFoundItemClickListener listener) {
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
        View view = mLayoutInflater.inflate(R.layout.item_search_user, parent, false);
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
        private ImageView mImageViewAvatar;
        private TextView mTextViewName;
        private OnUserFoundItemClickListener mListener;
        private User mUser;

        public ViewHolder(Context context, @NonNull View itemView, OnUserFoundItemClickListener listener) {
            super(itemView);
            mContext = context;
            mListener = listener;
            mImageViewAvatar = itemView.findViewById(R.id.image_item_user_found_avatar);
            mTextViewName = itemView.findViewById(R.id.text_item_user_found_name);
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
            mListener.onUserFoundClick(mUser);
        }
    }

    public interface OnUserFoundItemClickListener {
        void onUserFoundClick(User user);
    }
}
