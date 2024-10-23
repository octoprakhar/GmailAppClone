package com.example.gmailappclone.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.workDataOf
import com.example.gmailappclone.R
import com.example.gmailappclone.workmanager.ReceivingWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReceivingService : Service() {

    private val TAG = "ReceivingService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val allReceivedData = intent?.getStringExtra("allReceivedData")
        Log.d(TAG, "onStartCommand of ReceivingService: $allReceivedData")

        val receivingWorkRequest = OneTimeWorkRequestBuilder<ReceivingWorker>()
            .setInputData(workDataOf("allReceivedData" to allReceivedData))
            .build()


        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("receivingWork", ExistingWorkPolicy.KEEP, receivingWorkRequest)
        Log.d(TAG, "Enqueued ReceivingWorker")

        val notification = NotificationCompat.Builder(this, "103")
            .setContentTitle("Uploading the file .")
            .setContentText("Uploading...... $allReceivedData")
            .setSmallIcon(R.drawable.ic_launcher_background) // Use your own drawable
            .build()

        startForeground(2, notification)

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}