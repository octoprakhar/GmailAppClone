package com.example.gmailappclone.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gmailappclone.R
import com.example.gmailappclone.UtilClassesInstance
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.dataclasses.MessageState
import com.example.gmailappclone.dataclasses.ReceivedMessageState
import com.example.gmailappclone.helperclass.GetDateAndTime
import com.example.gmailappclone.repositories.MailItemRepository
import com.example.gmailappclone.singletons.MailItemGraph
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId

class ProfileScreenViewModel(
    private val allMailsRepository: MailItemRepository
) : ViewModel() {
    private val TAG = "ProfileScreenViewModel"

    private val getDateAndTime = GetDateAndTime()

    private val _mailLayoutList = MutableLiveData<List<MailLayoutItem>>(
        emptyList()
    )


    //For getting specific mail
    private val _specificMail = MutableLiveData<MailLayoutItem?>()
    val specificMail : LiveData<MailLayoutItem?> get() = _specificMail

    val mailLayoutList: LiveData<List<MailLayoutItem>> get() = _mailLayoutList
    var receivedMessageState by mutableStateOf(ReceivedMessageState())
        private set

    private val _receivedDataState = MutableLiveData<List<String>>()

    init {
        viewModelScope.launch {
            allMailsRepository.getAllMails().collect { mails ->
                _mailLayoutList.value = mails.sortedByDescending { it.id }

            }
        }

        viewModelScope.launch {
            UtilClassesInstance.sharedDataHolderHelper.downloadLinkFlow
                .collect { newLink ->
                    if (_receivedDataState.value != newLink) {
                        _receivedDataState.value = newLink
                        Log.d(TAG, "Receieved Data: $newLink")

//                        messageState.fileLink = _downloadLinkState.value
//                        setDownloadLink(newLink)
                    } else {
                        Log.d(TAG, "Received duplicate data, ignoring: $newLink")
                    }
                }
        }
        Log.d(TAG, "Received Data outside viewmodel scope: ${_receivedDataState.value}")

    }


    //Selecting favorite or not Favorite
    fun ToggleFavorite(mailId: Int) {
        _mailLayoutList.value = _mailLayoutList.value?.map { mailLayoutItem: MailLayoutItem ->
            if (mailLayoutItem.id == mailId) {
                val updatedMailLayoutItem = mailLayoutItem.copy(
                    isFavoriteClicked = !mailLayoutItem.isFavoriteClicked
                )
                viewModelScope.launch {
                    try {
                        allMailsRepository.insertMail(updatedMailLayoutItem)
                    } catch (e: Exception) {
                        Log.d(TAG, "Can't update the mail : ${e.message}")
                    }
                }

                updatedMailLayoutItem
            } else {
                mailLayoutItem
            }
        }
    }

    //Selecting favorite or not Favorite
    fun isMailSeen(mailId: Int) {
        _mailLayoutList.value = _mailLayoutList.value?.map { mailLayoutItem: MailLayoutItem ->
            if (mailLayoutItem.id == mailId) {
                val updatedMailLayoutItem = mailLayoutItem.copy(
                    isMailOpened = !mailLayoutItem.isMailOpened
                )
                viewModelScope.launch {
                    try {
                        allMailsRepository.insertMail(updatedMailLayoutItem)
                    } catch (e: Exception) {
                        Log.d(TAG, "Can't update the mail : ${e.message}")
                    }
                }

                updatedMailLayoutItem
            } else {
                mailLayoutItem
            }
        }
    }

    fun deleteMail(mailLayoutItem: MailLayoutItem){
        viewModelScope.launch {
            try {
                allMailsRepository.deleteMail(mailLayoutItem)
            }catch (e: Exception){
                Log.d(TAG, "Can't delete the mail : ${e.message}")
            }
        }
    }

    //Sending the clicked mail to other screen to view full mail
    fun findAParticularMail(mailId: Int){
        viewModelScope.launch {
            try {
                allMailsRepository.getSpecificMail(mailId).collect { mail ->
                    _specificMail.value = mail
                    Log.d(TAG, " Founded a speicific mail as ${_specificMail.value}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "Specific mail can't be found with error : ${e.message}")
                _specificMail.value = null
            }
        }
    }

    //Adding new mail in list
    @RequiresApi(Build.VERSION_CODES.O)
    fun addNewMail(mailLayoutItem: MailLayoutItem) {
        val currentDateAndTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
        val formattedTimeOrDate = getDateAndTime.getFormatedTimeOrDate(currentDateAndTime)
        val newMailLayoutItem = mailLayoutItem.copy(time = formattedTimeOrDate)
        viewModelScope.launch {
            try {
                allMailsRepository.insertMail(newMailLayoutItem)
            } catch (e: Exception) {
                Log.d(TAG, "Error occurred while inserting the mail: ${e.message}")
            }
        }
    }
}