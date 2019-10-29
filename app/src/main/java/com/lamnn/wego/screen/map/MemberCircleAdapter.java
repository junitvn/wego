package com.lamnn.wego.screen.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.UserLocation;
import com.lamnn.wego.utils.GlideApp;

import java.util.List;

public class MemberCircleAdapter extends RecyclerView.Adapter<MemberCircleAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<UserLocation> mUsers;
    private OnUserItemClickListener mListener;

    public MemberCircleAdapter(Context context, List<UserLocation> users, OnUserItemClickListener listener) {
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
        View view = mLayoutInflater.inflate(R.layout.item_list_member_maps, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mUsers.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private OnUserItemClickListener mClickListener;
        private UserLocation mUserLocation;
        private User mUser;
        private ImageView mImageAvatar;
        private TextView mTextName;

        public ViewHolder(Context context, @NonNull View itemView, OnUserItemClickListener listener) {
            super(itemView);
            mContext = context;
            mClickListener = listener;
            mImageAvatar = itemView.findViewById(R.id.image_circle_all);
            mImageAvatar.setOnClickListener(this);
            mTextName = itemView.findViewById(R.id.text_circle_name);
            mTextName.setOnClickListener(this);
        }

        private void bindData(UserLocation userLocation, int position) {
            if (userLocation == null) {
                return;
            }
            mUserLocation = userLocation;
            if (mUserLocation.getUser() != null) {
                mUser = mUserLocation.getUser();
            }
            if (position == 0) {
                mTextName.setText("Me");
            } else {
                mTextName.setText(mUser.getName());
            }
            if (mUser.getPhotoUri() != null) {
                GlideApp.with(mContext)
                        .load(mUser.getPhotoUri())
                        .into(mImageAvatar);
            }
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onUserItemClick(mUserLocation);
            } else {
                Toast.makeText(mContext, "Click listener null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    interface OnUserItemClickListener {
        void onUserItemClick(UserLocation userLocation);
    }
}
