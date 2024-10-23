package com.example.gmailappclone.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.gmailappclone.R
import com.example.gmailappclone.workmanager.UploadWorker

class UploadService : Service() {
    private val TAG = "UploadService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fileLink: ArrayList<String>? = intent?.getStringArrayListExtra("fileLink")

        val fileName = intent?.getStringExtra("fileName")
        Log.d(TAG, "onStartCommand: $fileLink $fileName")

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(
                workDataOf(
                    "fileLink" to (fileLink?.toTypedArray() ?: arrayOf()),
                    "fileName" to fileName
                )
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueue(uploadWorkRequest)

        val notification = NotificationCompat.Builder(this, "101")
            .setContentTitle("Uploading the file .")
            .setContentText("Uploading...... $fileName")
            .setSmallIcon(R.drawable.ic_launcher_background) // Use your own drawable
            .build()

        startForeground(1, notification)

        return START_STICKY
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}