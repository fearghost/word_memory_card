package com.example.wordmemory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wordmemory.domain.model.ParsedWord
import com.example.wordmemory.tts.TtsManager
import java.util.Locale

@Composable
fun StudyScreen(
    words: List<ParsedWord>,
    ttsManager: TtsManager,
    onComplete: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedLocale by remember { mutableStateOf(Locale.US) }

    if (words.isEmpty() || currentIndex >= words.size) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("All done!", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onComplete) {
                Text("Back to Home")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Progress and Accent Selection
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Card ${currentIndex + 1} of ${words.size}",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf(Locale.US, Locale.UK, Locale.forLanguageTag("en-AU")).forEach { locale ->
                        val label = when(locale.language) {
                            "en" -> if (locale.country == "US") "US" else if (locale.country == "GB") "UK" else "AU"
                            else -> locale.country
                        }
                        FilterChip(
                            selected = selectedLocale == locale,
                            onClick = { 
                                selectedLocale = locale
                                ttsManager.setAccent(locale)
                            },
                            label = { Text(label) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // Main Card Area
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FlashcardView(
                    word = words[currentIndex],
                    modifier = Modifier.fillMaxSize()
                )
                
                IconButton(
                    onClick = { ttsManager.speak(words[currentIndex].word) },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen")
                }
            }

            // Navigation Footer
            Button(
                onClick = { currentIndex++ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Next Card")
            }
        }
    }
}
