package com.example.beethozart.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class Playlist(
    @PrimaryKey
    val playlistName: String = "Unknown",

) {
}