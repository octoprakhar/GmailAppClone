package com.example.gmailappclone.workmanager

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gmailappclone.singletons.MailItemGraph
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class MailTimestampWorker(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        MailItemGraph.allMailsRepository.getAllMails().collect{mailList->
            mailList.forEach { mailItem ->
                val currentTime = System.currentTimeMillis()
                mailList.forEach { mail->
                    // Convert time string to timestamp in milliseconds
                    val creationTimeMillis = mailItem.time.toLongOrNull() ?: 0L
                    // Check if 24 hours have passed
                    if (currentTime - creationTimeMillis > TimeUnit.HOURS.toMillis(24)) {
                        // Format as date
                        val date = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(creationTimeMillis),
                            ZoneId.of("Asia/Kolkata") // Change to your timezone if needed
                        ).toLocalDate().toString() // Get date format


                        // Update the mail item in your database or perform other actions
                        // Example: Update the item to indicate it should be shown as a date
                        // mailLayoutItemDao.updateMailItem(mailItem.copy(displayFormat = date))
                    } else {
                        // Format as time (HH:mm)
                        val time = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(creationTimeMillis),
                            ZoneId.of("Asia/Kolkata")
                        ).toLocalTime().toString() // Get time format

                        // Update the mail item in your database or perform other actions
                        // Example: Update the item to indicate it should be shown as a time
                        // mailLayoutItemDao.updateMailItem(mailItem.copy(displayFormat = time))
                    }
                }

        }

    }
}