package com.example.gmailappclone.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gmailappclone.dataclasses.MailLayoutItem
import com.example.gmailappclone.repositories.MailItemRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DrawerViewModel(
    private val mailItemRepository: MailItemRepository
) : ViewModel()  {

    private val TAG = "DrawerViewModel"

//    val labelNames = listOf("Starred","Snoozed","Important","Sent","Scheduled","Outbox","Drafts","All Mail","Spam","Bin","[Imap]/Trash")


    private val _getPromotionMail = MutableLiveData<List<MailLayoutItem>>()
    val getPromotionMail : MutableLiveData<List<MailLayoutItem>> = _getPromotionMail

    private val _getSocialMail = MutableLiveData<List<MailLayoutItem>>()
    val getSocialMail : MutableLiveData<List<MailLayoutItem>> = _getSocialMail

    private val _getPrimaryMail = MutableLiveData<List<MailLayoutItem>>()
    val getPrimaryMail : MutableLiveData<List<MailLayoutItem>> = _getPrimaryMail

    private val _getStarredMail = MutableLiveData<List<MailLayoutItem>>()
    val getStarredMail : MutableLiveData<List<MailLayoutItem>> = _getStarredMail

    private val _getSentMail = MutableLiveData<List<MailLayoutItem>>()
    val getSentMail : MutableLiveData<List<MailLayoutItem>> = _getSentMail

    private val _getDraftMail = MutableLiveData<List<MailLayoutItem>>()
    val getDraftMail : MutableLiveData<List<MailLayoutItem>> = _getDraftMail

    private val _getBinMail = MutableLiveData<List<MailLayoutItem>>()
    val getBinMail : MutableLiveData<List<MailLayoutItem>> = _getBinMail



    init {
        viewModelScope.launch {
            mailItemRepository.getMailsByLabel("Promotion").collect{
                _getPromotionMail.value = it
            }
        }
        viewModelScope.launch {
            mailItemRepository.getMailsByLabel("Social").collect{
                _getSocialMail.value = it
            }
        }
        viewModelScope.launch {
            mailItemRepository.getMailsByLabel("Primary").collect{
                _getPrimaryMail.value = it
            }

        }
        viewModelScope.launch {
            mailItemRepository.getAllMails().collect {mailList->
                _getStarredMail.value = mailList.filter { it.isFavoriteClicked }


            }
        }
        viewModelScope.launch {
            mailItemRepository.getAllMails().collect{
                _getSentMail.value = it.filter { !it.isReceived }
            }
        }
        viewModelScope.launch {
            mailItemRepository.getAllMails().collect{
                _getDraftMail.value = it.filter { it.isSentSuccessfully }
            }
        }
        viewModelScope.launch {
            mailItemRepository.getAllMails().collect{
                _getBinMail.value = it.filter { it.isInBinOrTrash }
            }
        }
    }

}