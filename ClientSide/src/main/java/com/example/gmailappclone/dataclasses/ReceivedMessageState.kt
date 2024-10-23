package com.example.gmailappclone.dataclasses

data class ReceivedMessageState(
    val to : String? = null,
    val subject : String? = null,
    val from : String? = null,
    val text : String? = null,
    var fileLink : String? = null,
    val timeStamp : String? = null,
    val label : String = "Primary"
)
