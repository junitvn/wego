package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripSetting implements Parcelable {
    @SerializedName("receive_notification")
    @Expose
    private Boolean mReceiveNotification;
    @SerializedName("min_distance")
    @Expose
    private long mMinDistance;
    @SerializedName("time_to_repeat")
    @Expose
    private int mTimeToRepeat;

    public TripSetting() {
    }

    public TripSetting(Boolean receiveNotification, long minDistance, int TimeToRepeat) {
        mReceiveNotification = receiveNotification;
        mMinDistance = minDistance;
        mTimeToRepeat = TimeToRepeat;
    }

    public void setDefaultValue() {
        mReceiveNotification = true;
        mMinDistance = 1000;
        mTimeToRepeat = 5; //minutes
    }

    protected TripSetting(Parcel in) {
        byte tmpMReceiveNotification = in.readByte();
        mReceiveNotification = tmpMReceiveNotification == 0 ? null : tmpMReceiveNotification == 1;
        mMinDistance = in.readLong();
        mTimeToRepeat = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mReceiveNotification == null ? 0 : mReceiveNotification ? 1 : 2));
        dest.writeLong(mMinDistance);
        dest.writeInt(mTimeToRepeat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripSetting> CREATOR = new Creator<TripSetting>() {
        @Override
        public TripSetting createFromParcel(Parcel in) {
            return new TripSetting(in);
        }

        @Override
        public TripSetting[] newArray(int size) {
            return new TripSetting[size];
        }
    };

    public Boolean getReceiveNotification() {
        return mReceiveNotification;
    }

    public void setReceiveNotification(Boolean receiveNotification) {
        mReceiveNotification = receiveNotification;
    }

    public long getMinDistance() {
        return mMinDistance;
    }

    public void setMinDistance(long minDistance) {
        mMinDistance = minDistance;
    }

    public int getTimeToRepeat() {
        return mTimeToRepeat;
    }

    public void setTimeToRepeat(int timeToRepeat) {
        mTimeToRepeat = timeToRepeat;
    }
}
