package com.dtakac.aux_remote.common.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dtakac.aux_remote.R
import com.dtakac.aux_remote.common.base.resource.ResourceRepository
import com.dtakac.aux_remote.common.model.NowPlayingSong
import com.dtakac.aux_remote.container.activity.ContainerActivity


class NotificationHelperImpl(
    private val context: Context,
    private val resourceRepo: ResourceRepository
): NotificationHelper {
    private val channelId = "aux_channel"
    private val nowPlayingSongNotificationId = 1

    init {
        createNotificationChannel()
    }

    override fun showNowPlayingSongNotification(song: NowPlayingSong){
        val intent = Intent(context, ContainerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_music_note_black_24dp)
            .setContentTitle(song.name)
            .setContentText(resourceRepo.getString(R.string.now_playing))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
        NotificationManagerCompat.from(context).notify(nowPlayingSongNotificationId, builder.build())
    }

    override fun dismissNowPlayingSongNotification() {
        NotificationManagerCompat.from(context).cancel(nowPlayingSongNotificationId)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = resourceRepo.getString(R.string.channel_name)
            val descriptionText = resourceRepo.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = descriptionText
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}