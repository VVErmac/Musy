package com.viner.musy;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class AudioController extends AppCompatActivity {

    private MusicService mMusicService;
    private Intent mPlayIntent;

    private boolean mMusicBound = false;
    private TextView mSongTitleTextView, mArtistTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_controller);

        mSongTitleTextView = findViewById(R.id.songTitleTextView);
        mArtistTextView = findViewById(R.id.artistTextView);



        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MusicService.class);
            getApplicationContext().bindService(mPlayIntent, musicConnection, BIND_AUTO_CREATE);
            //      startService(mPlayIntent);
        }
    }

    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.playPauseButton:
                if (mMusicService.isPng()) {
                    mMusicService.pausePlayer();
                } else {
                    mMusicService.go();
                }
                break;
            case R.id.reverseButton:
                mMusicService.playPrev();
                mSongTitleTextView.setText(mMusicService.getmSongTitle());
                mArtistTextView.setText(mMusicService.getmSongArtist());
                break;
            case R.id.forwardButton:
                mMusicService.playNext();
                mSongTitleTextView.setText(mMusicService.getmSongTitle());
                mArtistTextView.setText(mMusicService.getmSongArtist());
                break;
            case R.id.shuffleButton:
                mMusicService.setShuffle();
                Toast.makeText(this, "Shuffled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mMusicService = binder.getService();
            mSongTitleTextView.setText(mMusicService.getmSongTitle());
            mArtistTextView.setText(mMusicService.getmSongArtist());
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicBound = false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                mMusicService.onDestroy();
                stopService(new Intent(this, MusicService.class));
                finishAffinity();
                break;
            case R.id.about:
                startActivity(new Intent(this, About.class));
                break;
        }

        return super.onOptionsItemSelected(item);

    }

}
