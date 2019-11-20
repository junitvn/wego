package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

import java.util.List;

public class Event implements Parcelable {
    @SerializedName("type")
    @Expose
    private String mTitle;
    @SerializedName("trip_id")
    @Expose
    private String mTripId;
    @SerializedName("event_id")
    @Expose
    private String mEventId;
    @SerializedName("user_id")
    @Expose
    private String mUserId;
    @SerializedName("user")
    @Expose
    private User mUser;
    @SerializedName("photos")
    @Expose
    private List<String> mPhotos;
    @SerializedName("location")
    @Expose
    private Location mLocation;
    @SerializedName("note")
    @Expose
    private String mNote;
    @SerializedName("status")
    @Expose
    private String mStatus;
    @SerializedName("time_stamp")
    @Expose
    private MyTimeStamp mTimeStamp;
    @SerializedName("coming_users")
    @Expose
    private List<String> mComingUsers;
    @SerializedName("waiting_users")
    @Expose
    private List<String> mWaitingUsers;

    public Event() {
    }

    public Event(String title, String tripId, String eventId, String userId, User user, List<String> photos, Location location, String note, String status, MyTimeStamp timeStamp, List<String> comingUsers, List<String> waitingUsers) {
        mTitle = title;
        mTripId = tripId;
        mEventId = eventId;
        mUserId = userId;
        mUser = user;
        mPhotos = photos;
        mLocation = location;
        mNote = note;
        mStatus = status;
        mTimeStamp = timeStamp;
        mComingUsers = comingUsers;
        mWaitingUsers = waitingUsers;
    }

    protected Event(Parcel in) {
        mTitle = in.readString();
        mTripId = in.readString();
        mEventId = in.readString();
        mUserId = in.readString();
        mUser = in.readParcelable(User.class.getClassLoader());
        mPhotos = in.createStringArrayList();
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mNote = in.readString();
        mStatus = in.readString();
        mComingUsers = in.createStringArrayList();
        mWaitingUsers = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mTripId);
        dest.writeString(mEventId);
        dest.writeString(mUserId);
        dest.writeParcelable(mUser, flags);
        dest.writeStringList(mPhotos);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mNote);
        dest.writeString(mStatus);
        dest.writeStringList(mComingUsers);
        dest.writeStringList(mWaitingUsers);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTripId() {
        return mTripId;
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public List<String> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(List<String> photos) {
        mPhotos = photos;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public String getNote() {
        return mNote;
    }

    public void setNote(String note) {
        mNote = note;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public MyTimeStamp getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(MyTimeStamp timeStamp) {
        mTimeStamp = timeStamp;
    }

    public List<String> getComingUsers() {
        return mComingUsers;
    }

    public void setComingUsers(List<String> comingUsers) {
        mComingUsers = comingUsers;
    }

    public List<String> getWaitingUsers() {
        return mWaitingUsers;
    }

    public void setWaitingUsers(List<String> waitingUsers) {
        mWaitingUsers = waitingUsers;
    }
}
