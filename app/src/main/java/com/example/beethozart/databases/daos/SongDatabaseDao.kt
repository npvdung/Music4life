package com.example.beethozart.databases.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.beethozart.entities.*

@Dao
interface SongDatabaseDao {
    @Insert
    fun insert(song: Song)

    @Update
    fun update(song: Song)

    @Query("SELECT * from all_song_table WHERE songID = :key")
    fun get(key: Long): Song?

    @Query("SELECT * from all_song_table ORDER BY songId DESC")
    fun getAllSongs(): LiveData<List<Song>>

    @Query("DELETE FROM all_song_table")
    fun clear()

    @Insert
    fun insertUser(user: User)

    @Query("DELETE FROM sign_in_user")
    fun deleteUser()

    @Query("SELECT * FROM sign_in_user")
    fun getUser(): LiveData<List<User>>

    @Transaction
    @Query("SELECT * FROM PLAYLIST_TABLE WHERE playlistName = :playlistName")
    fun getPlaylistWithSongs(playlistName: String): PlaylistWithSongs?

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): LiveData<List<Playlist>>

    @Query("SELECT playlistName FROM playlist_table")
    fun getAllPlaylistNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylist(playlist: Playlist)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlaylistSongRef(playlistSongRef: PlaylistSongCrossRef)
}