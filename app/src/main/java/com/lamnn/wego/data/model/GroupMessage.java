package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

public class GroupMessage implements Parcelable {
    @SerializedName("id")
    @Expose
    private String mId;
    @SerializedName("content")
    @Expose
    private String mContent;
    @SerializedName("sender")
    @Expose
    private User mSender;
    @SerializedName("time_stamp")
    @Expose
    private MyTimeStamp mTimeStamp;
    @SerializedName("group_id")
    @Expose
    private String mGroupId;
    @SerializedName("group_name")
    @Expose
    private String mGroupName;


    public GroupMessage() {
    }

    public GroupMessage(String id, String content, User sender, MyTimeStamp timeStamp, String groupId, String groupName) {
        mId = id;
        mContent = content;
        mSender = sender;
        mTimeStamp = timeStamp;
        mGroupId = groupId;
        mGroupName = groupName;
    }

    protected GroupMessage(Parcel in) {
        mId = in.readString();
        mContent = in.readString();
        mSender = in.readParcelable(User.class.getClassLoader());
        mGroupId = in.readString();
        mGroupName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mContent);
        dest.writeParcelable(mSender, flags);
        dest.writeString(mGroupId);
        dest.writeString(mGroupName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupMessage> CREATOR = new Creator<GroupMessage>() {
        @Override
        public GroupMessage createFromParcel(Parcel in) {
            return new GroupMessage(in);
        }

        @Override
        public GroupMessage[] newArray(int size) {
            return new GroupMessage[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public User getSender() {
        return mSender;
    }

    public void setSender(User sender) {
        mSender = sender;
    }

    public MyTimeStamp getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(MyTimeStamp timeStamp) {
        mTimeStamp = timeStamp;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(String groupId) {
        mGroupId = groupId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }
}
