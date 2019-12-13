package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GroupChannel implements Parcelable {
    @SerializedName("trip_id")
    @Expose
    private String mTripId;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("members")
    @Expose
    private List<String> mMembers;
    @SerializedName("last_message")
    @Expose
    private GroupMessage mLastMessage;

    public GroupChannel() {
    }

    public GroupChannel(String tripId, String name, List<String> members, GroupMessage lastMessage) {
        mTripId = tripId;
        mName = name;
        mMembers = members;
        mLastMessage = lastMessage;
    }

    protected GroupChannel(Parcel in) {
        mTripId = in.readString();
        mName = in.readString();
        mMembers = in.createStringArrayList();
        mLastMessage = in.readParcelable(GroupMessage.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTripId);
        dest.writeString(mName);
        dest.writeStringList(mMembers);
        dest.writeParcelable(mLastMessage, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupChannel> CREATOR = new Creator<GroupChannel>() {
        @Override
        public GroupChannel createFromParcel(Parcel in) {
            return new GroupChannel(in);
        }

        @Override
        public GroupChannel[] newArray(int size) {
            return new GroupChannel[size];
        }
    };

    public String getTripId() {
        return mTripId;
    }

    public void setTripId(String tripId) {
        mTripId = tripId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<String> getMembers() {
        return mMembers;
    }

    public void setMembers(List<String> members) {
        mMembers = members;
    }

    public GroupMessage getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(GroupMessage lastMessage) {
        mLastMessage = lastMessage;
    }
}
