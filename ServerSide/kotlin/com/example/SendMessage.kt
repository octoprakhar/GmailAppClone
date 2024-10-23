package com.example

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.sendNotification() {

    route("/sendMessage") {
        post {
            val body = call.receiveNullable<SendingMessageDto>() ?: run {
                call.respond(HttpStatusCode.BadRequest, "Invalid message body")
                return@post
            }

            val token = body.to
            if (token.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "FCM registration token is missing or invalid")
                return@post
            }

            try {
                // Send the message using FCM
                val response = FirebaseMessaging.getInstance().send(body.toMessage())
                call.respond(HttpStatusCode.OK, "Message sent successfully: $response")
            } catch (e: FirebaseMessagingException) {
                // Log and respond with the specific error
                call.respond(HttpStatusCode.InternalServerError, "Error sending message: ${e.localizedMessage}")
            }
        }
    }
}
