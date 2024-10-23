package com.example.gmailappclone.dataclasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonalDetails(
    @PrimaryKey
    val id : Int = 1,
    val userName : String? = null,
    val emailId : String? = null,
    val password : String? = null,
    val number : String? = null
)
