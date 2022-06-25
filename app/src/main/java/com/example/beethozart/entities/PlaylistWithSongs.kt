package com.example.beethozart.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.beethozart.entities.Playlist
import com.example.beethozart.entities.PlaylistSongCrossRef
import com.example.beethozart.entities.Song

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistName",
        entityColumn = "songId",
        associateBy = Junction(PlaylistSongCrossRef::class),
    )
    val songs: List<Song>
)