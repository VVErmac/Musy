package com.viner.musy;


public class Album {
    private String mTitle;
    private int mId;

    public Album(String Title) {
        this.mTitle = Title;
    }

    public String getmTitle() {
        return mTitle;
    }

    public int getmId() {
        return mId;
    }

    public Album(int id, String Title) {
        this.mTitle = Title;
        this.mId = id;
    }
}
