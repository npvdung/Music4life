package com.example.beethozart.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.example.beethozart.MainActivity
import com.example.beethozart.R
import com.example.beethozart.entities.Song
import com.example.beethozart.services.MusicPlayerService


class MusicPlayerNotificationBuilder(
    private val service: MusicPlayerService,
    private val sessionToken: MediaSessionCompat.Token
) {
    private var pendingIntent: PendingIntent = Intent(service, MainActivity::class.java)
            .let { notificationIntent ->
        PendingIntent.getBroadcast(
            service,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    private var notificationChannelCreated = false

    private fun createPendingIntent(actionName: String): PendingIntent {
        val intent = Intent().apply {
            action = ACTION_NOTIFICATION_PLAYER
            putExtra(NOTIFICATION_ACTION_NAME, actionName)
            // putExtra(EXTRA_NOTIFICATION_ID, 0)
        }

        val requestCode = when(actionName) {
            ACTION_PAUSE -> 1
            ACTION_NEXT -> 2
            ACTION_PREV -> 3
            else -> 4
        }

        return PendingIntent.getBroadcast(
            service,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun build(song: Song): Notification {
        if (!notificationChannelCreated)
            createNotificationChannel()
        val pausePendingIntent = createPendingIntent(ACTION_PAUSE)
        val nextPendingIntent = createPendingIntent(ACTION_NEXT)
        val prevPendingIntent = createPendingIntent(ACTION_PREV)

        return NotificationCompat.Builder(service, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_audiotrack_24px)
            .setLargeIcon(BitmapFactory.decodeResource(service.resources, R.drawable.note))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(song.title)
            .setContentText(song.album)
            .setContentIntent(pendingIntent)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_skip_previous_24dp, "skip_prev", prevPendingIntent)
            .addAction(R.drawable.ic_pause_24dp, "pause", pausePendingIntent)
            .addAction(R.drawable.ic_skip_next_24dp, "skip_next", nextPendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // val name = context.getString(R.string.channel_name)
            // val descriptionText = context.getString(R.string.channel_description)
            val name = "name"
            val descriptionText = "desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                    service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationChannelCreated = true
        }
    }

    companion object {
        const val CHANNEL_ID = "channel"
        const val ONGOING_NOTIFICATION_ID = 1
        const val ACTION_NOTIFICATION_PLAYER = "NOTIFICATION_PLAYER"
        const val NOTIFICATION_ACTION_NAME = "notification_action_name"
        const val ACTION_PAUSE = "pause"
        const val ACTION_NEXT = "next"
        const val ACTION_PREV = "prev"
    }
}