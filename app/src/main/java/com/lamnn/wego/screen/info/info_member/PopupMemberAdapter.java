package com.lamnn.wego.screen.info.info_member;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.UserLocation;

import java.util.List;

public class PopupMemberAdapter extends RecyclerView.Adapter<PopupMemberAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<UserLocation> mUserLocationList;
    private OnMemberItemClickListener mListener;

    public PopupMemberAdapter(Context context, List<UserLocation> userLocationList, OnMemberItemClickListener listener) {
        mContext = context;
        mUserLocationList = userLocationList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_coming_member, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(mUserLocationList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserLocationList == null ? 0 : mUserLocationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private TextView mTextViewMemberName;
        private ImageView mImageViewGoToLocation, mImageViewCall;
        private UserLocation mUserLocation;
        private OnMemberItemClickListener mListener;

        public ViewHolder(Context context, @NonNull View itemView, OnMemberItemClickListener onMemberItemClickListener) {
            super(itemView);
            mContext = context;
            mListener = onMemberItemClickListener;
            mTextViewMemberName = itemView.findViewById(R.id.text_member_name);
            mTextViewMemberName.setOnClickListener(this);
            mImageViewGoToLocation = itemView.findViewById(R.id.image_go_to_member);
            mImageViewGoToLocation.setOnClickListener(this);
            mImageViewCall = itemView.findViewById(R.id.image_call_to_member);
            mImageViewCall.setOnClickListener(this);
        }

        private void onBindData(UserLocation userLocation) {
            if (userLocation == null) return;
            mUserLocation = userLocation;
            if (FirebaseAuth.getInstance().getUid().equals(userLocation.getUid())) {
                StringBuilder name = new StringBuilder();
                name.append(userLocation.getUser().getName())
                        .append(" (")
                        .append(mContext.getString(R.string.text_me))
                        .append(")");
                mTextViewMemberName.setText(name);
                mImageViewGoToLocation.setVisibility(View.INVISIBLE);
                mImageViewCall.setVisibility(View.INVISIBLE);
            } else {
                mTextViewMemberName.setText(userLocation.getUser().getName());
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_member_name:
                    mListener.onMemberNameClick(mUserLocation);
                    break;
                case R.id.image_go_to_member:
                    mListener.onGoToLocationClick(mUserLocation);
                    break;
                case R.id.image_call_to_member:
                    mListener.onCallClick(mUserLocation);
                    break;
            }
        }
    }

    public interface OnMemberItemClickListener {
        void onMemberNameClick(UserLocation userLocation);

        void onGoToLocationClick(UserLocation userLocation);

        void onCallClick(UserLocation userLocation);
    }
}
