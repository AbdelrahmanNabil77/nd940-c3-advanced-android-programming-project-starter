package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.udacity.utils.DownloadUtils
import com.udacity.utils.createChannel
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var chosenBtn: Long = 0

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
            if (isBtnChosen()) {
                custom_button.changeButtonState(ButtonState.Loading)
                DownloadUtils.download(this, getChosenLink())
            } else {
                Toast.makeText(this@MainActivity, "Choose an option first", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (DownloadUtils.isDownloadSuccess(this@MainActivity, id!!)) {
                notificationManager.sendNotification(
                    "done",
                    "downloaded successfully",
                    this@MainActivity,
                    id,
                    "${getChosenTitle()}"
                )
                custom_button.changeButtonState(ButtonState.Completed)
            } else {
                notificationManager.sendNotification(
                    "fail",
                    "failed download",
                    this@MainActivity,
                    id,
                    "${getChosenTitle()}"
                )
                custom_button.changeButtonState(ButtonState.Completed)
            }
        }
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"

    }

    fun isBtnChosen(): Boolean {
        return rg_download_choices.checkedRadioButtonId != -1
    }

    fun getChosenLink(): String {
        var link = ""
        when (rg_download_choices.checkedRadioButtonId) {
            rb_glide.id -> link = GLIDE_URL
            rb_retrofit.id -> link = RETROFIT_URL
            rb_load_app.id -> link = UDACITY_URL
        }
        return link
    }

    fun getChosenTitle(): String {
        val btnTxt =
            findViewById<RadioButton>(rg_download_choices.checkedRadioButtonId).text.toString()
        return btnTxt
    }
}
