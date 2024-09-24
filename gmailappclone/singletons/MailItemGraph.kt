package com.example.gmailappclone.singletons

import android.content.Context
import androidx.room.Room
import com.example.gmailappclone.databases.MailItemDatabase
import com.example.gmailappclone.repositories.MailItemRepository

object MailItemGraph {

    lateinit var allMailsDatabase : MailItemDatabase

    val allMailsRepository by lazy {
        MailItemRepository(allMailsDatabase.mailItemDao())
    }

    fun init(context: Context){
        allMailsDatabase = Room.databaseBuilder(context,MailItemDatabase::class.java,"AllMails.db").build()
    }
}