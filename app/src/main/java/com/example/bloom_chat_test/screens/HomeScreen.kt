package com.example.bloom_chat_test.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloom_chat_test.AppViewModelProvider

@Composable
fun HomeScreen(
    navigateToChat: (String, String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val connections by viewModel.connection.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold {
        LazyColumn(contentPadding = it) {
            items(connections) { connections ->
                val displayUser = if (connections.user1Id == currentUser) connections.user2Id else connections.user1Id
                Row(modifier = Modifier.fillMaxWidth().height(50.dp).clickable(onClick = {
                    navigateToChat(connections.id, displayUser)
                }), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = displayUser,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}