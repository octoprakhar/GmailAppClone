package com.example.gmailappclone.repositories

import com.example.gmailappclone.daos.MailLayoutItemDao
import com.example.gmailappclone.dataclasses.MailLayoutItem
import kotlinx.coroutines.flow.Flow

class MailItemRepository(private val mailLayoutItemDao: MailLayoutItemDao) {

    suspend fun insertMail(mailLayoutItem: MailLayoutItem) = mailLayoutItemDao.insertMail(mailLayoutItem)

    suspend fun deleteMail(mailLayoutItem: MailLayoutItem) = mailLayoutItemDao.deleteMail(mailLayoutItem)

    fun getAllMails() : Flow<List<MailLayoutItem>> = mailLayoutItemDao.getAllMails()

    fun getSpecificMail(mailId : Int) : Flow<MailLayoutItem> = mailLayoutItemDao.getSpecificMail(mailId)


}