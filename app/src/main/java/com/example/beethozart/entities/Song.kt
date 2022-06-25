package com.example.beethozart.entities

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "all_song_table")

@Parcelize
data class Song(
        @PrimaryKey(autoGenerate = true)
        var songId: Long = 0L,

        @ColumnInfo(name = "title")
        val title: String = "Unknown",

        @ColumnInfo(name = "artist")
        val artist: String = "Unknown",

        @ColumnInfo(name = "album")
        val album: String = "Unknown",

        @ColumnInfo(name = "uri")
        val uri: String = "0",

        @ColumnInfo(name = "artWorkUri")
        val artWorkUri: String = "-1"
): Parcelable