package com.example.gmailappclone.viewmodels

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gmailappclone.UtilClassesInstance
import com.example.gmailappclone.api.SendMessageApi
import com.example.gmailappclone.dataclasses.MessageBody
import com.example.gmailappclone.dataclasses.MessageState
import com.example.gmailappclone.dataclasses.SendingMessageDto
import com.example.gmailappclone.firebaseUtils.FirebaseStorageHelper
import com.example.gmailappclone.firebaseUtils.FirestoreHelper
import com.example.gmailappclone.repositories.PersonalDetailRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ComposeViewModel(
    val firestoreHelper: FirestoreHelper,
    val firebaseStorageHelper: FirebaseStorageHelper,
    val personalDetailRepository: PersonalDetailRepository
) : ViewModel() {
    private val TAG = "ComposeViewModel"

    private val api : SendMessageApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create()

    private val _isRemoteUserPresent = MutableLiveData<Boolean>()
    val isRemoteUserPresent : LiveData<Boolean> get() = _isRemoteUserPresent

    private val _isUserPresent = MutableLiveData<Boolean>()
    val isUserPresent : LiveData<Boolean> get() = _isUserPresent

    private val _getAllNames = MutableLiveData<List<String>>()
    val getAllNames : LiveData<List<String>> get() = _getAllNames

    private val _checkingImageUploading = MutableLiveData<Boolean>()
    val checkingImageUploading : LiveData<Boolean> get() = _checkingImageUploading

    var messageState by mutableStateOf(MessageState())
        private set

    private val _downloadLinkState = MutableLiveData<List<String>>()

    private val _savedUserName = MutableLiveData<String>()
    val savedUserName : LiveData<String> get() = _savedUserName

//
    private val _imageUri = MutableLiveData<List<String>?>()
    val imageUri : LiveData<List<String>?> get() =  _imageUri


    init {

        viewModelScope.launch {
            personalDetailRepository.getPersonalDetail().collect{details->
                Log.d(TAG, "Received details from Room: $details")

                if (details != null){
                    _isRemoteUserPresent.value = true
                }else{
                    _isRemoteUserPresent.value = false
                }
            }
        }
        viewModelScope.launch {
            // Continuously observe the username from Room DB
            personalDetailRepository.getUserName().collect { user ->
                Log.d(TAG, "Received userName from Room: $user")

                // Only check Firestore if the username is available
                if (user != null && user.isNotBlank()) {
                    firestoreHelper.isUserNamePresent(user).collect { isPresent ->
                        _isUserPresent.value = isPresent
                        Log.d(TAG, "Is user present: ${_isUserPresent.value}")
                    }
                } else {
                    Log.d(TAG, "Username is null or empty")
                    _isUserPresent.value = false // Handle the case when there's no user
                }
            }
        }

        viewModelScope.launch {
            firestoreHelper.getAllNames().collect { names ->
                _getAllNames.value = names
            }
        }

        viewModelScope.launch {
            personalDetailRepository.getUserName().collect{
                _savedUserName.value = it
            }
        }

        viewModelScope.launch {
            UtilClassesInstance.sharedDataHolderHelper.isImageUploaded.collect{
                if (_checkingImageUploading.value != it){
                    _checkingImageUploading.value = it
                    Log.d(TAG, "Checking image uploading: $it")
                }
            }
        }


        viewModelScope.launch {
            UtilClassesInstance.sharedDataHolderHelper.downloadLinkFlow
                .collect { newLink ->
                    if (_downloadLinkState.value != newLink) {
                        _downloadLinkState.value = newLink
                        Log.d(TAG, "Download Link: $newLink")
                        Log.d(TAG,"Download Link inside viewmodel scope: ${_downloadLinkState.value}")
                    } else {
                        Log.d(TAG, "Received duplicate link, ignoring: $newLink")
                    }
                }
        }
        Log.d(TAG, "Download Link outside viewmodel scope: ${_downloadLinkState.value}")

    }


    fun setImageUri(uris: List<String>) {
        _imageUri.value = uris
    }




    fun onRemoteTokenChange(remoteToken: String) {
        messageState = messageState.copy(remoteToken = remoteToken)
    }

    fun onToChange(to: String) {
        messageState = messageState.copy(to = to)
    }
    fun onLabelChange(label:String){
        messageState = messageState.copy(label = label)
    }

    fun onSubjectChange(subject: String) {
        messageState = messageState.copy(subject = subject)
    }

    fun onFromChange(from: String) {
        messageState = messageState.copy(from = from)
    }

    fun onTextChange(text: String) {
        messageState = messageState.copy(text = text)
    }

    fun onFileLinkChange(fileLink: String) {
        messageState = messageState.copy(fileLink = fileLink)
    }



    fun getTokenByUserName(userName: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit){
        firestoreHelper.getTokenByUsername(userName, onSuccess, onFailure)

    }

    fun sendMessage(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        Log.d(TAG, "Sending message: $messageState")



        viewModelScope.launch {
            delay(5000)
            val sendingMessageDto = SendingMessageDto(
                to = messageState.remoteToken,
                body = MessageBody(
                    subject = messageState.subject,
                    from = messageState.from,
                    text = messageState.text,
                    fileLink = _downloadLinkState.value?.map { it }?.joinToString("*||*||*"),
                    label = messageState.label

                )
            )
            _imageUri.value = null
            Log.d(TAG, "Sending message: $sendingMessageDto")

            val response = api.sendMessage(sendingMessageDto)
            if (response.isSuccessful) {
                messageState = messageState.copy(
                    fileLink = null,
                    text = null,
                    subject = null,
                    from = null,
                    to = null,
                    remoteToken = null,
                    label = "Primary"
                )
                _imageUri.value = null
                UtilClassesInstance.sharedDataHolderHelper.setIsImageUploaded(false)
                onSuccess()
            } else {
                firebaseStorageHelper.deleteImageFromStorage(
                    "uploadedImage",
                    onSuccess = {
                        Log.d(TAG, "Image deleted successfully")
                    },
                    onFailure = {
                        Log.d(TAG, "Failed to delete image: $it")
                    }
                )
                messageState = messageState.copy(
                    fileLink = null,
                    text = null,
                    subject = null,
                    from = null,
                    to = null,
                    remoteToken = null
                )
                _imageUri.value = null
                onFailure(response.message())
            }
        }
    }


}