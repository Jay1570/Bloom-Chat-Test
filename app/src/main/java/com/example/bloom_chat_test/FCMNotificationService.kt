package com.example.bloom_chat_test

import com.example.bloom_chat_test.model.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService

class FCMNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val application = application as ChatApplication
        val userId = application.userPreference.user.value
        if (userId.isNotEmpty()) {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.set(FirebaseUser(id = userId, fcmToken = token), SetOptions.merge())
        }
    }
}