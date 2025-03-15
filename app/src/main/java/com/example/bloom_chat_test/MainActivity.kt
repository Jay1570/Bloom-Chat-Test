package com.example.bloom_chat_test

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bloom_chat_test.screens.ChatScreen
import com.example.bloom_chat_test.screens.HomeScreen
import com.example.bloom_chat_test.screens.SetUserScreen
import com.example.bloom_chat_test.ui.theme.BloomChatTestTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloomChatTestTheme {
                var hasNotificationPermission by remember {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mutableStateOf(
                            ContextCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        )
                    } else {
                        mutableStateOf(true)
                    }
                }
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        hasNotificationPermission = isGranted
                    }
                )
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    val viewModel: NavigationViewModel = viewModel(factory = AppViewModelProvider.factory)
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = viewModel.startDestination()
                    ) {
                        composable<SetUser>{
                            SetUserScreen(
                                navigateToHome = {
                                    navController.navigate(Home) {
                                        launchSingleTop = true
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable<Home>{
                            HomeScreen(
                                navigateToChat = { connectionId, receiverId ->
                                    navController.navigate(ChatScreen(connectionId = connectionId, receiverId = receiverId)) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable<ChatScreen>{
                            ChatScreen(
                                navigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}