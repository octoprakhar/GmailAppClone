package com.example.gmailappclone.dataclasses

data class SendingMessageDto(
    val to : String? = null,
    val body : MessageBody
)

data class MessageBody(
    val subject : String? = null,
    val from : String? = null,
    val text : String? = null,
    val fileLink : String? = null,
    val label : String = "Primary"

)
