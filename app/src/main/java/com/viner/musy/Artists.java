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


public class Artists extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Artist> mArtistList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.artists, container, false);
        mRecyclerView = root.findViewById(R.id.artist_list);
        mArtistList = new ArrayList<>();
        getArtistList();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Collections.sort(mArtistList, new Comparator<Artist>() {
            public int compare(Artist a, Artist b) {
                return a.getmName().compareTo(b.getmName());
            }
        });
        mAdapter = new ArtistAdapter(mArtistList, getContext());


        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    public void getArtistList() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor artistCursor = contentResolver.query(artistUri, null, null, null, null);
        if (artistCursor != null && artistCursor.moveToFirst()) {
            int nameColumn = artistCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int idColumn = artistCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            do {
                long thisId = artistCursor.getLong(idColumn);
                String thisName = artistCursor.getString(nameColumn);
                mArtistList.add(new Artist((int) thisId, thisName));
            } while (artistCursor.moveToNext());
        }
    }

    class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {
        private List<Artist> mArtistList;
        private Context mContext;

        public ArtistAdapter(List<Artist> mArtistList, Context mContext) {
            this.mArtistList = mArtistList;
            this.mContext = mContext;
        }

        @Override
        public ArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ArtistAdapter.ViewHolder holder, int position) {
            Artist artistItem = mArtistList.get(position);

            holder.mArtist.setText(artistItem.getmName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //TODO
                }
            });
        }

        @Override
        public int getItemCount() {
            return mArtistList.size();
        }

        protected class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mArtist;


            public ViewHolder(View itemView) {
                super(itemView);
                mArtist = itemView.findViewById(R.id.artistItemTextView);


            }

        }
    }
}
