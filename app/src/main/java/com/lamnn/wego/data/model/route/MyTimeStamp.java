package com.lamnn.wego.data.model.route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyTimeStamp {
    @SerializedName("_seconds")
    @Expose
    private String mSeconds;
    @SerializedName("_nanoseconds")
    @Expose
    private String mNanoseconds;

    public MyTimeStamp() {
    }

    public MyTimeStamp(String seconds) {
        mSeconds = seconds;
    }

    public MyTimeStamp(String seconds, String nanoseconds) {
        mSeconds = seconds;
        mNanoseconds = nanoseconds;
    }

    public String getSeconds() {
        return mSeconds;
    }

    public void setSeconds(String seconds) {
        mSeconds = seconds;
    }

    public String getNanoseconds() {
        return mNanoseconds;
    }

    public void setNanoseconds(String nanoseconds) {
        mNanoseconds = nanoseconds;
    }
}