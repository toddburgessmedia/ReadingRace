package com.toddburgessmedia.mycameraapp.firebase

import android.content.Context
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.toddburgessmedia.mycameraapp.R

class ReadingRaceMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)

        Log.d("mycamera","we got a message from Firebase!!")

        val builder = NotificationCompat.Builder(this,"channel_id")
            .setContentTitle(message?.notification?.title)
            .setContentText(message?.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle())
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)

        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE)
        when (notificationManager) {
            is NotificationManagerCompat -> {
                notificationManager.notify(0, builder.build())
            }
        }
    }
}