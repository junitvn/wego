package com.lamnn.wego.screen.trip.create_trip.share_code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.data.model.Invitation;
import com.lamnn.wego.data.model.User;
import com.lamnn.wego.data.model.route.MyTimeStamp;
import com.lamnn.wego.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

public class InvitationFriendAdapter extends RecyclerView.Adapter<InvitationFriendAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<User> mUsers;
    private OnUserFoundItemClickListener mListener;
    private String mTripId;

    public InvitationFriendAdapter(Context context, List<User> users, OnUserFoundItemClickListener listener) {
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
        View view = mLayoutInflater.inflate(R.layout.item_invite_friends, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(mUsers.get(position), mTripId);
    }

    @Override
    public int getItemCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private ImageView mImageViewAvatar;
        private TextView mTextViewName, mTextViewInviteStatus;
        private OnUserFoundItemClickListener mListener;
        private User mUser;
        private String mTripId;

        public ViewHolder(Context context, @NonNull View itemView, OnUserFoundItemClickListener listener) {
            super(itemView);
            mContext = context;
            mListener = listener;
            mImageViewAvatar = itemView.findViewById(R.id.image_item_user_invite_avatar);
            mTextViewName = itemView.findViewById(R.id.text_item_user_invite_name);
            itemView.setOnClickListener(this);
            mTextViewInviteStatus = itemView.findViewById(R.id.text_item_user_invite_status);
            mTextViewInviteStatus.setOnClickListener(this);
        }

        private void onBindData(User user, String tripId) {
            mTripId = tripId;
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
            getInvitation();
        }

        private void getInvitation() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("invitations")
                    .whereEqualTo("receiver_id", mUser.getUid())
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            Invitation invitation;
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                Gson gson = new Gson();
                                JsonElement jsonElement = gson.toJsonTree(doc.getData());
                                invitation = gson.fromJson(jsonElement, Invitation.class);
                                invitation.setId(doc.getId());
                                if (invitation.getCreator().getUid().equals(FirebaseAuth.getInstance().getUid())
                                        && mTripId.equals(invitation.getTrip().getCode())) {
                                    updateInvitationStatus(invitation);
                                }
                            }
                        }
                    });
        }

        private void updateInvitationStatus(Invitation invitation) {
            mUser.setInvitation(invitation);
            String status = "";
            switch (invitation.getStatus()) {
                case "cancel":
                    status = mContext.getString(R.string.invite);
                    mTextViewInviteStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    break;
                case "invited":
                    status = mContext.getString(R.string.cancel);
                    mTextViewInviteStatus.setTextColor(mContext.getResources().getColor(R.color.colorEditTextDisable));
                    break;
                case "joined":
                    status = mContext.getString(R.string.joined);
                    mTextViewInviteStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                default:
                    break;
            }
            mTextViewInviteStatus.setText(status);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.text_item_user_invite_status) {
                mListener.onInviteStatusClick(mUser);
            } else {
                mListener.onUserFoundClick(mUser);
            }
        }
    }

    public interface OnUserFoundItemClickListener {
        void onUserFoundClick(User user);

        void onInviteStatusClick(User user);
    }
}
