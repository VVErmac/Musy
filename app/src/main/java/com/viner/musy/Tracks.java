package com.viner.musy;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Tracks extends Fragment implements MediaController.MediaPlayerControl {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Song> mListSongs;
    private MusicService mMusicService;
    private boolean mMusicBound = false;
    private Intent mPlayIntent;
    private boolean mPaused = false, mPlaybackPaused = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_songs, container, false);
        mMusicService = new MusicService();
        mRecyclerView = root.findViewById(R.id.list_song);
        mListSongs = new ArrayList<>();
        getSongList();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Collections.sort(mListSongs, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getmTitle().compareTo(b.getmTitle());
            }
        });
        mAdapter = new SongAdapter(mListSongs, getContext());


        mRecyclerView.setAdapter(mAdapter);
        root.findViewById(R.id.prevTracksFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicService.playPrev();
            }
        });
        root.findViewById(R.id.shuffleTracksFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicService.setShuffle();
            }
        });
        root.findViewById(R.id.playTracksFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMusicService.isPng()) {
                    mMusicService.pausePlayer();
                } else {
                    mMusicService.go();
                }
            }
        });
        root.findViewById(R.id.nextTracksFragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicService.playNext();
            }
        });
        return root;

    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mMusicService = binder.getService();
            mMusicService.setList(mListSongs);
            mMusicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicBound = false;
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(getContext(), MusicService.class);
            getActivity().getApplicationContext().bindService(mPlayIntent, musicConnection, getActivity().BIND_AUTO_CREATE);
            getActivity().startService(mPlayIntent);
        }
    }

    public void songPicked(int i) {
        mMusicService.setSong(i);
        mMusicService.playSong();
        if (mPlaybackPaused) {
            mPlaybackPaused = true;
        }
    }


    public void getSongList() {

        ContentResolver musicResolver = getContext().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                mListSongs.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    public void start() {
        mMusicService.go();
    }

    @Override
    public void pause() {
        mPlaybackPaused = true;
        mMusicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (mMusicService != null && mMusicBound && mMusicService.isPng()) {
            return mMusicService.getDur();
        } else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mMusicService != null && mMusicBound && mMusicService.isPng()) {
            return mMusicService.getPosn();
        } else return 0;
    }

    @Override
    public void seekTo(int position) {
        mMusicService.seek(position);
    }

    @Override
    public boolean isPlaying() {
        return mMusicBound && mMusicService != null && mMusicService.isPng();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }



    @Override
    public void onDestroy() {
        getContext().stopService(mPlayIntent);
        mMusicService = null;
        super.onDestroy();
    }

    class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
        private List<Song> mSongList;
        private Context mContext;

        public SongAdapter(List<Song> songList, Context context) {
            this.mSongList = songList;
            this.mContext = context;
        }

        @Override
        public SongAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);


            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final SongAdapter.ViewHolder holder, final int position) {
            Song listItem = mSongList.get(position);

            holder.mTitle.setText(listItem.getmTitle());
            holder.mArtist.setText(listItem.getmArtist());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    songPicked(position);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mSongList.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTitle;
            public TextView mArtist;


            public ViewHolder(View itemView) {
                super(itemView);
                mTitle = itemView.findViewById(R.id.trackTitleTextView);
                mArtist = itemView.findViewById(R.id.nameArtistTextView);


            }
        }
    }

}

