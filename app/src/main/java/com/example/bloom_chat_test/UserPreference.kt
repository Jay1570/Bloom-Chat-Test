package com.example.bloom_chat_test

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserPreference(private val context: Context) {

    companion object {
        const val PREFS_NAME = "user_prefs"
        const val USER_KEY = "userId"
    }

    private val sharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    private val _userFlow by lazy { MutableStateFlow(getSavedUser()) }
    val user: StateFlow<String> by lazy { _userFlow }

    private fun getSavedUser(): String {
        return sharedPreferences.getString(USER_KEY, "") ?: ""
    }


    fun setUserId(userId: String) {
        sharedPreferences.edit { putString(USER_KEY, userId) }
        _userFlow.value = userId
    }
}