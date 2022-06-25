package com.example.beethozart.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.beethozart.R
import com.example.beethozart.databases.SongDatabase
import com.example.beethozart.entities.PlaylistSongCrossRef
import com.example.beethozart.entities.Song
import com.example.beethozart.entities.SongList
import com.example.beethozart.services.MusicPlayerService
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.*
import java.lang.Runnable
import kotlin.math.max
import kotlin.math.min


class PlayerViewModel(application: Application): AndroidViewModel(application) {
    private var songDatabase = SongDatabase.getInstance(application).songDatabaseDao
    val playlistList = songDatabase.getAllPlaylists()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    lateinit var musicPlayerServiceBinder: MusicPlayerService.MusicPlayerServiceBinder
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()

    private val _currentPosition = MutableLiveData<Float>()
    val currentPosition: LiveData<Float>
      get() = _currentPosition

    private val _isAttachedToPlayerFragment = MutableLiveData<Boolean>()
    val isAttachedToPlayerFragment: LiveData<Boolean>
      get() = _isAttachedToPlayerFragment

    private val _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song>
      get() = _currentSong

    val currentTitle: LiveData<String> = Transformations.switchMap(currentSong) {
        MutableLiveData(it.title)
    }

    var isPlaying = false

    private var repeatMode = Player.REPEAT_MODE_OFF
    private var shuffleModeEnable = false

    var player : SimpleExoPlayer? = null

    fun attachToPlayerFragment() {
        _isAttachedToPlayerFragment.value = true
    }

    fun detachToPlayerFragment() {
        _isAttachedToPlayerFragment.value = false
    }

    var songList: SongList? = null

    fun playSongList(songList: SongList) {
        this.songList = songList

        player = musicPlayerServiceBinder.playSongList(songList, this)
        isPlaying = true
        runnable = Runnable {
            _currentPosition.value = max(0.0001f, player!!.currentPosition.toFloat() / player!!.duration)
            _currentPosition.value = min(0.99f, _currentPosition.value!!)
            handler.postDelayed(runnable, 100)
        }
        handler.postDelayed(runnable, 100)
    }

    fun setCurrentSong(song: Song) {
        _currentSong.value = song
    }

    fun onGoNext() {
        player?.next()
    }

    fun onGoPrev() {
        player?.previous()
    }

    fun onPause() {
        player?.playWhenReady = !player!!.isPlaying
    }

    fun onSeek(timestamp: Float) {
        player?.seekTo((timestamp * player!!.duration).toLong())
    }

    fun onSetRepeatMode() {
        when (repeatMode) {
            Player.REPEAT_MODE_OFF -> repeatMode = Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> repeatMode = Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> repeatMode = Player.REPEAT_MODE_OFF
        }

        musicPlayerServiceBinder.setRepeatMode(repeatMode)
    }

    fun onSetShuffleMode() {
        shuffleModeEnable = when (shuffleModeEnable) {
            false -> true
            true -> false
        }

        musicPlayerServiceBinder.setShuffleMode(shuffleModeEnable)
    }

    fun onShareSong(context: Context, song: Song?) {
        song?.let {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "audio/*"

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(song.uri))

            context.startActivity(Intent.createChooser(shareIntent, "Share a song"))
        }
    }

    fun addCurrentSongToFavorite() {
         uiScope.launch {
             _currentSong.value?.let {
                 val context = getApplication<Application>().applicationContext
                 val favoritePlaylistName = context.getString(R.string.favorite_playlist)
                 insertPlaylistSongRef(
                     favoritePlaylistName,
                     it.songId
                 )

                 Toast.makeText(context, "Added the current song to $favoritePlaylistName", Toast.LENGTH_SHORT).show()
             }
         }
    }

    private suspend fun insertPlaylistSongRef(playlistName: String, songId: Long) {
        withContext(Dispatchers.IO) {
            songDatabase.insertPlaylistSongRef(PlaylistSongCrossRef(playlistName, songId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}