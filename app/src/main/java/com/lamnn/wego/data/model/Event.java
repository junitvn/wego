package com.lamnn.wego.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

import java.util.List;

public class Event {
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

    public Event() {
    }

    public Event(String title, String tripId, String userId, User user, List<String> photos, Location location, String note, String status) {
        mTitle = title;
        mTripId = tripId;
        mUserId = userId;
        mUser = user;
        mPhotos = photos;
        mLocation = location;
        mNote = note;
        mStatus = status;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    public String getTripId() {
        return mTripId;
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
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
}
