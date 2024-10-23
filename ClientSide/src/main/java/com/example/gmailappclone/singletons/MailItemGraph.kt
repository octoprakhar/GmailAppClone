package com.example.gmailappclone.singletons

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Room
import com.example.gmailappclone.databases.MailItemDatabase
import com.example.gmailappclone.databases.PersonalDetailDatabase
import com.example.gmailappclone.repositories.MailItemRepository
import com.example.gmailappclone.repositories.PersonalDetailRepository

object MailItemGraph {

    lateinit var allMailsDatabase : MailItemDatabase
    lateinit var personalDetailDatabase : PersonalDetailDatabase


    val allMailsRepository by lazy {
        MailItemRepository(allMailsDatabase.mailItemDao())
    }
    val personalDetailRepository by lazy {
        PersonalDetailRepository(personalDetailDatabase.personalDetailDao())
    }

    fun personalDetailInit(context: Context){
        personalDetailDatabase = Room.databaseBuilder(context,PersonalDetailDatabase::class.java,"PersonalDetail.db").build()
    }

    fun init(context: Context){
        allMailsDatabase = Room.databaseBuilder(context,MailItemDatabase::class.java,"AllMails.db").build()
    }
}