package com.viner.musy;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Albums extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Album> mAlbumsList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.albums, container, false);
        mRecyclerView = root.findViewById(R.id.albums_list);
        mAlbumsList = new ArrayList<>();
        getAlbumList();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Collections.sort(mAlbumsList, new Comparator<Album>() {
            public int compare(Album a, Album b) {
                return a.getmTitle().compareTo(b.getmTitle());
            }
        });
        mAdapter = new AlbumAdapter(mAlbumsList, getContext());


        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    public void getAlbumList() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor albumCursor = contentResolver.query(albumUri, null, null, null, null);
        if (albumCursor != null && albumCursor.moveToFirst()) {
            int nameColumn = albumCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int idColumn = albumCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do {
                long thisId = albumCursor.getLong(idColumn);
                String thisName = albumCursor.getString(nameColumn);
                mAlbumsList.add(new Album((int) thisId, thisName));
            } while (albumCursor.moveToNext());
        }
    }

    class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
        private List<Album> mAlbumList;
        private Context mContext;

        public AlbumAdapter(List<Album> mAlbumList, Context mContext) {
            this.mAlbumList = mAlbumList;
            this.mContext = mContext;
        }

        @Override
        public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AlbumAdapter.ViewHolder holder, int position) {
            Album albumItem = mAlbumList.get(position);

            holder.mAlbum.setText(albumItem.getmTitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAlbumList.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mAlbum;


            public ViewHolder(View itemView) {
                super(itemView);
                mAlbum = itemView.findViewById(R.id.albumItemTextView);


            }

        }
    }
}
