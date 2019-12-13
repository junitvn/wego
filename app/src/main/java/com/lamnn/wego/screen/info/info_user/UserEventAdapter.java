package com.lamnn.wego.screen.info.info_user;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;
import com.lamnn.wego.screen.event.EventPhotoAdapter;

import java.util.Date;
import java.util.List;

import static com.lamnn.wego.screen.map.InfoWindowAdapter.printDifference;
import static com.lamnn.wego.utils.Utils.checkExistUid;

public class UserEventAdapter extends RecyclerView.Adapter<UserEventAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Event> mEvents;
    private OnEventItemClickListener mListener;

    public UserEventAdapter(Context context, List<Event> events, OnEventItemClickListener listener) {
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
        View view = mLayoutInflater.inflate(R.layout.item_event_user, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        private Button mButtonEdit, mButtonDelete, mButtonDone;
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
            mButtonEdit = itemView.findViewById(R.id.button_event_im_waiting);
            mButtonEdit.setOnClickListener(this);
            mButtonDelete = itemView.findViewById(R.id.button_event_im_coming);
            mButtonDelete.setOnClickListener(this);
            mButtonDone = itemView.findViewById(R.id.button_event_done);
            mButtonDone.setOnClickListener(this);
            mRecyclerViewEventPhoto = itemView.findViewById(R.id.recycler_event_photo_item);
            mRecyclerViewEventPhoto.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerViewEventPhoto.setHasFixedSize(true);

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
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
            if (event.getStatus().equals("waiting")) {
                mTextViewStatus.setText(mContext.getString(R.string.text_waiting));
                mImageViewStatus.setImageResource(R.drawable.ic_dot_orange);
            } else {
                mTextViewStatus.setText(mContext.getString(R.string.text_done));
                mImageViewStatus.setImageResource(R.drawable.ic_dot_blue);
                mButtonDelete.setTextColor(mContext.getColor(R.color.colorButtonDisable));
                mButtonEdit.setTextColor(mContext.getColor(R.color.colorButtonDisable));
                mButtonDone.setTextColor(mContext.getColor(R.color.colorButtonEnable));
                mButtonDone.setText(mContext.getString(R.string.text_undone));
            }
            String coming;
            if (event.getComingUsers() != null && event.getComingUsers().size() > 0) {
                List<String> comings = event.getComingUsers();
                if (checkExistUid(FirebaseAuth.getInstance().getUid(), comings)) {
                    if (comings.size() == 1) {
                        coming = mContext.getString(R.string.you_are_coming);
                    } else {
                        coming = mContext.getString(R.string.you_and) + (comings.size() - 1) + mContext.getString(R.string.members_are_coming);
                    }
                } else {
                    coming = comings.size() + mContext.getString(R.string.members_are_coming);
                }
                mTextViewWhoComing.setText(coming);
                mTextViewWhoComing.setVisibility(View.VISIBLE);
            }
            String waiting;
            if (event.getWaitingUsers() != null && event.getWaitingUsers().size() > 0) {
                List<String> waitings = event.getWaitingUsers();
                if (checkExistUid(FirebaseAuth.getInstance().getUid(), waitings)) {
                    if (waitings.size() == 1) {
                        waiting = mContext.getString(R.string.you_are_waiting);
                    } else {
                        waiting = mContext.getString(R.string.you_and) + (waitings.size() - 1) + mContext.getString(R.string.member_are_waiting);
                    }
                } else {
                    waiting = waitings.size() + mContext.getString(R.string.member_are_waiting);
                }
                mTextViewWhoWaiting.setText(waiting);
                mTextViewWhoWaiting.setVisibility(View.VISIBLE);
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
                case R.id.button_event_im_waiting:
                    if (mEvent.getStatus().equals("done")) {
                        showAlert();
                    } else {
                        mOnEventItemClickListener.onButtonEditClick(mEvent);
                    }
                    break;
                case R.id.button_event_done:
                    mOnEventItemClickListener.onButtonDoneClick(mEvent);
                    break;
                case R.id.button_event_im_coming:
                    if (mEvent.getStatus().equals("done")) {
                        showAlert();
                    } else
                        mOnEventItemClickListener.onButtonDeleteClick(mEvent);
                    break;
                case R.id.text_event_who_coming:
                    mOnEventItemClickListener.onEventTextWhoComingClick(mEvent);
                    break;
                case R.id.text_event_who_waiting:
                    mOnEventItemClickListener.onEventTextWhoWaitingClick(mEvent);
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

        void onButtonEditClick(Event event);

        void onButtonDeleteClick(Event event);

        void onButtonDoneClick(Event event);
    }
}
