package com.example.wordmemory.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wordmemory.domain.model.ParsedWord
import com.example.wordmemory.data.repository.WordRepository
import com.example.wordmemory.tts.TtsManager
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var isSpeaking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            delay(200)
            
            if (currentIndex == 0) {
                ttsManager.speak(words[currentIndex].word)
                
                // Wait up to 1.5s to see if it actually starts speaking
                var started = false
                val startTime = System.currentTimeMillis()
                while (System.currentTimeMillis() - startTime < 1500) {
                    if (isSpeaking) {
                        started = true
                        break
                    }
                    delay(100)
                }
                
                if (!started) {
                    ttsManager.speak(words[currentIndex].word)
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
        EmptyStateScreen(onComplete, "No words in this bundle!")
    } else if (currentIndex >= words.size) {
        CompletionScreen(words.size, onComplete)
    } else {
        StudyScreenContent(
            words = words,
            currentIndex = currentIndex,
            selectedLocale = selectedLocale,
            isSwapped = isSwapped,
            isRotated = isRotated,
            onCardClick = { isRotated = !isRotated },
            onLocaleSelect = { locale -> 
                selectedLocale = locale
                ttsManager.setAccent(locale)
            },
            onSwapToggle = { isSwapped = !isSwapped },
            onSpeak = { word ->
                scope.launch {
                    isSpeaking = true
                    ttsManager.speak(word)
                    delay(1500)
                    isSpeaking = false
                }
            },
            onNext = { if (currentIndex < words.size - 1) currentIndex++ },
            onPrev = { if (currentIndex > 0) currentIndex-- },
            onBack = onComplete,
            wordCount = words.size
        )
    }
}

@Composable
fun EmptyStateScreen(onComplete: () -> Unit, message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onComplete) {
            Text("Back to Home")
        }
    }
}

@Composable
fun CompletionScreen(wordCount: Int, onComplete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🎉",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "All done!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "You reviewed $wordCount words",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onComplete) {
            Text("Back to Home")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreenContent(
    words: List<ParsedWord>,
    currentIndex: Int,
    selectedLocale: Locale,
    isSwapped: Boolean,
    isRotated: Boolean,
    onCardClick: () -> Unit,
    onLocaleSelect: (Locale) -> Unit,
    onSwapToggle: () -> Unit,
    onSpeak: (String) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onBack: () -> Unit,
    wordCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top bar with progress and back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            
            // Progress indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    progress = (currentIndex + 1).toFloat() / wordCount,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(4.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${currentIndex + 1} / $wordCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Spacer for balance
            Spacer(modifier = Modifier.weight(1f))
        }

        // Locale chips row
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf(Locale.US, Locale.UK, Locale.forLanguageTag("en-AU")).forEach { locale ->
                    val label = when(locale.country) {
                        "US" -> "🇺🇸 US"
                        "GB" -> "🇬🇧 UK"
                        "AU" -> "🇦🇺 AU"
                        else -> locale.country
                    }
                    FilterChip(
                        selected = selectedLocale == locale,
                        onClick = { onLocaleSelect(locale) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        border = FilterDefaults.border()
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                FilterChip(
                    selected = isSwapped,
                    onClick = onSwapToggle,
                    label = { Text("↔ Swap", style = MaterialTheme.typography.labelMedium) },
                    border = FilterDefaults.border()
                )
            }
        }

        // Flashcard
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                FlashcardView(
                    word = words[currentIndex],
                    isRotated = isRotated,
                    isSwapped = isSwapped,
                    onCardClick = onCardClick,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Speak button overlay
            IconButton(
                onClick = { onSpeak(words[currentIndex].word) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                    .size(48.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            NavigationButton(
                onClick = onPrev,
                enabled = currentIndex > 0,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = "Previous",
                modifier = Modifier.weight(1f)
            )
            NavigationButton(
                onClick = onNext,
                enabled = currentIndex < wordCount - 1,
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                label = "Next",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NavigationButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontWeight = FontWeight.Medium)
    }
}
