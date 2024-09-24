package com.example.gmailappclone.helperclass

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class GetDateAndTime {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormatedTimeOrDate(createdAt: LocalDateTime) : String{
        val now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
        val hoursBetween = Duration.between(createdAt,now).toHours()

        val formatterTime = DateTimeFormatter.ofPattern("hh:mm a")
        val formatterDate = DateTimeFormatter.ofPattern("dd MMM yyyy")

        return if (hoursBetween< 24){
            createdAt.format(formatterTime)
        }else{
            createdAt.format(formatterDate)
        }
    }
}