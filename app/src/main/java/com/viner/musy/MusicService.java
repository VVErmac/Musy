package com.viner.musy;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mPlayer = new MediaPlayer();
    private ArrayList<Song> mSongs = new ArrayList<>();
    private int mSongPosition;
    private final IBinder mMusicBind = new MusicBinder();
    private String mSongTitle = "";
    private String mSongArtist = "";
    private static final int NOTIFY_ID = 1;
    private boolean mShuffle;
    private Random mRandom;
    private final String MUSIC_SERVICE_ERROR = "MUSIC SERVICE";


    @Override
    public void onCreate() {
        super.onCreate();

        mSongPosition = 0;
        mRandom = new Random();
        mPlayer = new MediaPlayer();
        initMediaPlayer();
    }


    public void initMediaPlayer() {
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        mSongs = theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBind;
    }

    public boolean onUnbind(Intent intent) {
        mPlayer.reset();
        mPlayer.release();
        return false;
    }


    public void playSong() {
        mPlayer.reset();
        Song playSong = mSongs.get(mSongPosition);
        mSongTitle = playSong.getmTitle();
        mSongArtist = playSong.getmArtist();
        long currentSong = playSong.getmId();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);

        try {
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e(MUSIC_SERVICE_ERROR, "Error setting data source", e);
        }
        mPlayer.prepareAsync();

        // Toast.makeText(this, "Playing: " + mSongTitle + " " + mSongArtist, Toast.LENGTH_SHORT).show();
    }

    public void setSong(int songIndex) {
        mSongPosition = songIndex;
    }

    public void onCompletion(MediaPlayer mp) {
        if (mPlayer.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.v(MUSIC_SERVICE_ERROR, "Playback Error");
        mediaPlayer.reset();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

        Intent notificationIntent = new Intent(this, AudioController.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setTicker(mSongTitle)
                .setOngoing(false)
                .setContentTitle(mSongTitle)
                .setContentText(mSongArtist)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFY_ID, builder);


    }

    public int getPosn() {
        return mPlayer.getCurrentPosition();
    }

    public int getDur() {
        return mPlayer.getDuration();
    }

    public boolean isPng() {
        return mPlayer.isPlaying();
    }

    public void pausePlayer() {
        mPlayer.pause();
    }

    public void seek(int posn) {
        mPlayer.seekTo(posn);
    }

    public void go() {
        mPlayer.start();
    }

    public void playPrev() {
        if (mSongs != null) {
            mSongPosition--;
            if (mSongPosition < 0) mSongPosition = mSongs.size() - 1;

            playSong();
        }
    }


    public void playNext() {
        if (mSongs != null) {
            if (setShuffle()) {
                int newSong = mSongPosition;
                while (newSong == mSongPosition) {
                    newSong = mRandom.nextInt(mSongs.size());
                }
                mSongPosition = newSong;
            } else {
                mSongPosition++;
                if (mSongPosition >= mSongs.size()) mSongPosition = 0;
            }
            playSong();
        }
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        stopForeground(true);
    }

    public boolean setShuffle() {

        if (mShuffle) {
            return mShuffle = false;
        } else {
            return mShuffle = true;
        }
    }

    public String getmSongTitle() {
        return mSongTitle;
    }

    public String getmSongArtist() {
        return mSongArtist;
    }

}