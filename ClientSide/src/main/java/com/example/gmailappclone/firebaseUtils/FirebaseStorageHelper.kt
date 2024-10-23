package com.example.gmailappclone.firebaseUtils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.util.Locale

class FirebaseStorageHelper {

    private val TAG = "FirebaseStorageHelper"
    private val _storage = Firebase.storage
    private val _storageRef = _storage.reference

    private val _imageRef = _storageRef.child("images")


    fun UploadImage(
        name : List<String>,
        context: Context,
        uris: List<Uri>,
        onSuccess : (downloadUris:List<String>) -> Unit,
        onFailure : (exception:Exception) -> Unit,
        onFailureDownloadUrl : (exception:Exception) -> Unit,
        onFailureInImageConversion : (exception:Exception) -> Unit
    ){

        //Point to a specific file

        val downloadUris = mutableListOf<String>()
        try {
            //Converting Uri to stream

            uris.forEachIndexed { index,uri ->
                val filePath = name[index]
                val path = _imageRef.child(filePath)
                context.contentResolver.openInputStream(uri)?.use { inputStream->
                    Log.d(TAG, "Opened InputStream for URI: $uri")

                    val imageByteArray = inputStream.readBytes()

                    //Now Let's upload it
                    try {
                        val uploadTask = path.putBytes(imageByteArray)
                        uploadTask.addOnFailureListener {
                            // Handle unsuccessful uploads
//                        Log.e(TAG, "Upload failed: ${it.message}")
                        }.addOnSuccessListener { taskSnapshot ->
                            // Upload succeeded
//                        Log.d(TAG, "Upload succeeded! Metadata: ${taskSnapshot.metadata}")
                            //Getting download url
                            path.downloadUrl.addOnSuccessListener { uri ->
                                val downloadUrl = uri.toString()
                                downloadUris.add(downloadUrl)
                                if (downloadUris.size == uris.size){
                                    onSuccess(downloadUris)
                                }

                            }.addOnFailureListener {
//                                Log.e(TAG, "Failed to get download URL: ${it.message}")
                                onFailureDownloadUrl(it)
                            }

                        }
                    }catch (e:Exception){
//                    Log.d(TAG,"Error uploading image ${e.message}")
                        onFailure(e)
                    }

                }
            }

        }catch (e:Exception){
//            Log.d(TAG,"Error converting image to byteArray ${e.message}")
            onFailureInImageConversion(e)
        }

    }

    fun deleteImageFromStorage(
        fileName : String,
        onSuccess : () -> Unit,
        onFailure: (exception: Exception) -> Unit
    ){
        val filePath = _imageRef.child(fileName.lowercase(Locale.ROOT))
        filePath.delete().addOnSuccessListener {
            onSuccess()
        }
            .addOnFailureListener(onFailure)
    }

}