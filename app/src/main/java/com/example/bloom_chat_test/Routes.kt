package com.example.bloom_chat_test

import kotlinx.serialization.Serializable

sealed interface Route

@Serializable
data object SetUser : Route

@Serializable
data object Home : Route

@Serializable
data class ChatScreen(
    val connectionId: String,
    val receiverId: String
) : Route