package com.example.beethozart

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.beethozart.entities.Artist
import com.example.beethozart.entities.Playlist
import com.example.beethozart.entities.PlaylistWithSongs
import com.example.beethozart.entities.Song


@BindingAdapter("numTracks")
fun TextView.setNumTracks(item: Artist?) {
    item?.let {
        text = item.getNumSongs().toString()
    }
}


@BindingAdapter("playlistName")
fun TextView.setPlaylistName(item: Playlist?) {
    item?.let {
        text = item.playlistName
    }
}


@BindingAdapter("numTracks")
fun TextView.setNumTracks(item: Playlist?) {
    item?.let {
        // text = context.getString(R.string.num_tracks_format, item.getSize())
        text = context.getString(R.string.num_tracks_format, 0)
    }
}

@BindingAdapter("albumCover")
fun ImageView.setAlbumCover(item: Song?) {
    item?.let {
        if (item.artWorkUri != "-1")
            Glide.with(context)
                    .load(Uri.parse(item.artWorkUri))
                    .placeholder(R.drawable.note)
                    .into(this)
    }
}
