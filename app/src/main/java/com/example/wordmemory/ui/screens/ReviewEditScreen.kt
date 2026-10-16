package com.example.wordmemory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wordmemory.domain.model.ParsedWord
import com.example.wordmemory.ui.viewmodel.WordViewModel

@Composable
fun ReviewEditScreen(
    viewModel: WordViewModel,
    onSave: (List<ParsedWord>) -> Unit,
    onCancel: () -> Unit
) {
    val parsedWords by viewModel.parsedWords.collectAsState()
    
    // Local state to hold the edited words
    var editableWords by remember { mutableStateOf(parsedWords) }
    
    // Update local state when parsedWords changes (e.g. when entering screen)
    LaunchedEffect(parsedWords) {
        editableWords = parsedWords
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Review & Edit", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(editableWords.size, key = { it }) { index ->
                val word = editableWords[index]
                WordEditRow(
                    initialWord = word.word,
                    initialMeaning = word.meaning,
                    onWordChange = { newWord ->
                        editableWords = editableWords.toMutableList().apply {
                            this[index] = this[index].copy(word = newWord)
                        }
                    },
                    onMeaningChange = { newMeaning ->
                        editableWords = editableWords.toMutableList().apply {
                            this[index] = this[index].copy(meaning = newMeaning)
                        }
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(onClick = {
                onSave(editableWords)
            }, modifier = Modifier.weight(1f)) {
                Text("Save")
            }
        }
    }
}

@Composable
fun WordEditRow(
    initialWord: String,
    initialMeaning: String,
    onWordChange: (String) -> Unit,
    onMeaningChange: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = initialWord,
                onValueChange = onWordChange,
                label = { Text("Word") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = initialMeaning,
                onValueChange = onMeaningChange,
                label = { Text("Meaning") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
