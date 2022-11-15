package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.utils.DownloadUtils
import com.udacity.utils.createChannel
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.createChannel(
            this,
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.changeButtonState(ButtonState.Loading)
            Toast.makeText(this@MainActivity, "Download started", Toast.LENGTH_SHORT).show()
            DownloadUtils.download(this, URL)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (DownloadUtils.isDownloadSuccess(this@MainActivity, id!!)) {
                Toast.makeText(this@MainActivity, "DONE SUCCESS", Toast.LENGTH_LONG).show()
                notificationManager.sendNotification(
                    "done",
                    "downloaded successfully",
                    this@MainActivity,
                    id,
                    "PICTURE"
                )
                custom_button.changeButtonState(ButtonState.Completed)
            } else {
                Toast.makeText(this@MainActivity, "DONE FAIL", Toast.LENGTH_LONG).show()
                notificationManager.sendNotification(
                    "done",
                    "failed download",
                    this@MainActivity,
                    id,
                    "PICTURE"
                )
                custom_button.changeButtonState(ButtonState.Completed)
            }
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
    }

}
