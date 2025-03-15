package com.example.bloom_chat_test

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bloom_chat_test.screens.ChatViewModel
import com.example.bloom_chat_test.screens.HomeViewModel
import com.example.bloom_chat_test.screens.SetUserViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            NavigationViewModel(myApp().userPreference)
        }

        initializer {
            SetUserViewModel(myApp().userPreference)
        }

        initializer {
            HomeViewModel(myApp().userPreference)
        }

        initializer {
            ChatViewModel(this.createSavedStateHandle(), myApp().userPreference, myApp().applicationContext)
        }
    }
}

fun CreationExtras.myApp(): ChatApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChatApplication)