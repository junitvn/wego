package com.lamnn.wego.data.model.place;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lamnn.wego.data.model.Location;

public class Point implements Parcelable {
    @SerializedName("id")
    @Expose
    private String mId;
    @SerializedName("idTrip")
    @Expose
    private String mIdTrip;
    @SerializedName("location")
    @Expose
    private Location mLocation;
    @SerializedName("name")
    @Expose
    private String mName;
    @SerializedName("type")
    @Expose
    private String mType;
    @SerializedName("creator_id")
    @Expose
    private String mCreatorId;
    @SerializedName("time_stamp")
    @Expose
    private String mTimeStamp;

    public Point() {
    }


    protected Point(Parcel in) {
        mId = in.readString();
        mIdTrip = in.readString();
        mLocation = in.readParcelable(Location.class.getClassLoader());
        mName = in.readString();
        mType = in.readString();
        mCreatorId = in.readString();
        mTimeStamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mIdTrip);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mName);
        dest.writeString(mType);
        dest.writeString(mCreatorId);
        dest.writeString(mTimeStamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getIdTrip() {
        return mIdTrip;
    }

    public void setIdTrip(String idTrip) {
        mIdTrip = idTrip;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getCreatorId() {
        return mCreatorId;
    }

    public void setCreatorId(String creatorId) {
        mCreatorId = creatorId;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }
}
