package com.example.gmailappclone

import com.example.gmailappclone.dataclasses.ReceivedMessageState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class SharedDataHolderHelper {

    private val _downloadLinkFlow = MutableSharedFlow<List<String>>(replay = 1)
    val downloadLinkFlow : SharedFlow<List<String>> get() = _downloadLinkFlow

    private val _isImageUploaded = MutableSharedFlow<Boolean>(replay = 1)
    val isImageUploaded : SharedFlow<Boolean> get() = _isImageUploaded

    suspend fun setIsImageUploaded(isUploaded : Boolean){
        _isImageUploaded.emit(isUploaded)

    }

    suspend fun emitDownloadLink(downloadLink : List<String>){
        _downloadLinkFlow.emit(downloadLink)

    }

}