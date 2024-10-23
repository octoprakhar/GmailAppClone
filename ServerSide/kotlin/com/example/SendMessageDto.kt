package com.example

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.serialization.Serializable

@Serializable
data class SendingMessageDto(
    val to : String? = null,
    val body : MessageBody
)

@Serializable
data class MessageBody(
    val subject : String? = null,
    val from : String? = null,
    val text : String? = null,
    val fileLink : String? = null,
    val label : String = "Primary"

)

fun SendingMessageDto.toMessage() : Message{
    return Message.builder()
        .setToken(to)
        .setNotification(
            Notification.builder()
                .setTitle(body.subject)
                .setBody("${this.body.from} ||| ${this.body.text} ||| ${this.body.fileLink} ||| ${this.body.label}")
//                .setBody(body.text)
                .build()
        )
        .build()
}
