package com.example.bloom_chat_test.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloom_chat_test.UserPreference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                userPreference.setUserId(userId.value)
                navigateToHome()
            } catch (e: Exception) {
                Log.e("SetUserViewModel", "Error saving user ID :- ", e)
            }
        }
    }
}