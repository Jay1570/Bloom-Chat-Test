package com.example.bloom_chat_test.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.bloom_chat_test.ChatScreen
import com.example.bloom_chat_test.UserPreference
import com.example.bloom_chat_test.model.Chat
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
    private val applicationContext: Context
) : ViewModel() {

    private val parameters: ChatScreen = savedStateHandle.toRoute<ChatScreen>()
    private val connectionId = parameters.connectionId
    private val receiverId = parameters.receiverId
    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        getChats()
    }

    fun onMessageChange(message: String) {
        _uiState.update { it.copy(message = message) }
    }

    fun onSend() {
        viewModelScope.launch {
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
                firestore.collection("connections").document(connectionId).update("timestamp", timestamp).await()
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
//    private fun sendNotificationToReceiver(message: String) {
//        firestore.collection("users").document(receiverId).get()
//            .addOnSuccessListener { documentSnapshot ->
//                val fcmToken = documentSnapshot.getString("fcmToken")
//                if (!fcmToken.isNullOrEmpty()) {
//                    sendMessageToFCM(fcmToken, message, message, "BMSbBhSjRIIgJ3f6NhnRiJxM33_S-YrgZoNxhEunDsvxAWlhJx9JU78DojwyEwUxjphinVUSCb2_05FjzKrMtK0")
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("ChatViewModel", "Failed to fetch FCM Token", e)
//            }
//    }
//
//
//    fun sendMessageToFCM(deviceToken: String, messageTitle: String, messageBody: String, serverKey: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val urlString = "https://fcm.googleapis.com/v1/projects/bloom-chat-test/messages:send"
//                val url = URL(urlString)
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "POST"
//                connection.setRequestProperty("Content-Type", "application/json")
//                connection.setRequestProperty("Authorization", "Bearer $serverKey")
//                connection.doOutput = true
//
//                val message = JSONObject().apply {
//                    put("token", deviceToken)
//                    put("data", JSONObject().apply {
//                        put("title", messageTitle)
//                        put("body", messageBody)
//                    })
//                }
//
//                val outputStreamWriter = OutputStreamWriter(connection.outputStream)
//                outputStreamWriter.write(message.toString())
//                outputStreamWriter.flush()
//
//                val responseCode = connection.responseCode
//                val responseMessage = connection.responseMessage
//                Log.d("FCM", "Response Code : $responseCode")
//                Log.d("FCM", "Response Message : $responseMessage")
//
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    //Success
//                    val response = connection.inputStream.bufferedReader().use { it.readText() }
//                    Log.d("FCM", "Response Body: $response")
//                } else {
//                    //Handle Error
//                    val errorResponse =
//                        connection.errorStream?.bufferedReader()?.use { it.readText() }
//                    Log.e("FCM", "Error Response : $errorResponse")
//                }
//            } catch (e: Exception) {
//                Log.e("FCM", "Exception Sending message", e)
//            }
//        }
//    }
//

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

data class ChatUiState(
    val chats: List<Chat> = emptyList(),
    val currentUser: String = "",
    val message: String = ""
)