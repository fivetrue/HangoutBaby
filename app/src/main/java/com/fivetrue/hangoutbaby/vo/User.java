package com.fivetrue.hangoutbaby.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kwonojin on 16. 9. 17..
 */
public class User implements Parcelable{

    private String userUid;
    private String userId;
    private String userImageUrl;
    private String gcmId;
    private long registerDate;
    private long lastDate;
    private int state;

    protected User(Parcel in) {
        userUid = in.readString();
        userId = in.readString();
        userImageUrl = in.readString();
        gcmId = in.readString();
        registerDate = in.readLong();
        lastDate = in.readLong();
        state = in.readInt();
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

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public long getLastDate() {
        return lastDate;
    }

    public void setLastDate(long lastDate) {
        this.lastDate = lastDate;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "User{" +
                "userUid='" + userUid + '\'' +
                ", userId='" + userId + '\'' +
                ", userImageUrl='" + userImageUrl + '\'' +
                ", gcmId='" + gcmId + '\'' +
                ", registerDate=" + registerDate +
                ", lastDate=" + lastDate +
                ", state=" + state +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userUid);
        dest.writeString(userId);
        dest.writeString(userImageUrl);
        dest.writeString(gcmId);
        dest.writeLong(registerDate);
        dest.writeLong(lastDate);
        dest.writeInt(state);
    }
}
