package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Invitation implements Parcelable {
    @SerializedName("id")
    @Expose
    private String mId;
    @SerializedName("creator")
    @Expose
    private User mCreator;
    @SerializedName("receiver_id")
    @Expose
    private String mReceiverId;
    @SerializedName("trip")
    @Expose
    private Trip mTrip;
    @SerializedName("status")
    @Expose
    private String mStatus;

    public Invitation() {
    }

    public Invitation(User creator, String receiverId, Trip trip, String status) {
        mCreator = creator;
        mReceiverId = receiverId;
        mTrip = trip;
        mStatus = status;
    }

    protected Invitation(Parcel in) {
        mId = in.readString();
        mCreator = in.readParcelable(User.class.getClassLoader());
        mReceiverId = in.readString();
        mTrip = in.readParcelable(Trip.class.getClassLoader());
        mStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeParcelable(mCreator, flags);
        dest.writeString(mReceiverId);
        dest.writeParcelable(mTrip, flags);
        dest.writeString(mStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Invitation> CREATOR = new Creator<Invitation>() {
        @Override
        public Invitation createFromParcel(Parcel in) {
            return new Invitation(in);
        }

        @Override
        public Invitation[] newArray(int size) {
            return new Invitation[size];
        }
    };

    public User getCreator() {
        return mCreator;
    }

    public void setCreator(User creator) {
        mCreator = creator;
    }

    public String getReceiverId() {
        return mReceiverId;
    }

    public void setReceiverId(String receiverId) {
        mReceiverId = receiverId;
    }

    public Trip getTrip() {
        return mTrip;
    }

    public void setTrip(Trip trip) {
        mTrip = trip;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
