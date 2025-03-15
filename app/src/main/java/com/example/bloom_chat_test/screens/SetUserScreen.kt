package com.example.bloom_chat_test.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloom_chat_test.AppViewModelProvider

@Composable
fun SetUserScreen(
    navigateToHome: () -> Unit,
    viewModel: SetUserViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val userId by viewModel.userId.collectAsStateWithLifecycle()
    Scaffold { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = { viewModel.onUserIdChange(it) },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                )
                Button(
                    onClick = { viewModel.saveUserId(navigateToHome) }, modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onBackground,
                        contentColor = MaterialTheme.colorScheme.background
                    ),
                    enabled = userId.isNotEmpty()
                ) {
                    Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}