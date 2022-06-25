package com.example.beethozart.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beethozart.databases.SongDatabase
import com.example.beethozart.entities.Artist
import com.example.beethozart.entities.Song
import timber.log.Timber

class ArtistManagerViewModel(application: Application): AndroidViewModel(application) {
    private var songDatabase = SongDatabase.getInstance(application).songDatabaseDao

    var songList: LiveData<List<Song>> = songDatabase.getAllSongs()

    private val _artistList = MutableLiveData<List<Artist>>()

    private val _clickedArtist = MutableLiveData<Artist>()
    val clickedArtist: LiveData<Artist>
      get() = _clickedArtist

    val artistList: LiveData<List<Artist>>
        get() = _artistList

    fun createArtistList(songList: List<Song>) {
        _artistList.value = listOf()
        val artistMap = HashMap<String, Artist>()

        for (song in songList) {
            if (artistMap.containsKey(song.artist)) {
                artistMap[song.artist]!!.addSong(song)
            }
            else {
                artistMap[song.artist] = Artist(song.artist, mutableListOf(song))
            }
        }
        Timber.i("Number of artists: ${artistMap.size}")

        _artistList.value = artistMap.values.toList()
    }

    fun onArtistClicked(artist: Artist) {
        Timber.i("clicked artist: ${artist.artistName}")
        _clickedArtist.value = artist
    }

    fun onArtistSongListNavigated() {
        _clickedArtist.value = null
    }
}