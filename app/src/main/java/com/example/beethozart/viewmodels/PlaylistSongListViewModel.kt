package com.example.beethozart.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beethozart.entities.Song
import com.example.beethozart.entities.SongList

class PlaylistSongListViewModel(application: Application, songList: List<Song>): AndroidViewModel(application) {
    private val _songList = MutableLiveData<List<Song>>()
    val songList: LiveData<List<Song>>
        get() = _songList

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song>
        get() = _currentSong

    init {
        _songList.value = songList
    }

    fun onSongClicked(song: Song) {
        _currentSong.value = song
    }

    fun onPlayerNavigated() {
        _currentSong.value = null
    }

    fun getSongList(): SongList {
        return SongList(songList.value!!.toMutableList())
    }
}