package com.example.wordmemory.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wordmemory.data.repository.WordRepository
import com.example.wordmemory.ui.viewmodel.WordViewModel
import kotlinx.coroutines.launch

@Composable
fun NewBundleScreen(
    repository: WordRepository,
    viewModel: WordViewModel,
    onNavigationUp: () -> Unit,
    onBundleCreated: () -> Unit
) {
    var bundleName by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val parsedWords by viewModel.parsedWords.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                viewModel.processImages(uris)
            }
        }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(SnackbarHostState()) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Create New Bundle", style = MaterialTheme.typography.headlineMedium)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = bundleName,
                onValueChange = { bundleName = it },
                label = { Text("Bundle Name (e.g., Travel)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Images to OCR")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isProcessing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(parsedWords) { word ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = word.word, style = MaterialTheme.typography.titleLarge)
                                Text(text = word.meaning, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isCreating) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        scope.launch {
                            isCreating = true
                            try {
                                val bundleId = repository.createBundle(bundleName)
                                repository.saveWordsToBundle(bundleId, parsedWords)
                                onBundleCreated()
                            } catch (e: Exception) {
                                // Error handling
                            } finally {
                                isCreating = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = bundleName.isNotBlank() && parsedWords.isNotEmpty()
                ) {
                    Text("Create Bundle with ${parsedWords.size} words")
                }
            }
            
            TextButton(onClick = onNavigationUp, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel")
            }
        }
    }
}
