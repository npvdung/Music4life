package com.example.beethozart.entities

import androidx.room.Entity

@Entity(primaryKeys = ["playlistName", "songId"], tableName = "playlist_song_cross_ref")
data class PlaylistSongCrossRef(
    val playlistName: String,
    val songId: Long
)