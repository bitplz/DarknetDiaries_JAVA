package com.bug32.darknetdiaries;

import java.util.Iterator;

public class Item {

    private String mTitle;
    private String mDesc;
    private String mImgUrl;
    private String mAudioUrl;
    private String mPubDate;
    private String mDuration;


    public Item (String title, String desc, String imgUrl, String audioUrl, String pubDate, String duration){

        this.mTitle = title;
        this.mDesc = desc;
        this.mImgUrl = imgUrl;
        this.mAudioUrl = audioUrl;
        this.mPubDate = pubDate;
        this.mDuration = duration;

    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmImgUrl() {
        return mImgUrl;
    }

    public void setmImgUrl(String mImgUrl) {
        this.mImgUrl = mImgUrl;
    }

    public String getmAudioUrl() {
        return mAudioUrl;
    }

    public void setmAudioUrl(String mAudioUrl) {
        this.mAudioUrl = mAudioUrl;
    }

    public String getmPubDate() {
        return mPubDate;
    }

    public void setmPubDate(String mPubDate) {
        this.mPubDate = mPubDate;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }
}
