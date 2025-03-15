package com.example.bloom_chat_test.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Connections(
    @DocumentId val id: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val timestamp: Timestamp = Timestamp.now()
)