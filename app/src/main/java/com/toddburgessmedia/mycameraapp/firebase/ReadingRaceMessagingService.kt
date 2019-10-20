package com.toddburgessmedia.mycameraapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.toddburgessmedia.mycameraapp.R

class ReadingRaceMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val channelID = this.getString(R.string.default_notification_channel_id)
        val title = message?.notification?.title

        val channel = NotificationChannel(channelID,title,NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = message?.notification?.body

        val builder = NotificationCompat.Builder(this,channelID)
            .setContentTitle(title)
            .setContentText(message?.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)

        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(1, builder.build())

    }
}