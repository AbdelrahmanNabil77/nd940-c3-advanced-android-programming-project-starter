package com.udacity.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import com.udacity.R

object DownloadUtils {
    fun isDownloadSuccess(context: Context, id: Long): Boolean {
        var isSuccess = false
        //DownloadManager.Query() is used to filter DownloadManager queries
        val query = DownloadManager.Query()

        query.setFilterById(id!!)
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

            when (status) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    isSuccess = true
                }
                DownloadManager.STATUS_FAILED -> {
                    isSuccess = false
                }
            }

        } else {
            isSuccess = false
        }
        return isSuccess
    }

    fun download(context: Context,url:String):Long {
        var downloadID:Long=0L
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(context.getString(R.string.app_name))
                .setDescription(context.getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        return downloadID
    }
}