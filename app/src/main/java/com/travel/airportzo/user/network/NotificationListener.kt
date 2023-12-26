package com.travel.airportzo.user.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.travel.airportzo.user.R
import com.travel.airportzo.user.savedpreference.SavedSharedPreference
import com.travel.airportzo.user.ui.activity.MainActivity
import com.travel.airportzo.user.utils.Utility

class NotificationListener: FirebaseMessagingService() {
    private var requestID = 0
    override fun onNewToken(token: String) {
        Log.d("deviceToken", "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        SavedSharedPreference.setUserData(
            this, "", "",
            "",
            "", "", "", ""
        )

        Utility.clearBackStackAndStartActivity(applicationContext, MainActivity::class.java)

        Log.d("NotificationMessage", message.notification?.title.toString())
        Log.d("NotificationMessage", message.notification?.body.toString())
        Log.d("NotificationMessage", message.toString())

        val builder = NotificationCompat.Builder(this, resources.getString(R.string.app))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message.notification?.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(requestID++, builder.build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "YourChannelId",
                "YourChannelName",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, "YourChannelId")
                .setContentTitle("Your Service is Running")
                .setContentText("Your service is currently active.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()

            startForeground(1, notification)
        }
    }
}