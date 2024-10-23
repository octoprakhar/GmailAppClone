package com.example.gmailappclone.dataclasses

data class MessageState(
    val remoteToken : String? = null,
    val to : String? = null,
    val subject : String? = null,
    val from : String? = null,
    val text : String? = null,
    var fileLink : String? = null,
    var label : String = "Primary"
)
