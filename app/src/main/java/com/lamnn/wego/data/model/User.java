package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

import java.util.List;

public class User implements Parcelable {
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("active_trip")
    @Expose
    private String mActiveTrip;
    @SerializedName("phone_number")
    @Expose
    private String mPhone;
    @SerializedName("photo_url")
    @Expose
    private String mPhotoUri;
    @SerializedName("uid")
    @Expose
    private String mUid;
    @SerializedName("is_first_time")
    @Expose
    private Boolean mIsFirstTime;
    @SerializedName("my_trips")
    @Expose
    private List<String> mMyTrips;
    @SerializedName("friends")
    @Expose
    private List<String> mFriends;
    private Invitation mInvitation;

    public User() {
    }

    public User(String uid) {
        mUid = uid;
    }


    public User(String name, String phone, String photoUri, String uid) {
        mName = name;
        mPhone = phone;
        mPhotoUri = photoUri;
        mUid = uid;
    }

    protected User(Parcel in) {
        mName = in.readString();
        mActiveTrip = in.readString();
        mPhone = in.readString();
        mPhotoUri = in.readString();
        mUid = in.readString();
        byte tmpMIsFirstTime = in.readByte();
        mIsFirstTime = tmpMIsFirstTime == 0 ? null : tmpMIsFirstTime == 1;
        mMyTrips = in.createStringArrayList();
        mFriends = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mActiveTrip);
        dest.writeString(mPhone);
        dest.writeString(mPhotoUri);
        dest.writeString(mUid);
        dest.writeByte((byte) (mIsFirstTime == null ? 0 : mIsFirstTime ? 1 : 2));
        dest.writeStringList(mMyTrips);
        dest.writeStringList(mFriends);
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

    public Boolean getFirstTime() {
        return mIsFirstTime;
    }

    public void setFirstTime(Boolean firstTime) {
        mIsFirstTime = firstTime;
    }

    public List<String> getMyTrips() {
        return mMyTrips;
    }

    public void setMyTrips(List<String> myTrips) {
        mMyTrips = myTrips;
    }

    public List<String> getFriends() {
        return mFriends;
    }

    public void setFriends(List<String> friends) {
        mFriends = friends;
    }

    public Invitation getInvitation() {
        return mInvitation;
    }

    public void setInvitation(Invitation invitation) {
        mInvitation = invitation;
    }
}
