package com.example.wordmemory.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
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
    var fontScaleIndex by remember { mutableIntStateOf(1) }
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No words in this bundle!", style = MaterialTheme.typography.titleLarge)
                Button(onClick = onComplete, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Back")
                }
            }
        }
    } else {
        ThreeTabStudyScreen(
            words = words,
            bundleId = bundleId,
            repository = repository,
            ttsManager = ttsManager,
            isTtsReady = isTtsReady,
            isRotated = isRotated,
            onRotationChange = { isRotated = it },
            isSwapped = isSwapped,
            onSwapChange = { isSwapped = it },
            fontScaleIndex = fontScaleIndex,
            onFontScaleChange = { fontScaleIndex = it },
            onSpeak = { word ->
                scope.launch {
                    ttsManager.speak(word)
                }
            },
            onBack = onComplete
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ThreeTabStudyScreen(
    words: List<ParsedWord>,
    bundleId: Long,
    repository: WordRepository,
    ttsManager: TtsManager,
    isTtsReady: Boolean,
    isRotated: Boolean,
    onRotationChange: (Boolean) -> Unit,
    isSwapped: Boolean,
    onSwapChange: (Boolean) -> Unit,
    fontScaleIndex: Int,
    onFontScaleChange: (Int) -> Unit,
    onSpeak: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("List") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Study") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Test") }
            )
        }

        when (selectedTab) {
            0 -> WordListTab(
                words = words,
                bundleId = bundleId,
                repository = repository,
                ttsManager = ttsManager,
                isTtsReady = isTtsReady,
                onSpeak = onSpeak
            )
            1 -> StudyModeTab(
                words = words,
                bundleId = bundleId,
                repository = repository,
                ttsManager = ttsManager,
                isTtsReady = isTtsReady,
                isRotated = isRotated,
                onRotationChange = onRotationChange,
                isSwapped = isSwapped,
                onSwapChange = onSwapChange,
                fontScaleIndex = fontScaleIndex,
                onFontScaleChange = onFontScaleChange,
                onSpeak = onSpeak,
                onBack = onBack
            )
            2 -> TestModeTab(
                words = words,
                bundleId = bundleId,
                repository = repository,
                onSpeak = onSpeak
            )
        }
    }
}

