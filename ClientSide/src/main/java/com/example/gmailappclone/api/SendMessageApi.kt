package com.example.gmailappclone.api

import com.example.gmailappclone.dataclasses.SendingMessageDto
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SendMessageApi {

    @POST("/sendMessage")
    suspend fun sendMessage(@Body message: SendingMessageDto): Response<ResponseBody>


}