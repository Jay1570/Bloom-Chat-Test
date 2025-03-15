package com.example.bloom_chat_test.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Chats(
    val id: String = "",
    val chats: List<Chat> = emptyList()
)

data class Chat(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now()
)