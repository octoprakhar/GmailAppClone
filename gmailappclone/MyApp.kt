package com.example.gmailappclone

import android.app.Application
import com.example.gmailappclone.singletons.MailItemGraph

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MailItemGraph.init(this)
    }
}