package com.example.gmailappclone.firebaseUtils

import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.gmailappclone.UtilClassesInstance
import com.example.gmailappclone.services.ReceivingService
import com.example.gmailappclone.services.UploadService
import com.example.gmailappclone.singletons.MailItemGraph
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class SendNotificationService : FirebaseMessagingService() {
    private val TAG = "SendNotificationService"
    private val firestoreHelper = FirestoreHelper()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            val userName = MailItemGraph.personalDetailRepository.getUserName().firstOrNull()
            if (userName!=null){
                firestoreHelper.updateTokenByUsername(
                    userName = userName,
                    newToken = token,
                    onFailure ={
                        Log.d(TAG, "onNewToken: $it")
                    },
                    onSuccess = {
                        Log.d(TAG, "onNewToken: Success")
                    }
                )

            }else{
                Log.d(TAG,"Can't get the user in remote location. Please delete account and recreate or contact our service team")
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.notification?.body
        val title = message.notification?.title

        val currentTimeInMillis =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli()

            }else{
                System.currentTimeMillis()

            }

        val allData = "$data ||| $title ||| $currentTimeInMillis"

        Log.d(TAG, "onMessageReceived: $allData")
        CoroutineScope(Dispatchers.IO).launch {
            val receivingDataIntent = Intent(applicationContext, ReceivingService::class.java)
            receivingDataIntent.putExtra("allReceivedData",allData)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(receivingDataIntent)

            }else{
                startService(receivingDataIntent)
            }

        }
    }
}