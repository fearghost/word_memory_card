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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import com.example.wordmemory.domain.model.ParsedWord
import com.example.wordmemory.data.local.room.TestResultEntity
import com.example.wordmemory.data.repository.WordRepository
import com.example.wordmemory.tts.TtsManager
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun StudyScreen(
    bundleId: Long,
    repository: WordRepository,
    ttsManager: TtsManager,
    onComplete: () -> Unit
) {
    var isRotated by remember { mutableStateOf(false) }
    var isSwapped by remember { mutableStateOf(false) }
    var fontScaleIndex by remember { mutableIntStateOf(1) }
    val isTtsReady by ttsManager.isReady.collectAsState()
    val scope = rememberCoroutineScope()

    // Fixed locale: US only
    LaunchedEffect(bundleId) {
        ttsManager.setAccent(Locale.US)
    }

    // Reactive word list — automatically refreshes when DB changes (e.g. toggle difficult)
    val wordsFlow = repository.getWordsByBundleFlow(bundleId)
    var words by remember { mutableStateOf<List<ParsedWord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(wordsFlow) {
        wordsFlow.collect { fetched ->
            words = fetched
            isLoading = false
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
                repository = repository
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
    onSpeak: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
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
                IconButton(onClick = { onSpeak(word.word) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = {
                    scope.launch {
                        repository.toggleWordDifficult(word.id, bundleId, !word.isDifficult)
                    }
                }) {
                    Icon(
                        imageVector = if (word.isDifficult) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle difficult",
                        tint = if (word.isDifficult) MaterialTheme.colorScheme.inverseSurface
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
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
                    text = when (fontScaleIndex) {
                        0 -> "S"
                        1 -> "M"
                        2 -> "L"
                        3 -> "XL"
                        else -> "XXL"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = { onFontScaleChange((fontScaleIndex + 1).coerceAtMost(4)) }) {
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
                fontScaleMultiplier = when (fontScaleIndex) {
                    0 -> 1.0f
                    1 -> 1.25f
                    2 -> 1.5f
                    3 -> 1.75f
                    else -> 2.0f
                },
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

    // Reset pagerState when word count changes (e.g. filter toggle, DB update)
    LaunchedEffect(words.size) {
        if (pagerState.currentPage >= words.size) {
            pagerState.scrollToPage(0)
        }
    }

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

        // Flashcard area
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Card(
                        modifier = Modifier.fillMaxSize(),
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

                // Speak button — aligned to card area
                IconButton(
                    onClick = {
                        val idx = pagerState.currentPage
                        if (idx in words.indices) {
                            onSpeak(words[idx].word)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                        .size(44.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Difficult mark button — aligned to card area
                IconButton(
                    onClick = {
                        val idx = pagerState.currentPage
                        if (idx in words.indices) {
                            val currentWord = words[idx]
                            scope.launch {
                                repository.toggleWordDifficult(currentWord.id, bundleId, !currentWord.isDifficult)
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f))
                        .size(38.dp)
                ) {
                    val idx = pagerState.currentPage
                    Icon(
                        imageVector = if (idx in words.indices && words[idx].isDifficult) Icons.Default.Star
                                      else Icons.Default.StarBorder,
                        contentDescription = "Toggle difficult",
                        tint = if (idx in words.indices && words[idx].isDifficult) MaterialTheme.colorScheme.onTertiaryContainer
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
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

    // TTS on page change — manual only, triggered by Speak button above
    // Removed auto-speak on page change so user controls when to hear pronunciation

    // Reset rotation on swap toggle
    LaunchedEffect(isSwapped) {
        onRotationChange(false)
    }
}

// ==================== TAB 3: Test Mode ====================

/** Represents one test row: the word info + user's answer */
private data class TestResult(
    val word: ParsedWord,
    val userAnswer: String,
    val isCorrect: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestModeTab(
    words: List<ParsedWord>,
    bundleId: Long,
    repository: WordRepository
) {
    var phase by remember { mutableStateOf<TestPhase>(TestPhase.Testing) }
    var showHistory by remember { mutableStateOf(false) }
    val historyResults = remember { mutableStateListOf<TestResultEntity>() }
    val scope = rememberCoroutineScope()
    val testResults = remember { mutableStateListOf<TestResult>() }

    // Collect test result history when viewing
    LaunchedEffect(bundleId, showHistory) {
        if (showHistory) {
            repository.getTestResultsFlow(bundleId).collect { results ->
                historyResults.clear()
                historyResults.addAll(results)
            }
        }
    }

    when {
        showHistory -> HistoryView(
            historyResults = historyResults,
            onBack = { showHistory = false }
        )
        phase == TestPhase.Testing -> TestingPhase(
            words = words,
            bundleId = bundleId,
            repository = repository,
            scope = scope,
            testResults = testResults,
            onPhaseChange = { newPhase -> phase = newPhase }
        )
        phase == TestPhase.Results -> ResultsPage(
            results = testResults,
            onRestart = {
                testResults.clear()
                phase = TestPhase.Testing
            },
            onShowHistory = { showHistory = true },
            hasHistory = historyResults.isNotEmpty()
        )
    }
}

private enum class TestPhase { Testing, Results }

@Composable
private fun TestingPhase(
    words: List<ParsedWord>,
    bundleId: Long,
    repository: WordRepository,
    scope: kotlinx.coroutines.CoroutineScope,
    testResults: kotlin.collections.MutableList<TestResult>,
    onPhaseChange: (TestPhase) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var userInput by remember { mutableStateOf(TextFieldValue("")) }

    fun goNext() {
        val isCorrect = userInput.text.trim().equals(
            words[currentIndex].word, ignoreCase = true
        )
        testResults.add(TestResult(words[currentIndex], userInput.text.trim(), isCorrect))
        if (currentIndex < words.size - 1) {
            currentIndex++
            userInput = TextFieldValue("")
        } else {
            // Save to DB before switching phase
            scope.launch {
                val correctCount = testResults.count { it.isCorrect }
                val wrongWords = testResults.filter { !it.isCorrect }.map { "${it.word.word}|${it.word.meaning}" }.joinToString(";")
                repository.saveTestResult(bundleId, testResults.size, correctCount, wrongWords)
            }
            onPhaseChange(TestPhase.Results)
        }
    }

    fun restart() {
        testResults.clear()
        currentIndex = 0
        userInput = TextFieldValue("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
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
                "${currentIndex + 1} / ${words.size}",
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

        // Input field
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            label = { Text("Type the English word") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Next/Finish button
        Button(
            onClick = ::goNext,
            enabled = userInput.text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (currentIndex < words.size - 1) {
                Text("Next")
            } else {
                Text("Finish")
            }
        }

        // Restart button at bottom
        TextButton(onClick = ::restart) {
            Text("Start Over")
        }
    }
}

@Composable
private fun ResultsPage(
    results: List<TestResult>,
    onRestart: () -> Unit,
    onShowHistory: () -> Unit,
    hasHistory: Boolean
) {
    val correctCount = results.count { it.isCorrect }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & score
        Text(
            "Test Complete!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "$correctCount / ${results.size} correct",
            style = MaterialTheme.typography.titleLarge,
            color = if (correctCount == results.size) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
        )

        // Results list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(results, key = { it.word.id }) { result ->
                ResultRow(result = result)
            }
        }

        // History button
        if (hasHistory) {
            Button(
                onClick = onShowHistory,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Test History")
            }
        }

        // Restart button
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Restart Test")
        }
    }
}

@Composable
private fun ResultRow(result: TestResult) {
    val isCorrect = result.isCorrect
    val containerColor = if (isCorrect) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Meaning
            Text(
                text = result.word.meaning,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Answer row: correct answer | user answer
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Answer",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.word.word,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCorrect) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Your Answer",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = result.userAnswer.ifEmpty { "(empty)" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCorrect) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            // Status icon
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    if (isCorrect) "✅" else "❌",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

// ==================== Test History View ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryView(
    historyResults: List<TestResultEntity>,
    onBack: () -> Unit
) {
    var expandedItemId by remember { mutableStateOf<Long?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Toolbar
        TopAppBar(
            title = { Text("Test History") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        )

        if (historyResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No test history yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyResults, key = { it.id }) { result ->
                    var isExpanded by remember { mutableStateOf(expandedItemId == result.id) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        colors = CardDefaults.cardColors(
                            containerColor = if (result.correctAnswers == result.totalQuestions) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Summary row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    val dateStr = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
                                        .format(result.date)
                                    Text(
                                        dateStr,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "${result.correctAnswers} / ${result.totalQuestions} correct",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (result.correctAnswers == result.totalQuestions) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand"
                                )
                            }

                            // Expanded: wrong words
                            if (isExpanded && result.wrongWordsJson.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Wrong Words:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                // Parse the pipe-separated wrong words (word|meaning)
                                val wrongWords = result.wrongWordsJson.split(";")
                                for ((idx, wrongWord) in wrongWords.withIndex()) {
                                    val parts = wrongWord.split("|", limit = 2)
                                    if (parts.size == 2) {
                                        Text(
                                            "${idx + 1}. ${parts[0]} — ${parts[1]}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else if (isExpanded && result.correctAnswers == result.totalQuestions) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Perfect score! No wrong words.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
