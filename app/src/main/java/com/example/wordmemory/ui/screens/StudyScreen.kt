package com.example.wordmemory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wordmemory.domain.model.ParsedWord
import com.example.wordmemory.data.repository.WordRepository
import com.example.wordmemory.tts.TtsManager
import java.util.Locale

@Composable
fun StudyScreen(
    bundleId: Long,
    repository: WordRepository,
    ttsManager: TtsManager,
    onComplete: () -> Unit
) {
    var words by remember { mutableStateOf<List<ParsedWord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedLocale by remember { mutableStateOf(Locale.US) }
    var isRotated by remember { mutableStateOf(false) }
    var isSwapped by remember { mutableStateOf(false) }
    val isTtsReady by ttsManager.isReady.collectAsState()

    LaunchedEffect(bundleId) {
        words = repository.getWordsByBundle(bundleId)
        isLoading = false
    }

    LaunchedEffect(selectedLocale) {
        ttsManager.setAccent(selectedLocale)
    }

    LaunchedEffect(currentIndex) {
        isRotated = false
    }

    // Combined effect to handle initial playback and subsequent navigation safely
    LaunchedEffect(isTtsReady, words, currentIndex, isRotated, isSwapped) {
        if (isTtsReady && words.isNotEmpty() && !isRotated && !isSwapped) {
            // A small buffer to ensure the engine is fully primed after isReady becomes true
            delay(200) 
            
            // For the very first word, we try multiple times to ensure it's not lost during engine warmup
            if (currentIndex == 0) {
                repeat(3) { i ->
                    ttsManager.speak(words[currentIndex].word)
                    delay(500)
                }
            } else {
                ttsManager.speak(words[currentIndex].word)
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (words.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No words in this bundle!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onComplete) {
                Text("Back to Home")
            }
        }
    } else if (currentIndex >= words.size) {
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
                        val label = when(locale.country) {
                            "US" -> "US"
                            "GB" -> "UK"
                            "AU" -> "AU"
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
                    FilterChip(
                        selected = isSwapped,
                        onClick = { isSwapped = !isSwapped },
                        label = { Text("Swap") }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                FlashcardView(
                    word = words[currentIndex],
                    isRotated = isRotated,
                    isSwapped = isSwapped,
                    onCardClick = { isRotated = !isRotated },
                    modifier = Modifier.fillMaxSize()
                )
                
                IconButton(
                    onClick = { ttsManager.speak(words[currentIndex].word) },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { if (currentIndex > 0) currentIndex-- },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }
                Button(
                    onClick = { if (currentIndex < words.size - 1) currentIndex++ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }
        }
    }
}
