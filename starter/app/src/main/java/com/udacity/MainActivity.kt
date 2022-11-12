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
            } else {
                Toast.makeText(this@MainActivity, "DONE FAIL", Toast.LENGTH_LONG).show()
                notificationManager.sendNotification(
                    "done",
                    "failed download",
                    this@MainActivity,
                    id,
                    "PICTURE"
                )
            }
        }
    }

    companion object {
        private const val URL =
            "https://p.turbosquid.com/ts-thumb/Kw/Wf9h5Q/4Mak7F9o/1/jpg/1552159621/600x600/fit_q87/253a0549c698f267266e79ae2eba7d7795b239b4/1.jpg"
    }

}
