package com.example.wordmemory.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var isRotated by remember { mutableStateOf(false) }
    var isSwapped by remember { mutableStateOf(false) }
    var fontScaleIndex by remember { mutableIntStateOf(1) } // 0=1.0x, 1=1.25x, 2=1.5x
    val isTtsReady by ttsManager.isReady.collectAsState()
    val scope = rememberCoroutineScope()

    // Fixed locale: US only
    LaunchedEffect(bundleId) {
        ttsManager.setAccent(Locale.US)
    }

    LaunchedEffect(bundleId) {
        words = repository.getWordsByBundle(bundleId)
        isLoading = false
    }

    // TTS on first load — simple, no race condition
    LaunchedEffect(isTtsReady, words) {
        if (isTtsReady && words.isNotEmpty()) {
            delay(300)
            ttsManager.speak(words.first().word)
        }
    }

    // Reset rotation on swap toggle
    LaunchedEffect(isSwapped) {
        isRotated = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (words.isEmpty()) {
        EmptyStateScreen(onComplete, "No words in this bundle!")
    } else {
        StudyScreenContent(
            words = words,
            ttsManager = ttsManager,
            isTtsReady = isTtsReady,
            isSwapped = isSwapped,
            isRotated = isRotated,
            fontScaleIndex = fontScaleIndex,
            onFontScaleChange = { fontScaleIndex = it },
            onCardClick = { isRotated = !isRotated },
            onSwapToggle = { isSwapped = !isSwapped },
            onSpeak = { word ->
                scope.launch {
                    ttsManager.speak(word)
                }
            },
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StudyScreenContent(
    words: List<ParsedWord>,
    ttsManager: TtsManager,
    isTtsReady: Boolean,
    isSwapped: Boolean,
    isRotated: Boolean,
    fontScaleIndex: Int,
    onFontScaleChange: (Int) -> Unit,
    onCardClick: () -> Unit,
    onSwapToggle: () -> Unit,
    onSpeak: (String) -> Unit,
    onBack: () -> Unit,
    wordCount: Int
) {
    val pagerState = rememberPagerState(pageCount = { words.size })
    val scope = rememberCoroutineScope()

    // Font scale multiplier (0=1.0x, 1=1.25x, 2=1.5x)
    val fontScaleMultiplier = when (fontScaleIndex) {
        0 -> 1.0f
        1 -> 1.25f
        else -> 1.5f
    }

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

            // Font scale +/- buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onFontScaleChange((fontScaleIndex - 1).coerceAtLeast(0)) }) {
                    Text("A-", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
                Text(
                    text = when (fontScaleIndex) {
                        0 -> "S"
                        1 -> "M"
                        else -> "L"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = { onFontScaleChange((fontScaleIndex + 1).coerceAtMost(2)) }) {
                    Text("A+", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }

            // Progress indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LinearProgressIndicator(
                    progress = (pagerState.currentPage + 1).toFloat() / wordCount,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(4.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${pagerState.currentPage + 1} / $wordCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Swap toggle chip only (locale chips removed)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = isSwapped,
                onClick = onSwapToggle,
                label = { Text("↔ Swap", style = MaterialTheme.typography.labelMedium) }
            )
        }

        // Flashcard with HorizontalPager (swipe navigation)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
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
                        word = words[page],
                        isRotated = if (page == pagerState.currentPage) isRotated else false,
                        isSwapped = isSwapped,
                        fontScaleMultiplier = fontScaleMultiplier,
                        onCardClick = {
                            if (page == pagerState.currentPage) {
                                onCardClick()
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Speak button overlay
            IconButton(
                onClick = { onSpeak(words[pagerState.currentPage].word) },
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

        // Page indicator dots (replaces Previous/Next buttons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in words.indices) {
                val isSelected = i == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }

    // TTS on page change — uses ttsManager.isSpeaking internally
    LaunchedEffect(pagerState.currentPage, isTtsReady) {
        if (isTtsReady && words.isNotEmpty() && pagerState.currentPage < words.size) {
            delay(200)
            ttsManager.speak(words[pagerState.currentPage].word)
        }
    }
}
