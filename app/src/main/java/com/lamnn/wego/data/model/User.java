package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

public class User implements Parcelable {
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("active_trip")
    @Expose
    private String mActiveTrip;
    @SerializedName("time_stamp")
    @Expose
    private MyTimeStamp mTimeStamp;
    @SerializedName("phone_number")
    @Expose
    private String mPhone;
    @SerializedName("photo_url")
    @Expose
    private String mPhotoUri;
    @SerializedName("uid")
    @Expose
    private String mUid;
    @SerializedName("status")
    @Expose
    private String mStatus;
    @SerializedName("location")
    @Expose
    private Location mLocation;

    public User() {
    }

    public User(String uid) {
        mUid = uid;
    }

    protected User(Parcel in) {
        mName = in.readString();
        mActiveTrip = in.readString();
        mPhone = in.readString();
        mPhotoUri = in.readString();
        mUid = in.readString();
        mStatus = in.readString();
        mLocation = in.readParcelable(Location.class.getClassLoader());
    }

    public User(String name, String phone, String photoUri, String uid) {
        mName = name;
        mPhone = phone;
        mPhotoUri = photoUri;
        mUid = uid;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mActiveTrip);
        dest.writeString(mPhone);
        dest.writeString(mPhotoUri);
        dest.writeString(mUid);
        dest.writeString(mStatus);
        dest.writeParcelable(mLocation, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getActiveTrip() {
        return mActiveTrip;
    }

    public void setActiveTrip(String activeTrip) {
        mActiveTrip = activeTrip;
    }

    public MyTimeStamp getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(MyTimeStamp timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(String photoUri) {
        mPhotoUri = photoUri;
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

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }
}
