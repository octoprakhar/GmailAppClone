package com.example.gmailappclone.workmanager

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gmailappclone.R
import com.example.gmailappclone.UtilClassesInstance
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.dataclasses.MessageState
import com.example.gmailappclone.dataclasses.ReceivedMessageState
import com.example.gmailappclone.firebaseUtils.FirebaseStorageHelper
import com.example.gmailappclone.singletons.MailItemGraph
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class ReceivingWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val TAG = "ReceivingWorker"
    val firebaseStorageHelper = FirebaseStorageHelper()
    override suspend fun doWork(): Result {
        val allReceivedData = inputData.getString("allReceivedData")
        val linkNames = mutableListOf<String>()
        Log.d(TAG, "doWork of ReceivingWorker: $allReceivedData")

        if (allReceivedData == null) {
            Log.e(TAG, "Failed to retrieve input data")
            return Result.failure()
        }
        val context = applicationContext


        try {
            Log.d(TAG, "doWork of ReceivingWorker: $allReceivedData")
            showNotification(context,"Success", "Received data: $allReceivedData")
            val allDataList : List<String> = allReceivedData.split("|||")
            Log.d(TAG, "doWork of ReceivingWorkers data list: $allDataList")
            val downloadableLinkList = allDataList[2].split("*||*||*")
            downloadableLinkList.forEachIndexed { index, s ->
                linkNames.add("${Uri.parse(s).lastPathSegment?.substringAfterLast(" % 2F")}")
            }

            if (downloadableLinkList.all { it.isNotBlank() && it.isNotEmpty() }){
                Log.d(TAG,"Before sending list to saving and downloading image function $downloadableLinkList")
                val fileUri = savingAndDownloadingImage(context, downloadableLinkList)
                val fileUriListAsString = fileUri?.joinToString("*||*||*")
                if (fileUri != null && fileUri.isNotEmpty()){
                    Log.d(TAG, "Got file uri as : $fileUri")
                    val receivedMessage = ReceivedMessageState(
                        from = allDataList[0],
                        subject = allDataList[4],
                        fileLink = fileUriListAsString,
                        text = allDataList[1],
                        timeStamp = allDataList[5],
                        to = "avdone",
                        label = allDataList[3]

                    )

                    val changeToMessageState = MailLayoutItem(
                        sender = receivedMessage.from ?: "No sender found",
                        receiver = receivedMessage.to ?: "No receiver found",
                        content = receivedMessage.text ?: "No content found",
                        subject = receivedMessage.subject ?: "No subject found",
                        time = receivedMessage.timeStamp ?: "No time found",
                        fileLink = fileUriListAsString,
                        isReceived = true,
                        label = receivedMessage.label
                    )

                    MailItemGraph.allMailsRepository.insertMail(changeToMessageState)
                    linkNames.forEach {name->
                        firebaseStorageHelper.deleteImageFromStorage(
                            fileName = name,
                            onSuccess = {
                                Log.d(TAG, "Image deleted successfully")
                            },
                            onFailure = {
                                Log.d(TAG, "Failed to delete image: $it")
                            }
                        )
                    }

                    return Result.success()
                }else{
                    Log.d(TAG, "File uri is null")
                    val receivedMessage = ReceivedMessageState(
                        from = allDataList[0],
                        subject = allDataList[4],
                        fileLink = fileUriListAsString,
                        text = allDataList[1],
                        timeStamp = allDataList[5],
                        to = "avdone",
                        label = allDataList[3]
                    )

                    val changeToMessageState = MailLayoutItem(
                        sender = receivedMessage.from ?: "No sender found",
                        receiver = receivedMessage.to ?: "No receiver found",
                        content = receivedMessage.text ?: "No content found",
                        subject = receivedMessage.subject ?: "No subject found",
                        time = receivedMessage.timeStamp ?: "No time found",
                        fileLink = fileUriListAsString,
                        isReceived = true,
                        label = receivedMessage.label

                    )

                    MailItemGraph.allMailsRepository.insertMail(changeToMessageState)
                    return Result.success()
                }

            }

        }catch (e:Exception){
            showNotification(context,"Error",e.message ?: "Unknown error")
        }
        return Result.success()

    }

    private fun savingAndDownloadingImage(
        context: Context,
        downloadableLink: List<String>,

    ) : List<Uri>? {

        Log.d(TAG,"In saving and downloading image function go downloadable link as $downloadableLink")

        val fileNameList = mutableListOf<String>()
        val bitmapList = mutableListOf<Bitmap>()
        downloadableLink.forEachIndexed { index, link ->
            fileNameList.add("${Uri.parse(link).lastPathSegment?.substringAfterLast(" % 2F")}.jpg")
        }

        downloadableLink.forEach { link->
            try {

                val url = URL(link)
                val connection : HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream: InputStream = connection.inputStream
                val bitmap : Bitmap = BitmapFactory.decodeStream(inputStream)

                bitmapList.add(bitmap)


            }catch (e: Exception){
                Log.d(TAG, "Error in saving image: ${e.message}")
                return null

            }
        }
        Log.d(TAG, "Got bitmap list as $bitmapList")
        Log.d(TAG,"Got file name list as $fileNameList")
        return saveImageToGallery(context, bitmapList, fileNameList)

    }

    private fun saveImageToGallery(context: Context, bitmaps: List<Bitmap>, fileName: List<String>) : List<Uri>? {
        var outputStream : OutputStream?
        var imageUri: Uri? = null
        val imageUriList = mutableListOf<Uri>()

        bitmaps.forEachIndexed { index, bitmap ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                //Save image using scoped storage
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME,fileName[index])
                    put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                outputStream = imageUri?.let {

                    context.contentResolver.openOutputStream(it)
                }

            }else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

                val image = java.io.File(imagesDir, fileName[index])
                outputStream = FileOutputStream(image)

                // Get URI from the legacy file path
                imageUri = Uri.fromFile(image)
            }
            outputStream?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.flush()
            }
            imageUriList.add(imageUri ?: Uri.parse("noting found"))


        }



                showNotification(context, "Image Saved", "Image saved to gallery")

        return imageUriList

    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, message: String, details: String) {
        val builder = NotificationCompat.Builder(context, "103")
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your actual notification icon
            .setContentTitle(message)
            .setContentText(details)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically remove the notification when tapped

        // Note: No PendingIntent is set, so clicking the notification won't trigger any action

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(103, builder.build())
    }
}