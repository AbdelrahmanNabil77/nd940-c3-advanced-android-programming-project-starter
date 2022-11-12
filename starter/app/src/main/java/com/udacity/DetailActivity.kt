package com.udacity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.utils.Constants
import com.udacity.utils.DownloadUtils
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        Log.d(
            "downloadDetails", "FILE NAME: ${intent.getStringExtra(Constants.FILE_NAME)} \n" +
                    "status: ${
                        DownloadUtils.isDownloadSuccess(
                            this,
                            intent.getLongExtra(Constants.DOWNLOAD_ID, -1)
                        )
                    }"
        )
    }

}
