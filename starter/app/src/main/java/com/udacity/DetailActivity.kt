package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.utils.Constants
import com.udacity.utils.DownloadUtils
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        cancelNotification()
        val filename: String = intent.getStringExtra(Constants.FILE_NAME) ?: ""
        val status = DownloadUtils.isDownloadSuccess(
            this, intent.getLongExtra(Constants.DOWNLOAD_ID, -1)
        )
        tv_file_name.text = filename
        handleDownloadStatus(status)
        ok_button.setOnClickListener {
            finish()
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun handleDownloadStatus(isSuccess: Boolean) {
        if (isSuccess) {
            tv_status.text = "Success"
            tv_status.setTextColor(getColor(R.color.colorPrimaryDark))
        } else {
            tv_status.text = "Failed"
            tv_status.setTextColor(getColor(R.color.red))
        }
    }

    fun cancelNotification(){
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
    }

}
