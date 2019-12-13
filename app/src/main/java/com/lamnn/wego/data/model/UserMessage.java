package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.route.MyTimeStamp;

public class UserMessage implements Parcelable {
    @SerializedName("channel_id")
    @Expose
    private String mChannelId;
    @SerializedName("content")
    @Expose
    private String mContent;
    @SerializedName("sender")
    @Expose
    private User mSender;
    @SerializedName("time_stamp")
    @Expose
    private MyTimeStamp mTimeStamp;

    public UserMessage() {
    }

    public UserMessage(String channelId, String content, User sender, MyTimeStamp timeStamp) {
        mChannelId = channelId;
        mContent = content;
        mSender = sender;
        mTimeStamp = timeStamp;
    }

    protected UserMessage(Parcel in) {
        mChannelId = in.readString();
        mContent = in.readString();
        mSender = in.readParcelable(User.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mChannelId);
        dest.writeString(mContent);
        dest.writeParcelable(mSender, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserMessage> CREATOR = new Creator<UserMessage>() {
        @Override
        public UserMessage createFromParcel(Parcel in) {
            return new UserMessage(in);
        }

        @Override
        public UserMessage[] newArray(int size) {
            return new UserMessage[size];
        }
    };

    public String getChannelId() {
        return mChannelId;
    }

    public void setChannelId(String channelId) {
        mChannelId = channelId;
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
}
