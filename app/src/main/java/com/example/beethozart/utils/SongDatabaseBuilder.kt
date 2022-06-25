package com.example.beethozart.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.beethozart.databases.SongDatabase
import com.example.beethozart.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SongDatabaseBuilder(private val context: Context) {

    suspend fun build() {
        withContext(Dispatchers.IO) {
            val songDatabase = SongDatabase.getInstance(context).songDatabaseDao
            songDatabase.clear()

            val projection = arrayOf(
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Media.ALBUM_ID,
                    MediaStore.Audio.ArtistColumns.ARTIST,
            )

            val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
            val uriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val artWorkUriExternal = Uri.parse("content://media/external/audio/albumart")
            val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"
            val audioCursor = context.contentResolver.query(
                    uriExternal, projection, selection, null, sortOrder
            )

            if (audioCursor != null && audioCursor.moveToFirst()) {
                while (audioCursor.moveToNext()) {
                    val idIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val titleIndex =
                            audioCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                    val albumIndex =
                            audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
                    val artistIndex =
                            audioCursor.getColumnIndexOrThrow(MediaStore.Audio.ArtistColumns.ARTIST)
                    val albumIdIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

                    val id = audioCursor.getString(idIndex)
                    val albumId = audioCursor.getString(albumIdIndex)

                    val song = Song(
                            id.toLong(),
                            audioCursor.getString(titleIndex),
                            audioCursor.getString(artistIndex),
                            audioCursor.getString(albumIndex),
                            Uri.withAppendedPath(uriExternal, "" + id).toString(),
                            Uri.withAppendedPath(artWorkUriExternal, "" + albumId).toString()
                    )

                    songDatabase.insert(song)
                }

                audioCursor.close()
            }
        }
    }
}