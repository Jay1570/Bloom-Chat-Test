package com.example.bloom_chat_test.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloom_chat_test.UserPreference
import com.example.bloom_chat_test.model.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SetUserViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _userId = MutableStateFlow("")
    val userId = _userId.asStateFlow()

    fun onUserIdChange(newUserId: String) {
        _userId.value = newUserId
    }

    fun saveUserId(navigateToHome: () -> Unit) {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                val docRef = firestore.collection("users").document(userId.value)
                docRef.set(FirebaseUser(id = userId.value, fcmToken =  token), SetOptions.merge()).await()
                userPreference.setUserId(userId.value)
                navigateToHome()
            } catch (e: Exception) {
                Log.e("SetUserViewModel", "Error saving user ID :- ", e)
            }
        }
    }
}