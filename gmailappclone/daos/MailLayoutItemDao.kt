package com.example.gmailappclone.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.gmailappclone.dataclasses.MailLayoutItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MailLayoutItemDao {

    @Upsert
    suspend fun insertMail(mailLayoutItem: MailLayoutItem)

    @Delete
    suspend fun deleteMail(mailLayoutItem: MailLayoutItem)

    @Query("SELECT * FROM MailLayoutItem")
    fun getAllMails() : Flow<List<MailLayoutItem>>

    @Query("SELECT * FROM MailLayoutItem WHERE id = :mailId LIMIT 1")
    fun getSpecificMail(mailId : Int) : Flow<MailLayoutItem>

}