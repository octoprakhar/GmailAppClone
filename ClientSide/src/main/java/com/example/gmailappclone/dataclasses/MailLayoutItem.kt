package com.example.gmailappclone.dataclasses

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
data class MailLayoutItem(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val icon : Int = 0,
    val sender : String= "",
    val receiver : String = "",
    val subject : String = "",
    val content : String = "",
    val time : String = "",
    val isFavoriteClicked : Boolean = false,
    var isMailOpened : Boolean = false,
    var fileLink : String? = null,
    var label : String? = null,
    var isReceived : Boolean = false,
    var isInBinOrTrash : Boolean = false,
    var isSentSuccessfully : Boolean = false
)
