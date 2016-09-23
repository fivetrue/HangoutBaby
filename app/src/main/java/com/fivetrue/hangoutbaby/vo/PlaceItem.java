package com.fivetrue.hangoutbaby.vo;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by kwonojin on 16. 9. 17..
 */
public class PlaceItem implements Parcelable{

    private String placeId;

    private String placeName;

    private double placeLatitude;
    private double placeLongitude;

    private String placeCity;
    private String placeAddress;

    private String placeDescription;

    private String placeAuthor;

    private long placePostDate;

    private int placeCommentCount;

    private User user = null;

    protected PlaceItem(Parcel in) {
        placeId = in.readString();
        placeName = in.readString();
        placeLatitude = in.readDouble();
        placeLongitude = in.readDouble();
        placeCity = in.readString();
        placeDescription = in.readString();
        placeAuthor = in.readString();
        placePostDate = in.readLong();
        placeCommentCount = in.readInt();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<PlaceItem> CREATOR = new Creator<PlaceItem>() {
        @Override
        public PlaceItem createFromParcel(Parcel in) {
            return new PlaceItem(in);
        }

        @Override
        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    };

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public String getPlaceCity() {
        return placeCity;
    }

    public void setPlaceCity(String placeCity) {
        this.placeCity = placeCity;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public String getPlaceAuthor() {
        return placeAuthor;
    }

    public void setPlaceAuthor(String placeAuthor) {
        this.placeAuthor = placeAuthor;
    }

    public long getPlacePostDate() {
        return placePostDate;
    }

    public void setPlacePostDate(long placePostDate) {
        this.placePostDate = placePostDate;
    }

    public int getPlaceCommentCount() {
        return placeCommentCount;
    }

    public void setPlaceCommentCount(int placeCommentCount) {
        this.placeCommentCount = placeCommentCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    @Override
    public String toString() {
        return "Place [placeId=" + placeId + ", placeName=" + placeName + ", placeLatitude=" + placeLatitude
                + ", placeLongitude=" + placeLongitude + ", placeCity=" + placeCity + ", placeAddress=" + placeAddress
                + ", placeDescription=" + placeDescription + ", placeAuthor=" + placeAuthor + ", placePostDate="
                + placePostDate + ", placeCommentCount=" + placeCommentCount + ", user=" + user + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(placeName);
        dest.writeDouble(placeLatitude);
        dest.writeDouble(placeLongitude);
        dest.writeString(placeCity);
        dest.writeString(placeDescription);
        dest.writeString(placeAuthor);
        dest.writeLong(placePostDate);
        dest.writeInt(placeCommentCount);
        dest.writeParcelable(user, flags);
    }
}
