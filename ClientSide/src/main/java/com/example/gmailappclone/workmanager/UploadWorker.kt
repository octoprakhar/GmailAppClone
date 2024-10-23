package com.example.gmailappclone.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gmailappclone.R
import com.example.gmailappclone.UtilClassesInstance
import com.example.gmailappclone.firebaseUtils.FirebaseStorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class UploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val TAG = "UploadWorker"
    override suspend fun doWork(): Result {
        val fileLinkArray = inputData.getStringArray("fileLink") ?: return Result.failure()


        // Convert it back to a List<String> or ArrayList<String>
        val fileLinkList = fileLinkArray.toList() ?: emptyList()

        val fileNames = mutableListOf<String>()

        fileLinkList.forEach {
            if (it != null) {
                fileNames.add(getFileName(applicationContext,Uri.parse(it)))
            }
        }
        Log.d(TAG,"Got fileLink: $fileLinkList and fileName: $fileNames")
        val context = applicationContext
        val firebaseStorageHelper = FirebaseStorageHelper()
        val uriString = mutableListOf<Uri>()
        fileLinkList.forEach {
            if (it != null) {
                uriString.add(Uri.parse(it))
            }
        }
        try {
            // Upload the image to Firebase Storage
            //Delay it by 5 seconds
            Log.d(TAG,"Got uri string as $uriString")
            firebaseStorageHelper.UploadImage(
                name = fileNames,
                context = context,
                uris = compressImage(context, uriString) ?: return Result.failure(),
                onSuccess = { downloadUri ->
                    CoroutineScope(Dispatchers.IO).launch {
                        UtilClassesInstance.sharedDataHolderHelper.emitDownloadLink(downloadUri)
                        UtilClassesInstance.sharedDataHolderHelper.setIsImageUploaded(true)

                    }
                },
                onFailure = {
                    showNotification(context,"Upload Failed", it.message ?: "Unknown error")
                },
                onFailureDownloadUrl = {
                    showNotification(context,"Failed to get download URL", it.message ?: "Unknown error")
                },
                onFailureInImageConversion = {
                    showNotification(context,"Failed to convert image", it.message ?: "Unknown error")
                }
            )
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
    private fun getFileName(context: Context, uri: Uri): String {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName ?: "UploadThisImage"
    }

    private fun compressImage(context: Context, uris: List<Uri?>): List<Uri> {
        val compressedUris = mutableListOf<Uri>()

        uris.forEachIndexed {index:Int, uri: Uri? ->
            if (uri != null){

                Log.d(TAG, "Compressing image for URI: $uri")

                // Step 1: Load the image as a Bitmap using ImageDecoder for Android P (API 28) and above
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    // For older versions, use the deprecated method
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                // Step 2: Create a ByteArrayOutputStream to hold the compressed data
                val outputStream = ByteArrayOutputStream()

                // Step 3: Compress the Bitmap into JPEG format with 50% quality
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream)

                // Step 4: Get the compressed image data as a ByteArray
                val byteArray = outputStream.toByteArray()

                // Step 5: Create a new cache file to store the compressed image
                val cacheFile = File(context.cacheDir, "compressed_image${index}.jpg")

                try {
                    // Step 6: Write the byte array to the cache file
                    cacheFile.outputStream().use { fileOutputStream ->
                        fileOutputStream.write(byteArray)
                    }

                    Log.d(TAG, "Compressed image saved to: ${cacheFile.absolutePath}")


                    // Step 7: Return the Uri of the cache file
                    compressedUris.add(Uri.fromFile(cacheFile))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return compressedUris



    }


    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context, message: String, details: String) {
        val builder = NotificationCompat.Builder(context, "101")
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your actual notification icon
            .setContentTitle(message)
            .setContentText(details)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically remove the notification when tapped

        // Note: No PendingIntent is set, so clicking the notification won't trigger any action

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(101, builder.build())
    }


}