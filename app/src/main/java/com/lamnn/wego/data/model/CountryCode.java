package com.lamnn.wego.data.model;

import org.json.JSONException;
import org.json.JSONObject;

public class CountryCode {
    private String mName;
    private String mCodeNumber;
    private String mCode;

    public CountryCode() {
        super();
    }

    public CountryCode(String name, String codeNumber, String code) {
        this.mName = name;
        this.mCodeNumber = codeNumber;
        this.mCode = code;
    }

    public CountryCode(JSONObject item) throws JSONException {
        super();
        mName = item.getString(CountryCodeKey.JSON_KEY_NAME);
        mCodeNumber = item.getString(CountryCodeKey.JSON_KEY_CODE_NUMBER);
        mCode = item.getString(CountryCodeKey.JSON_KEY_CODE);
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String name) {
        this.mName = name;
    }

    public String getmCodeNumber() {
        return mCodeNumber;
    }

    public void setmCodeNumber(String codeNumber) {
        this.mCodeNumber = codeNumber;
    }

    public String getmCode() {
        return mCode;
    }

    public void setmCode(String code) {
        this.mCode = code;
    }

    public static class CountryCodeKey {
        public static final String JSON_KEY_NAME = "name";
        public static final String JSON_KEY_CODE_NUMBER = "dial_code";
        public static final String JSON_KEY_CODE = "code";
    }
}
