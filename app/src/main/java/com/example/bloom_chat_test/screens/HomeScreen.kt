package com.example.bloom_chat_test.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloom_chat_test.AppViewModelProvider
import com.example.bloom_chat_test.R
import com.example.bloom_chat_test.ui.theme.orange
import java.text.SimpleDateFormat
import java.util.Locale

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
                val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(connections.timestamp.toDate())
                UserItem(
                    name = displayUser,
                    lastMessage = connections.lastMessage,
                    lastMessageTime = dateFormat,
                    unreadCount = connections.unreadCount,
                    unreadAvailable = connections.lastSenderId == displayUser,
                    onClick = {
                        navigateToChat(connections.id, displayUser)
                    }
                )
            }
        }
    }
}

@Composable
fun UserItem(
    name: String,
    lastMessage: String,
    lastMessageTime: String,
    unreadCount: Int = 0,
    unreadAvailable: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImage(size = 56.dp)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Row {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = lastMessageTime,
                    color = if (unreadCount > 0 && unreadAvailable) orange else MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row {
                Text(text = lastMessage, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                if (unreadCount > 0 && unreadAvailable) {
                    Text(
                        text = unreadCount.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .background(color = orange, shape = CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileImage(size: Dp) {
    Image(
        painter = painterResource(id = R.drawable.user),
        contentDescription = "Profile",
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}