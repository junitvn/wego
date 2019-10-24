package com.lamnn.wego.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

public class UserLocation {
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

    public UserLocation() {
    }

    public UserLocation(String uid, String status, MyTimeStamp timeStamp, Location location) {
        mUid = uid;
        mStatus = status;
        mTimeStamp = timeStamp;
        mLocation = location;
    }

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
}
