package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserChannel implements Parcelable {
    @SerializedName("channel_id")
    @Expose
    private String mChannelId;
    @SerializedName("member_uid")
    @Expose
    private List<String> mMemberUid;
    @SerializedName("members")
    @Expose
    private List<User> mMembers;
    @SerializedName("last_message")
    @Expose
    private UserMessage mLastMessage;
    @SerializedName("user_id")
    @Expose
    private String mUserId;

    public UserChannel() {
    }

    protected UserChannel(Parcel in) {
        mChannelId = in.readString();
        mMemberUid = in.createStringArrayList();
        mMembers = in.createTypedArrayList(User.CREATOR);
        mLastMessage = in.readParcelable(GroupMessage.class.getClassLoader());
        mUserId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mChannelId);
        dest.writeStringList(mMemberUid);
        dest.writeTypedList(mMembers);
        dest.writeParcelable(mLastMessage, flags);
        dest.writeString(mUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserChannel> CREATOR = new Creator<UserChannel>() {
        @Override
        public UserChannel createFromParcel(Parcel in) {
            return new UserChannel(in);
        }

        @Override
        public UserChannel[] newArray(int size) {
            return new UserChannel[size];
        }
    };

    public String getChannelId() {
        return mChannelId;
    }

    public void setChannelId(String channelId) {
        mChannelId = channelId;
    }

    public List<String> getMemberUid() {
        return mMemberUid;
    }

    public void setMemberUid(List<String> memberUid) {
        mMemberUid = memberUid;
    }

    public List<User> getMembers() {
        return mMembers;
    }

    public void setMembers(List<User> members) {
        mMembers = members;
    }

    public UserMessage getLastMessage() {
        return mLastMessage;
    }

    public void setLastMessage(UserMessage lastMessage) {
        mLastMessage = lastMessage;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }


}
