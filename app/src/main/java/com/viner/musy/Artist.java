package com.viner.musy;


public class Artist {
    private String mName;
    private int mId;

    public Artist(String mName){
        this.mName = mName;
    }

    public Artist( int id, String mName) {
        this.mName = mName;
        this.mId = id;
    }

    public String getmName() {

        return mName;
    }

    public int getmId() {
        return mId;
    }
}
