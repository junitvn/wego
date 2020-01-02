package com.lamnn.wego.screen.info.info_member;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.screen.event.EventPhotoAdapter;

import java.util.Date;
import java.util.List;

import static com.lamnn.wego.screen.map.InfoWindowAdapter.printDifference;
import static com.lamnn.wego.utils.AppUtils.STATUS_DONE;
import static com.lamnn.wego.utils.AppUtils.TYPE_WAITING;
import static com.lamnn.wego.utils.Utils.checkExistUid;

public class MemberEventAdapter extends RecyclerView.Adapter<MemberEventAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Event> mEvents;
    private OnEventItemClickListener mListener;

    public MemberEventAdapter(Context context, List<Event> events, OnEventItemClickListener listener) {
        mContext = context;
        mEvents = events;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_event_member, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBindData(mEvents.get(position));

    }

    @Override
    public int getItemCount() {
        return mEvents == null ? 0 : mEvents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, EventPhotoAdapter.OnPhotoItemClickListener {
        private Context mContext;
        private TextView mTextViewTitle, mTextViewNote, mTextViewStatus, mTextViewTime,
                mTextViewAddress, mTextViewWhoComing, mTextViewWhoWaiting;
        private Button mButtonImComing, mButtonImWaiting;
        private OnEventItemClickListener mOnEventItemClickListener;
        private ImageView mImageViewStatus;
        private Event mEvent;
        private RecyclerView mRecyclerViewEventPhoto;
        private EventPhotoAdapter mPhotoAdapter;

        public ViewHolder(Context context, @NonNull View itemView, OnEventItemClickListener onEventItemClickListener) {
            super(itemView);
            mContext = context;
            mOnEventItemClickListener = onEventItemClickListener;
            mImageViewStatus = itemView.findViewById(R.id.image_event_status);
            mTextViewNote = itemView.findViewById(R.id.text_event_note);
            mTextViewNote.setOnClickListener(this);
            mTextViewNote.setOnLongClickListener(this);
            mTextViewTitle = itemView.findViewById(R.id.text_event_type);
            mTextViewTitle.setOnClickListener(this);
            mTextViewTitle.setOnLongClickListener(this);
            mTextViewTime = itemView.findViewById(R.id.text_event_time);
            mTextViewTime.setOnClickListener(this);
            mTextViewTime.setOnLongClickListener(this);
            mTextViewStatus = itemView.findViewById(R.id.text_event_status);
            mTextViewStatus.setOnClickListener(this);
            mTextViewStatus.setOnLongClickListener(this);
            mTextViewAddress = itemView.findViewById(R.id.text_event_address);
            mTextViewAddress.setOnClickListener(this);
            mTextViewWhoComing = itemView.findViewById(R.id.text_event_who_coming);
            mTextViewWhoComing.setOnClickListener(this);
            mTextViewWhoWaiting = itemView.findViewById(R.id.text_event_who_waiting);
            mTextViewWhoWaiting.setOnClickListener(this);
            mImageViewStatus = itemView.findViewById(R.id.image_event_status);
            mButtonImWaiting = itemView.findViewById(R.id.button_event_im_waiting);
            mButtonImWaiting.setOnClickListener(this);
            mButtonImComing = itemView.findViewById(R.id.button_event_im_coming);
            mButtonImComing.setOnClickListener(this);
            mRecyclerViewEventPhoto = itemView.findViewById(R.id.recycler_event_photo_item);
            mRecyclerViewEventPhoto.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerViewEventPhoto.setHasFixedSize(true);
        }

        private void onBindData(Event event) {
            mEvent = event;
            if (event == null) return;
            if (mEvent.getPhotos() != null) {
                mPhotoAdapter = new EventPhotoAdapter(mContext, mEvent.getPhotos(), this, false);
                mRecyclerViewEventPhoto.setAdapter(mPhotoAdapter);
                mPhotoAdapter.notifyDataSetChanged();
            }
            mTextViewTitle.setText(event.getTitle());
            mTextViewNote.setText(event.getNote());

            String coming;
            if (event.getComingUsers() != null && event.getComingUsers().size() > 0) {
                List<String> comings = event.getComingUsers();
                if (checkExistUid(FirebaseAuth.getInstance().getUid(), comings)) {
                    mButtonImComing.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    if (comings.size() == 1) {
                        coming = mContext.getString(R.string.you_are_coming);
                    } else {
                        coming = mContext.getString(R.string.you_and) + (comings.size() - 1) + mContext.getString(R.string.members_are_coming);
                    }
                } else {
                    mButtonImComing.setTextColor(mContext.getResources().getColor(R.color.colorButtonEnable));
                    coming = comings.size() + mContext.getString(R.string.members_are_coming);
                }
                mTextViewWhoComing.setText(coming);
                mTextViewWhoComing.setVisibility(View.VISIBLE);
            }
            String waiting;
            if (event.getWaitingUsers() != null && event.getWaitingUsers().size() > 0) {
                List<String> waitings = event.getWaitingUsers();
                if (checkExistUid(FirebaseAuth.getInstance().getUid(), waitings)) {
                    mButtonImWaiting.setTextColor(mContext.getResources().getColor(R.color.colorCaution));
                    if (waitings.size() == 1) {
                        waiting = mContext.getString(R.string.you_are_waiting);
                    } else {
                        waiting = mContext.getString(R.string.you_and) + (waitings.size() - 1) + mContext.getString(R.string.member_are_waiting);
                    }
                } else {
                    mButtonImWaiting.setTextColor(mContext.getResources().getColor(R.color.colorButtonEnable));
                    waiting = waitings.size() + mContext.getString(R.string.member_are_waiting);
                }
                mTextViewWhoWaiting.setText(waiting);
                mTextViewWhoWaiting.setVisibility(View.VISIBLE);
            }
            if (event.getStatus().equals(TYPE_WAITING)) {
                mTextViewStatus.setText(mContext.getString(R.string.text_waiting));
                mImageViewStatus.setImageResource(R.drawable.ic_dot_orange);
            } else {
                mTextViewStatus.setText(mContext.getString(R.string.text_done));
                mImageViewStatus.setImageResource(R.drawable.ic_dot_blue);
            }
            if (mEvent.getStatus().equals(STATUS_DONE)) {
                mButtonImWaiting.setTextColor(mContext.getResources().getColor(R.color.colorButtonDisable));
                mButtonImComing.setTextColor(mContext.getResources().getColor(R.color.colorButtonDisable));
            }
            String time = printDifference(new Date(Long.parseLong(event.getTimeStamp().getSeconds()) * 1000), new Date(), mContext);
            mTextViewTime.setText(time);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_event_type:
                    mOnEventItemClickListener.onEventItemClick(mEvent);
                    break;
                case R.id.text_event_who_coming:
                    mOnEventItemClickListener.onEventTextWhoComingClick(mEvent);
                    break;
                case R.id.text_event_who_waiting:
                    mOnEventItemClickListener.onEventTextWhoWaitingClick(mEvent);
                    break;
                case R.id.button_event_im_coming:
                    if (mEvent.getStatus().equals(STATUS_DONE)) {
                        showAlert();
                    } else
                        mOnEventItemClickListener.onButtonImComingClick(mEvent);
                    break;
                case R.id.button_event_im_waiting:
                    if (mEvent.getStatus().equals(STATUS_DONE)) {
                        showAlert();
                    } else
                        mOnEventItemClickListener.onButtonImWaitingClick(mEvent);
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.text_event_status:
                case R.id.text_event_note:
                case R.id.text_event_time:
                case R.id.text_event_type:
                    mOnEventItemClickListener.onEventItemLongClick(mEvent);
                    break;
            }
            return false;
        }

        @Override
        public void onPhotoItemClick(Uri uri) {

        }

        @Override
        public void onRemoveClick(int position) {

        }

        private void showAlert() {
            Toast.makeText(mContext, mContext.getString(R.string.event_has_finished), Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnEventItemClickListener {
        void onEventItemClick(Event event);

        void onEventItemLongClick(Event event);

        void onEventTextWhoComingClick(Event event);

        void onEventTextWhoWaitingClick(Event event);

        void onButtonImComingClick(Event event);

        void onButtonImWaitingClick(Event event);
    }
}