// ==================== TAB 1: Word List ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordListTab(
    words: List<ParsedWord>,
    bundleId: Long,
    repository: WordRepository,
    ttsManager: TtsManager,
    isTtsReady: Boolean,
    onSpeak: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(words, key = { it.id }) { word ->
            WordListItem(
                word = word,
                bundleId = bundleId,
                repository = repository,
                ttsManager = ttsManager,
                isTtsReady = isTtsReady,
                onSpeak = onSpeak
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordListItem(
    word: ParsedWord,
    bundleId: Long,
    repository: WordRepository,
    ttsManager: TtsManager,
    isTtsReady: Boolean,
    onSpeak: (String) -> Unit
) {
    var isDifficult by remember { mutableStateOf(word.isDifficult) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (isTtsReady) onSpeak(word.word) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    word.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    word.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    isDifficult = !isDifficult
                    scope.launch {
                        repository.toggleWordDifficult(word.id, bundleId, isDifficult)
                    }
                }) {
                    Icon(
                        imageVector = if (isDifficult) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle difficult",
                        tint = if (isDifficult) MaterialTheme.colorScheme.inverseSurface
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { if (isTtsReady) onSpeak(word.word) }) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ==================== TAB 2: Study Mode ====================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StudyModeTab(
    words: List<ParsedWord>,
    bundleId: Long,
    repository: WordRepository,
    ttsManager: TtsManager,
    isTtsReady: Boolean,
    isRotated: Boolean,
    onRotationChange: (Boolean) -> Unit,
    isSwapped: Boolean,
    onSwapChange: (Boolean) -> Unit,
    fontScaleIndex: Int,
    onFontScaleChange: (Int) -> Unit,
    onSpeak: (String) -> Unit,
    onBack: () -> Unit
) {
    var showOnlyDifficult by remember { mutableStateOf(false) }

    val displayWords = if (showOnlyDifficult) {
        words.filter { it.isDifficult }
    } else {
        words
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar: back, font controls, difficult filter toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }

            // Font scale buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onFontScaleChange((fontScaleIndex - 1).coerceAtLeast(0)) }) {
                    Text("A-", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
                Text(
                    text = when (fontScaleIndex) { 0 -> "S" 1 -> "M" else -> "L" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = { onFontScaleChange((fontScaleIndex + 1).coerceAtMost(2)) }) {
                    Text("A+", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }

            // Difficult filter toggle
            FilterChip(
                selected = showOnlyDifficult,
                onClick = { showOnlyDifficult = !showOnlyDifficult },
                label = { Text("⭐ Only Difficult") },
                leadingIcon = {
                    Icon(
                        if (showOnlyDifficult) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        // Swap toggle chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = isSwapped,
                onClick = { onSwapChange(!isSwapped) },
                label = { Text("↔ Swap") }
            )
        }

        // Flashcard area
        if (displayWords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No difficult words marked yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            StudyCardArea(
                words = displayWords,
                bundleId = bundleId,
                repository = repository,
                ttsManager = ttsManager,
                isTtsReady = isTtsReady,
                isRotated = isRotated,
                onRotationChange = onRotationChange,
                isSwapped = isSwapped,
                fontScaleMultiplier = when (fontScaleIndex) { 0 -> 1.0f 1 -> 1.25f else -> 1.5f },
                onSpeak = onSpeak,
                wordCount = displayWords.size
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StudyCardArea(
    words: List<ParsedWord>,
    bundleId: Long,
    repository: WordRepository,
    ttsManager: TtsManager,
    isTtsReady: Boolean,
    isRotated: Boolean,
    onRotationChange: (Boolean) -> Unit,
    isSwapped: Boolean,
    fontScaleMultiplier: Float,
    onSpeak: (String) -> Unit,
    wordCount: Int
) {
    val pagerState = rememberPagerState(pageCount = { words.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress
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

        // Flashcard
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    FlashcardView(
                        word = words[page],
                        isRotated = if (page == pagerState.currentPage) isRotated else false,
                        isSwapped = isSwapped,
                        fontScaleMultiplier = fontScaleMultiplier,
                        onCardClick = {
                            if (page == pagerState.currentPage) onRotationChange(!isRotated)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Speak button
            IconButton(
                onClick = { onSpeak(words[pagerState.currentPage].word) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                    .size(48.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Listen",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Difficult mark button (overlay on card)
            IconButton(
                onClick = {
                    val currentWord = words[pagerState.currentPage]
                    scope.launch {
                        repository.toggleWordDifficult(currentWord.id, bundleId, !currentWord.isDifficult)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f))
                    .size(40.dp)
            ) {
                val currentWord = words[pagerState.currentPage]
                Icon(
                    if (currentWord.isDifficult) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Toggle difficult",
                    tint = if (currentWord.isDifficult) MaterialTheme.colorScheme.onTertiaryContainer
                           else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Page dots
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
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }

    // TTS on page change
    LaunchedEffect(pagerState.currentPage, isTtsReady) {
        if (isTtsReady && words.isNotEmpty() && pagerState.currentPage < words.size) {
            delay(200)
            ttsManager.speak(words[pagerState.currentPage].word)
        }
    }

    // Reset rotation on swap toggle
    LaunchedEffect(isSwapped) {
        onRotationChange(false)
    }
}

// ==================== TAB 3: Test Mode ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestModeTab(
    words: List<ParsedWord>,
    bundleId: Long,
    repository: WordRepository,
    onSpeak: (String) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    var feedback by remember { mutableStateOf<TestFeedback?>(null) }
    var score by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Score display
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Test Mode",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Score: $score / ${words.size}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Progress indicator
        LinearProgressIndicator(
            progress = (currentIndex + 1).toFloat() / words.size,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(6.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            color = MaterialTheme.colorScheme.primary
        )

        // Korean meaning (question)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = words[currentIndex].meaning,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Speak button for Korean meaning
        IconButton(
            onClick = { onSpeak(words[currentIndex].meaning) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Listen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        // Input field
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Type the English word") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = feedback == null
        )

        // Feedback
        feedback?.let { fb ->
            Text(
                text = if (fb.correct) "✅ Correct!" else "❌ Answer: ${words[currentIndex].word}",
                style = MaterialTheme.typography.titleMedium,
                color = if (fb.correct) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
            )
        }

        // Action buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (feedback != null && currentIndex < words.size - 1) {
                Button(
                    onClick = {
                        currentIndex++
                        userInput = TextFieldValue("")
                        feedback = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next")
                }
            } else if (feedback != null && currentIndex == words.size - 1) {
                // All done — show restart
                Button(
                    onClick = {
                        currentIndex = 0
                        userInput = TextFieldValue("")
                        feedback = null
                        score = 0
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Restart")
                }
            } else if (feedback == null) {
                Button(
                    onClick = {
                        val correct = userInput.text.trim().equals(words[currentIndex].word, ignoreCase = true)
                        feedback = TestFeedback(correct)
                        if (correct) score++
                    },
                    enabled = userInput.text.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

private data class TestFeedback(val correct: Boolean)
