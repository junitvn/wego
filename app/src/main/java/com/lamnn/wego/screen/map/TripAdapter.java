package com.lamnn.wego.screen.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.lamnn.wego.R;
import com.lamnn.wego.data.model.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Trip> mTrips;
    private OnTripItemClickListener mListener;

    public TripAdapter(Context context, List<Trip> trips, OnTripItemClickListener listener) {
        mContext = context;
        mTrips = trips;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.item_trip, parent, false);
        return new ViewHolder(mContext, view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mTrips.get(position));
    }

    @Override
    public int getItemCount() {
        return mTrips != null ? mTrips.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private OnTripItemClickListener mClickListener;
        private Trip mTrip;
        private ImageView mImageSetting;
        private TextView mTextTripName, mTextTripCode;
        private ConstraintLayout mConstraintLayout;

        public ViewHolder(Context context, @NonNull View itemView, OnTripItemClickListener listener) {
            super(itemView);
            mContext = context;
            mClickListener = listener;
            mTextTripName = itemView.findViewById(R.id.text_item_spinner_name);
            mTextTripName.setOnClickListener(this);
            mTextTripCode = itemView.findViewById(R.id.text_trip_code);
            mTextTripCode.setOnClickListener(this);
            mImageSetting = itemView.findViewById(R.id.image_setting_trip);
            mImageSetting.setOnClickListener(this);
            mConstraintLayout = itemView.findViewById(R.id.layout_item_trip);
        }

        private void bindData(Trip trip) {
            if (trip == null) {
                return;
            }
            mTrip = trip;
            mTextTripName.setText(trip.getName());
            mTextTripCode.setText("ID: " + trip.getCode());
            if (trip.getActive() != null && trip.getActive()) {
                mTextTripName.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                mImageSetting.setImageResource(R.drawable.ic_settings_active);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_item_spinner_name:
                case R.id.text_trip_code:
                    if (mTrip.getActive() != null && mTrip.getActive()) {
                        mClickListener.onTripSettingClick(mTrip);
                    } else
                        mClickListener.onTripItemClick(mTrip);
                    break;
                case R.id.image_setting_trip:
                    mClickListener.onTripSettingClick(mTrip);
                    break;

            }
        }
    }

    interface OnTripItemClickListener {
        void onTripItemClick(Trip trip);

        void onTripSettingClick(Trip trip);
    }
}
