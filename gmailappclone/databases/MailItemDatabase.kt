package com.example.gmailappclone.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gmailappclone.daos.MailLayoutItemDao
import com.example.gmailappclone.dataclasses.MailLayoutItem

@Database(
    entities = [MailLayoutItem::class],
    version = 1,
    exportSchema = false
)
abstract class MailItemDatabase : RoomDatabase() {

    abstract fun mailItemDao() : MailLayoutItemDao

}