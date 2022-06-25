package com.example.beethozart.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beethozart.entities.SongList
import com.example.beethozart.viewmodels.ArtistSongListViewModel
import java.lang.IllegalArgumentException

class ArtistSongListViewModelFactory(
    private val application: Application,
    private val songList: SongList
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtistSongListViewModel::class.java)) {
            return ArtistSongListViewModel(application, songList.toList()) as T
        }

        throw IllegalArgumentException("Unknown view model")
    }
}