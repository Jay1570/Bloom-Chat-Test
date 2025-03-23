package com.example.bloom_chat_test.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.bloom_chat_test.ChatScreen
import com.example.bloom_chat_test.UserPreference
import com.example.bloom_chat_test.model.Chat
import com.example.bloom_chat_test.model.Connections
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel(
    savedStateHandle: SavedStateHandle,
    private val userPreference: UserPreference,
) : ViewModel() {

    private val parameters: ChatScreen = savedStateHandle.toRoute<ChatScreen>()
    private val connectionId = parameters.connectionId
    private val receiverId = parameters.receiverId //use this to fetch profile of receiver
    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        getChats()
        _uiState.update { it.copy(currentUser = userPreference.user.value, receiverId = receiverId) }
    }

    fun onMessageChange(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
            val unreadMessages =
                _uiState.value.chats.filter { it.senderId != _uiState.value.currentUser && !it.read }
            if (unreadMessages.isNotEmpty()) {
                val batch = firestore.batch()
                unreadMessages.forEach { message ->
                    val messageRef = firestore.collection("chats").document(connectionId).collection("chats").document(message.id)
                    batch.update(messageRef, "read", true)
                }
                val connectionRef = firestore.collection("connections").document(connectionId)
                batch.update(connectionRef, "unreadCount", 0)
                batch.commit().await()
            }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error marking messages as read", e)
                //show snackbar
            }
        }
    }

    fun onSend() {
        viewModelScope.launch {
            try {
                val message = uiState.value.message
                if (message.isNotEmpty()) {
                    val timestamp = Timestamp.now()
                    firestore.collection("chats").document(connectionId).collection("chats").add(
                        Chat(
                            message = message,
                            senderId = _uiState.value.currentUser,
                            timestamp = timestamp
                        )
                    ).await()
                    firestore.collection("connections").document(connectionId).get()
                        .addOnSuccessListener {
                            var connection =
                                it.toObject(Connections::class.java) ?: return@addOnSuccessListener
                            connection = connection.copy(
                                timestamp = timestamp,
                                lastSenderId = _uiState.value.currentUser,
                                unreadCount = connection.unreadCount + 1,
                                lastMessage = message
                            )
                            firestore.collection("connections").document(connectionId)
                                .set(connection).addOnFailureListener {
                                Log.e("ChatViewModel", "Error updating connection", it)
                            }
                        }
                }
                _uiState.update { it.copy(message = "") }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
                //show snackbar
            }
        }
    }

    private fun getChats() {
        listenerRegistration?.remove()
        listenerRegistration = firestore.collection("chats").document(connectionId).collection("chats").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("ChatViewModel", "Error fetching chats", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val chats = snapshot.toObjects(Chat::class.java)
                _uiState.update { it.copy(chats = chats) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

data class ChatUiState(
    val chats: List<Chat> = emptyList(),
    val currentUser: String = "",
    val receiverId: String = "",
    val message: String = ""
)