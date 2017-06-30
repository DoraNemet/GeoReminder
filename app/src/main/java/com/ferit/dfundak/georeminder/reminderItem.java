package com.ferit.dfundak.georeminder;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Dora on 17/06/2017.
 */

public class reminderItem {

    int mId;
    LatLng mPinnedLocation;
    double mLat;
    double mLong;
    double mRadius;
    String mTitle;
    String mDescription;
    String mDate;
    String mTime;
    String mImageName;

    public reminderItem(){}

    public reminderItem(LatLng pinnedLocation, double radius, String title, String description, String date, String time, String imageName) {
        this.mPinnedLocation = pinnedLocation;
        this.mRadius = radius;
        this.mTitle = title;
        this.mDescription = description;
        this.mDate = date;
        this.mTime = time;
        this.mImageName = imageName;
    }

    public reminderItem(int id, LatLng pinnedLocation, double radius, String title, String description, String date, String time, String imageName) {
        this.mId = id;
        this.mPinnedLocation = pinnedLocation;
        this.mRadius = radius;
        this.mTitle = title;
        this.mDescription = description;
        this.mDate = date;
        this.mTime = time;
        this.mImageName = imageName;
    }

    public reminderItem(String title, String description, String date, String time) {
        this.mTitle = title;
        this.mDescription = description;
        this.mDate = date;
        this.mTime = time;
    }

    public reminderItem(LatLng pinnedLocation, double radius, String title, String description) {
        this.mPinnedLocation = pinnedLocation;
        this.mRadius = radius;
        this.mTitle = title;
        this.mDescription = description;
    }

    public LatLng getPinnedLocation() {
        return mPinnedLocation;
    }

    public void setPinnedLocation(LatLng mPinnedLocation) {
        this.mPinnedLocation = mPinnedLocation;
    }

    public double getRadius() {
        return mRadius;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public Double getLat() {
        if(mPinnedLocation != null){
            return mPinnedLocation.latitude;
        }
        else{
            return 0.0;
        }
    }

    public Double getLong() {
        if(mPinnedLocation != null){
            return mPinnedLocation.longitude;
        }
        else{
            return 0.0;
        }
    }

    public int getID() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String mImageName) {
        this.mImageName = mImageName;
    }
}
