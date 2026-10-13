package com.example.wordmemory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.rememberSnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wordmemory.ui.viewmodel.WordViewModel
import com.example.wordmemory.domain.model.ParsedWord
import kotlinx.coroutines.launch

@Composable
fun ParsingScreen(viewModel: WordViewModel) {
    val parsedWords by viewModel.parsedWords.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = rememberSnackbarHostState()
    val scope = rememberCoroutineScope()

    // Handle error messages via Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Parsed Words", style = MaterialTheme.typography.headlineMedium)
            
            if (isProcessing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(parsedWords) { word ->
                        WordItemRow(word)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* Trigger Image Picker (handled by Activity) */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Images")
            }
        }
    }
}

@Composable
fun WordItemRow(word: ParsedWord) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = word.word, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Text(text = word.meaning, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
