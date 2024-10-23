package com.example.gmailappclone.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gmailappclone.daos.PersonalDetailDao
import com.example.gmailappclone.dataclasses.PersonalDetails

@Database(entities = [PersonalDetails::class], version = 1, exportSchema = false)
abstract class PersonalDetailDatabase : RoomDatabase() {

    abstract fun personalDetailDao(): PersonalDetailDao



}