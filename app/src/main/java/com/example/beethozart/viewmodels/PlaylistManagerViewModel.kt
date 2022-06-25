package com.example.beethozart.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.example.beethozart.R
import com.example.beethozart.databases.SongDatabase
import com.example.beethozart.databases.daos.SongDatabaseDao
import com.example.beethozart.entities.Playlist
import com.example.beethozart.entities.PlaylistWithSongs
import com.example.beethozart.entities.Song
import com.google.android.material.dialog.MaterialDialogs
import kotlinx.coroutines.*
import timber.log.Timber

class PlaylistViewModel(application: Application): AndroidViewModel(application) {
    private var songDatabase = SongDatabase.getInstance(application).songDatabaseDao

    val playlistList = songDatabase.getAllPlaylists()

    private var _clickedPlaylist = MutableLiveData<Playlist>()
    val clickedPlaylist: LiveData<Playlist>
      get() = _clickedPlaylist

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private suspend fun addPlaylist(playlistName: String) {
        withContext(Dispatchers.IO) {
            songDatabase.insertPlaylist(Playlist(playlistName))
        }
    }

    fun onAddPlaylist(context: Context) {
        MaterialDialog(context).show {
            title(text = context.getString(R.string.playlist_name_dialog_title))
            input(hintRes = R.string.playlist_name_hint) { _, charSequence ->
                uiScope.launch {
                    addPlaylist(charSequence.toString())
                }
            }
            positiveButton { R.string.submit }
            negativeButton { R.string.cancel }
        }
    }

    fun onPlaylistClicked(playlist: Playlist) {
        _clickedPlaylist.value = playlist
    }

    suspend fun getSongOf(playlist: Playlist): MutableList<Song> {
        return withContext(Dispatchers.IO) {
            val playlistWithSongs = songDatabase.getPlaylistWithSongs(playlist.playlistName)
            playlistWithSongs!!.songs.toMutableList()
        }
    }

    fun onPlaylistSongListNavigated() {
        _clickedPlaylist.value = null
    }
}