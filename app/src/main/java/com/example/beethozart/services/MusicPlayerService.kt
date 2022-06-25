package com.example.beethozart.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.example.beethozart.entities.Song
import com.example.beethozart.entities.SongList
import com.example.beethozart.notification.MusicPlayerNotificationBuilder
import com.example.beethozart.viewmodels.PlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueEditor
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import timber.log.Timber

class MusicPlayerService: Service() {

    private var player: SimpleExoPlayer? = null

    private val binder = MusicPlayerServiceBinder()
    private val notificationBroadcastReceiver = NotificationBroadcastReceiver()
    val musicPlayerNotificationBuilder by lazy {
        MusicPlayerNotificationBuilder(this, mediaSessionCompat.sessionToken)
    }

    private lateinit var mediaSessionCompat : MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    inner class MusicPlayerServiceBinder : Binder() {
        fun playSongList(songList: SongList, playerViewModel: PlayerViewModel): SimpleExoPlayer {
            player?.release()

            val context = this@MusicPlayerService

            player = SimpleExoPlayer.Builder(context).build()

            for (x in 0 until songList.size) {
                player!!.addMediaItem(MediaItem.fromUri(songList[x].uri))
            }

            player!!.prepare()
            player!!.play()

            startForeground(
                    MusicPlayerNotificationBuilder.ONGOING_NOTIFICATION_ID,
                    musicPlayerNotificationBuilder
                            .build(songList[player!!.currentWindowIndex])
            )
            playerViewModel.setCurrentSong(songList[player!!.currentWindowIndex])

            player!!.addListener(object : Player.EventListener {
                override fun onPositionDiscontinuity(reason: Int) {
                    super.onPositionDiscontinuity(reason)

                    startForeground(
                            MusicPlayerNotificationBuilder.ONGOING_NOTIFICATION_ID,
                            musicPlayerNotificationBuilder
                                    .build(songList[player!!.currentWindowIndex])
                    )

                    playerViewModel.setCurrentSong(songList[player!!.currentWindowIndex])
                }
            })

            return player!!
        }

        fun setRepeatMode(mode: Int) {
            player!!.repeatMode = mode
        }

        fun setShuffleMode(enable: Boolean) {
            player!!.shuffleModeEnabled = enable
        }
    }

    inner class NotificationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.extras!!.getString(
                    MusicPlayerNotificationBuilder.NOTIFICATION_ACTION_NAME)
            ) {
                MusicPlayerNotificationBuilder.ACTION_PREV ->
                    this@MusicPlayerService.player?.previous()
                MusicPlayerNotificationBuilder.ACTION_PAUSE ->
                    player?.playWhenReady = !player!!.isPlaying
                MusicPlayerNotificationBuilder.ACTION_NEXT ->
                    this@MusicPlayerService.player?.next()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val intentFilter = IntentFilter(MusicPlayerNotificationBuilder.ACTION_NOTIFICATION_PLAYER)
        registerReceiver(notificationBroadcastReceiver, intentFilter)

        mediaSessionCompat = MediaSessionCompat(this, "Beethozart")
        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
        mediaSessionConnector.setPlayer(player)

        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSessionCompat) {
            override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
                return MediaDescriptionCompat.Builder()
                        .setTitle("MediaDescription title")
                        .setDescription("MediaDescription description for $windowIndex")
                        .setSubtitle("MediaDescription subtitle")
                        .build();
            }
        })
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationBroadcastReceiver)
        player?.release()

        mediaSessionCompat.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaSessionCompat.isActive = true
        return START_STICKY
    }
}