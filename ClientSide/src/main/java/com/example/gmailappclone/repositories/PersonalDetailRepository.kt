package com.example.gmailappclone.repositories

import com.example.gmailappclone.daos.PersonalDetailDao
import com.example.gmailappclone.dataclasses.PersonalDetails
import kotlinx.coroutines.flow.Flow

class PersonalDetailRepository(private val personalDetailDao: PersonalDetailDao) {

    suspend fun insertPersonalDetail(details: PersonalDetails) = personalDetailDao.insertPersonalDetail(details)
    suspend fun deleteAll() = personalDetailDao.deleteAll()
    fun getPersonalDetail() : Flow<PersonalDetails> = personalDetailDao.getPersonalDetail()
    fun getUserName() : Flow<String?> = personalDetailDao.getUserName()
    fun getEmail(id : Int = 1) : String = personalDetailDao.getEmail(id)
    fun getNumber() : Flow<String> = personalDetailDao.getNumber()
    fun getPassword(id : Int = 1) : String = personalDetailDao.getPassword(id)
    suspend fun updateUserName(userName : String) = personalDetailDao.updateUserName(userName)
    suspend fun updateEmail(email : String) = personalDetailDao.updateEmail(email)
    suspend fun updateNumber(number : String) = personalDetailDao.updateNumber(number)
    suspend fun updatePassword(password : String) = personalDetailDao.updatePassword(password)


}