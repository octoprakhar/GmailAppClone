package com.example.gmailappclone

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.gmailappclone.singletons.MailItemGraph

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MailItemGraph.init(this)
        MailItemGraph.personalDetailInit(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Upload Notification"
            val descriptionText = "This notification category belongs to every notification send at time of upload"
            val importanceLevel = NotificationManager.IMPORTANCE_HIGH
            val uploadChannel = NotificationChannel("101",name,importanceLevel)
            uploadChannel.description = descriptionText

            val savingChannel = NotificationChannel("102","Saving Notification",importanceLevel)
            savingChannel.description = "This notification category belongs to every notification send at time of saving"

            val receivingDataChannel = NotificationChannel("103","Recieving Data Notification",importanceLevel)
            receivingDataChannel.description = "This notification category belongs to every notification send at time of receiving the data from sender."

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(uploadChannel)
            notificationManager.createNotificationChannel(savingChannel)
            notificationManager.createNotificationChannel(receivingDataChannel)


        }
    }
}