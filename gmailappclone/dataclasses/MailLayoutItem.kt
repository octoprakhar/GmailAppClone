package com.example.gmailappclone.dataclasses

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
data class MailLayoutItem(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val icon : Int,
    val sender : String,
    val receiver : String,
    val subject : String,
    val content : String,
    val time : String,
    val isFavoriteClicked : Boolean,
    var isMailOpened : Boolean = false
)
