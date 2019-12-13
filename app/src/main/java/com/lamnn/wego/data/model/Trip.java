package com.lamnn.wego.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trip implements Parcelable {

    @SerializedName("id")
    @Expose
    private String mId;
    @SerializedName("code")
    @Expose
    private String mCode;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("creator_id")
    @Expose
    private String mCreatorId;
    @SerializedName("start_point")
    @Expose
    private Point mStartPoint;
    @SerializedName("end_point")
    @Expose
    private Point mEndPoint;
    @SerializedName("creator_time")
    @Expose
    private String mCreationTime;
    @SerializedName("members")
    @Expose
    private List<String> mMembers = null;
    @SerializedName("points")
    @Expose
    private List<Point> mSpecialPoints = null;
    @SerializedName("trip_setting")
    @Expose
    private TripSetting mTripSetting;
    private Boolean isActive;

    public Trip() {
    }

    protected Trip(Parcel in) {
        mId = in.readString();
        mCode = in.readString();
        mName = in.readString();
        mCreatorId = in.readString();
        mStartPoint = in.readParcelable(Point.class.getClassLoader());
        mEndPoint = in.readParcelable(Point.class.getClassLoader());
        mCreationTime = in.readString();
        mMembers = in.createStringArrayList();
        mSpecialPoints = in.createTypedArrayList(Point.CREATOR);
        mTripSetting = in.readParcelable(TripSetting.class.getClassLoader());
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mCode);
        dest.writeString(mName);
        dest.writeString(mCreatorId);
        dest.writeParcelable(mStartPoint, flags);
        dest.writeParcelable(mEndPoint, flags);
        dest.writeString(mCreationTime);
        dest.writeStringList(mMembers);
        dest.writeTypedList(mSpecialPoints);
        dest.writeParcelable(mTripSetting, flags);
        dest.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCreatorId() {
        return mCreatorId;
    }

    public void setCreatorId(String creatorId) {
        mCreatorId = creatorId;
    }

    public Point getStartPoint() {
        return mStartPoint;
    }

    public void setStartPoint(Point startPoint) {
        mStartPoint = startPoint;
    }

    public Point getEndPoint() {
        return mEndPoint;
    }

    public void setEndPoint(Point endPoint) {
        mEndPoint = endPoint;
    }

    public String getCreationTime() {
        return mCreationTime;
    }

    public void setCreationTime(String creationTime) {
        mCreationTime = creationTime;
    }

    public List<String> getMembers() {
        return mMembers;
    }

    public void setMembers(List<String> members) {
        mMembers = members;
    }

    public List<Point> getSpecialPoints() {
        return mSpecialPoints;
    }

    public void setSpecialPoints(List<Point> specialPoints) {
        mSpecialPoints = specialPoints;
    }

    public TripSetting getTripSetting() {
        return mTripSetting;
    }

    public void setTripSetting(TripSetting tripSetting) {
        mTripSetting = tripSetting;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
