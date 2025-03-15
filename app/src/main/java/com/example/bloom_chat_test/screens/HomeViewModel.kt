package com.example.bloom_chat_test.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.bloom_chat_test.UserPreference
import com.example.bloom_chat_test.model.Connections
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _connections = MutableStateFlow<List<Connections>>(emptyList())
    val connection = _connections.asStateFlow()

    val currentUser get() = userPreference.user

    init {
        getConnections()
    }

    private fun getConnections() {
        listenerRegistration?.remove()
        Log.d("HomeViewModel", currentUser.value)
        listenerRegistration = firestore.collection("connections").where(
            Filter.or(
                Filter.equalTo("user1Id", currentUser.value),
                Filter.equalTo("user2Id", currentUser.value)
            )
        ).orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("HomeViewModel", "Error fetching connections", e)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val connections = snapshot.documents.mapNotNull { it.toObject(Connections::class.java) }
                _connections.value = connections
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}