package com.viner.musy;


public class Song {
    private long mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;

    public Song(long mId, String mTitle, String mArtist) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mArtist = mArtist;
    }

    public Song(String mTitle, String mArtist) {
        this.mTitle = mTitle;
        this.mArtist = mArtist;
    }

    public Song(long mId, String mTitle, String mArtist, String mAlbum) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mArtist = mArtist;
        this.mAlbum = mAlbum;
    }

    public long getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmArtist() {
        return mArtist;
    }

    public String getmAlbum() {
        return mAlbum;
    }
}
