package com.lamnn.wego.screen.conversation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.GroupMessage;
import com.lamnn.wego.utils.GlideApp;

import java.util.List;

import static com.lamnn.wego.utils.Utils.convertSecondToDataTime;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<GroupMessage> mGroupMessages;
    private final int DATE = 0, ME = 1, OTHER = 2;

    public ConversationAdapter(Context context, List<GroupMessage> groupMessages) {
        mContext = context;
        mGroupMessages = groupMessages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case DATE:
                View v1 = inflater.inflate(R.layout.layout_holder_date, parent, false);
                viewHolder = new DateViewHolder(v1);
                break;
            case ME:
                View v = inflater.inflate(R.layout.layout_holder_me, parent, false);
                viewHolder = new MeViewHolder(v);
                break;
            default:
                View v2 = inflater.inflate(R.layout.layout_holder_you, parent, false);
                viewHolder = new OtherViewHolder(mContext, v2);
                break;

        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (mGroupMessages.get(position).getSender().getUid().equals(FirebaseAuth.getInstance().getUid())) {
            return ME;
        } else return OTHER;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case DATE:
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                dateViewHolder.onBindData(mGroupMessages.get(position));
                break;
            case ME:
                MeViewHolder meViewHolder = (MeViewHolder) holder;
                meViewHolder.onBindData(mGroupMessages.get(position));
                break;
            default:
                OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
                otherViewHolder.onBindData(mGroupMessages.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mGroupMessages != null ? mGroupMessages.size() : 0;
    }

    public void addItem(List<GroupMessage> groupMessages) {
        groupMessages.addAll(groupMessages);
        notifyDataSetChanged();
    }

    static class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewTime;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTime = itemView.findViewById(R.id.text_conversation_time);
        }

        private void onBindData(GroupMessage groupMessage) {
            mTextViewTime.setText("DATE TO DO");
        }

    }

    static class MeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextViewTime;
        private TextView mTextViewContent;

        public MeViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewTime = itemView.findViewById(R.id.text_conversation_time_me);
            mTextViewContent = itemView.findViewById(R.id.text_conversation_content_me);
            mTextViewContent.setOnClickListener(this);
        }

        private void onBindData(GroupMessage groupMessage) {
            mTextViewContent.setText(groupMessage.getContent());
            mTextViewTime.setText(convertSecondToDataTime(Integer.parseInt(groupMessage.getTimeStamp().getSeconds())));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_conversation_content_me:
                    toggleTime();
                    break;
            }
        }

        private void toggleTime() {
            if (mTextViewTime.getVisibility() == View.VISIBLE) {
                mTextViewTime.setVisibility(View.GONE);
            } else {
                mTextViewTime.setVisibility(View.VISIBLE);
            }
        }
    }

    static class OtherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private TextView mTextViewTime;
        private TextView mTextViewContent, mTextViewSender;
        private ImageView mImageViewAvatar;

        public OtherViewHolder(Context context, @NonNull View itemView) {
            super(itemView);
            mContext = context;
            mTextViewTime = itemView.findViewById(R.id.text_conversation_time);
            mTextViewContent = itemView.findViewById(R.id.text_conversation_content);
            mTextViewContent.setOnClickListener(this);
            mTextViewSender = itemView.findViewById(R.id.text_conversation_sender);
            mImageViewAvatar = itemView.findViewById(R.id.image_avatar_chat);
        }

        private void onBindData(GroupMessage groupMessage) {
            mTextViewContent.setText(groupMessage.getContent());
            mTextViewSender.setText(groupMessage.getSender().getName());
            mTextViewTime.setText(convertSecondToDataTime(Integer.parseInt(groupMessage.getTimeStamp().getSeconds())));
            GlideApp.with(mContext)
                    .load(groupMessage.getSender().getPhotoUri())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mImageViewAvatar);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_conversation_content:
                    toggleTime();
                    break;
            }
        }

        private void toggleTime() {
            if (mTextViewTime.getVisibility() == View.VISIBLE) {
                mTextViewTime.setVisibility(View.GONE);
            } else {
                mTextViewTime.setVisibility(View.VISIBLE);
            }
        }
    }
}
