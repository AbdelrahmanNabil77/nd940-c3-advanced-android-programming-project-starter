package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(
    messageTitle: String, messageBody: String, applicationContext: Context,
    downloadId: Long, fileName:String
) {
    // Create the content intent for the notification, which launches this activity
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(Constants.DOWNLOAD_ID,downloadId)
    contentIntent.putExtra(Constants.FILE_NAME,fileName)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )
        .setSmallIcon(R.drawable.ic_baseline_arrow_downward_24)
        .setContentTitle(messageTitle)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .addAction(R.drawable.ic_baseline_arrow_downward_24,
        "check the status",
        contentPendingIntent)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.createChannel(context: Context, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.enableVibration(true)

        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)

    }
}