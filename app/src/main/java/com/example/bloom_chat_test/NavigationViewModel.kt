package com.example.bloom_chat_test

import androidx.lifecycle.ViewModel

class NavigationViewModel(private val userPreference: UserPreference) : ViewModel() {

    fun startDestination(): Route {
        return if (userPreference.user.value.isEmpty()) {
            SetUser
        } else {
            Home
        }
    }
}