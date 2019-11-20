package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventStatus implements Parcelable {
    @SerializedName("trip_id")
    @Expose
    private String mTripId;
    @SerializedName("status")
    @Expose
    private String mStatus;
    @SerializedName("event")
    @Expose
    private Event mEvent;

    public EventStatus() {
    }

    public EventStatus(String tripId, String status, Event event) {
        mTripId = tripId;
        mStatus = status;
        mEvent = event;
    }

    protected EventStatus(Parcel in) {
        mTripId = in.readString();
        mStatus = in.readString();
        mEvent = in.readParcelable(Event.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTripId);
        dest.writeString(mStatus);
        dest.writeParcelable(mEvent, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EventStatus> CREATOR = new Creator<EventStatus>() {
        @Override
        public EventStatus createFromParcel(Parcel in) {
            return new EventStatus(in);
        }

        @Override
        public EventStatus[] newArray(int size) {
            return new EventStatus[size];
        }
    };

    public String getTripId() {
        return mTripId;
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public Event getEvent() {
        return mEvent;
    }

    public void setEvent(Event event) {
        mEvent = event;
    }
}
