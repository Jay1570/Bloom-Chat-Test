package com.example.bloom_chat_test

import android.app.Application

class ChatApplication : Application() {

    lateinit var userPreference: UserPreference

    override fun onCreate() {
        super.onCreate()
        userPreference = UserPreference(this)
    }
}