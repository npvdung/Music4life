package com.example.beethozart.viewmodels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.example.beethozart.R
import com.example.beethozart.entities.SongList
import com.example.beethozart.databases.SongDatabase
import com.example.beethozart.entities.Playlist
import com.example.beethozart.entities.PlaylistSongCrossRef
import com.example.beethozart.entities.Song
import kotlinx.coroutines.*
import timber.log.Timber


class SongManagerViewModel(application: Application): AndroidViewModel(application) {
    private var songDatabase = SongDatabase.getInstance(application).songDatabaseDao

    val songList = songDatabase.getAllSongs()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song>
        get() = _currentSong

    init {
        _currentSong.value = null

        uiScope.launch {
            initFavoritePlaylist(application)
        }
    }

    private suspend fun initFavoritePlaylist(context: Context) {
        withContext(Dispatchers.IO) {
            songDatabase.insertPlaylist(Playlist(context.getString(R.string.favorite_playlist)))
        }
    }

    fun onSongClicked(song: Song) {
        _currentSong.value = song
    }

    fun onAddToPlaylist(song: Song, context: Context) {
        uiScope.launch {
            val playlistNames = getAllPlaylistNames()

            MaterialDialog(context).show {
                listItems(items = playlistNames) { dialog, index, text ->
                    uiScope.launch {
                        insertPlaylistSongRef(text.toString(), song.songId)
                    }
                }
                negativeButton { R.string.cancel }
            }
        }
    }

    private suspend fun insertPlaylistSongRef(playlistName: String, songId: Long) {
        withContext(Dispatchers.IO) {
            songDatabase.insertPlaylistSongRef(PlaylistSongCrossRef(playlistName, songId))
        }
    }

    private suspend fun getAllPlaylistNames(): List<String> {
        return withContext(Dispatchers.IO) {
            songDatabase.getAllPlaylistNames()
        }
    }

    fun onPlayerNavigated() {
        _currentSong.value = null
    }

    fun getSongList(): SongList {
        return SongList(songList.value!!.toMutableList()).beginWith(_currentSong.value)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}