package com.example.wordmemory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wordmemory.data.repository.WordRepository
import kotlinx.coroutines.launch

@Composable
fun NewBundleScreen(
    repository: WordRepository,
    onNavigationUp: () -> Unit,
    onBundleCreated: () -> Unit
) {
    var bundleName by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Create New Bundle", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = bundleName,
            onValueChange = { bundleName = it },
            label = { Text("Bundle Name (e.g., Travel)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isCreating) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    scope.launch {
                        repository.createBundle(bundleName)
                        onBundleCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = bundleName.isNotBlank()
            ) {
                Text("Create")
            }
        }
        
        TextButton(onClick = onNavigationUp, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}
