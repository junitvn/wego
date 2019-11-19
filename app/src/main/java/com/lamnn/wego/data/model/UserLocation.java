package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

import java.util.List;

public class UserLocation implements Parcelable {
    @SerializedName("uid")
    @Expose
    private String mUid;
    @SerializedName("status")
    @Expose
    private String mStatus;
    @SerializedName("time_stamp")
    @Expose
    private MyTimeStamp mTimeStamp;
    @SerializedName("location")
    @Expose
    private Location mLocation;
    @SerializedName("user")
    @Expose
    private User mUser;
    @SerializedName("event_statuses")
    @Expose
    private List<EventStatus> mEventStatuses;
    @SerializedName("events")
    @Expose
    private List<Event> mEvents;


    public UserLocation() {
    }

    protected UserLocation(Parcel in) {
        mUid = in.readString();
        mStatus = in.readString();
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mUser = in.readParcelable(User.class.getClassLoader());
        mEventStatuses = in.createTypedArrayList(EventStatus.CREATOR);
        mEvents = in.createTypedArrayList(Event.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUid);
        dest.writeString(mStatus);
        dest.writeParcelable(mLocation, flags);
        dest.writeParcelable(mUser, flags);
        dest.writeTypedList(mEventStatuses);
        dest.writeTypedList(mEvents);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
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

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public List<EventStatus> getEventStatuses() {
        return mEventStatuses;
    }

    public void setEventStatuses(List<EventStatus> eventStatuses) {
        mEventStatuses = eventStatuses;
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    public void setEvents(List<Event> events) {
        mEvents = events;
    }
}
