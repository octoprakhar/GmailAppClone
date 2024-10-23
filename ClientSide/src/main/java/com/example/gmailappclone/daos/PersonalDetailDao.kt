package com.example.gmailappclone.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gmailappclone.dataclasses.PersonalDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalDetail(personalDetail: PersonalDetails)

    @Query("DELETE FROM personaldetails WHERE id = :id")
    suspend fun deleteAll(id : Int = 1)

    @Query("SELECT * FROM personaldetails")
    fun getPersonalDetail() : Flow<PersonalDetails>

    @Query("SELECT userName FROM personaldetails")
    fun getUserName() : Flow<String?>

    @Query("SELECT emailId FROM personaldetails WHERE id = :id")
    fun getEmail(id : Int ) : String

    @Query("SELECT number FROM personaldetails")
    fun getNumber() : Flow<String>

    @Query("SELECT password FROM personaldetails WHERE id = :id")
    fun getPassword( id : Int ) : String

    @Query("UPDATE personaldetails SET userName = :userName")
    suspend fun updateUserName(userName : String)

    @Query("UPDATE personaldetails SET emailId = :email")
    suspend fun updateEmail(email : String)

    @Query("UPDATE personaldetails SET number = :number")
    suspend fun updateNumber(number : String)

    @Query("UPDATE personaldetails SET password = :password")
    suspend fun updatePassword(password : String)


}