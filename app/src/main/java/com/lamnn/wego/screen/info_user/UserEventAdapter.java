package com.lamnn.wego.screen.info_user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Event;

import java.util.Date;
import java.util.List;

import static com.lamnn.wego.screen.map.InfoWindowAdapter.printDifference;

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
        View view = mLayoutInflater.inflate(R.layout.item_event, parent, false);
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

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Context mContext;
        private TextView mTextViewTitle, mTextViewNote, mTextViewStatus, mTextViewTime;
        private OnEventItemClickListener mOnEventItemClickListener;
        private ImageView mImageViewStatus;
        private Event mEvent;

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

        }

        private void onBindData(Event event) {
            mEvent = event;
            if (event == null) return;
            if (!event.getStatus().equals("waiting")) {
                mImageViewStatus.setImageResource(R.drawable.ic_vertical_line_done);
            }
            mTextViewTitle.setText(event.getTitle());
            mTextViewNote.setText(event.getNote());
            if (event.getStatus().equals("waiting")) {
                mTextViewStatus.setText("Status: Waiting");
            } else
                mTextViewStatus.setText("Status: Done");
            String time = printDifference(new Date(Long.parseLong(event.getTimeStamp().getSeconds()) * 1000), new Date());
            mTextViewTime.setText(time);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_event_type:
                    mOnEventItemClickListener.onEventItemClick(mEvent);
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
    }

    public interface OnEventItemClickListener {
        void onEventItemClick(Event event);

        void onEventItemLongClick(Event event);
    }
}
