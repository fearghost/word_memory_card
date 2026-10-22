package com.example.wordmemory.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.wordmemory.data.local.room.AppDatabase
import com.example.wordmemory.data.repository.WordRepository
import com.example.wordmemory.parser.TextParser
import com.example.wordmemory.ui.navigation.AppNavigation
import com.example.wordmemory.ui.viewmodel.WordViewModel
import com.example.wordmemory.ocr.MLKitOCRManager
import com.example.wordmemory.tts.AndroidTtsManager
import com.example.wordmemory.api.GeminiCloudLLMClient
import com.example.wordmemory.data.local.room.dictionary.DictionaryRepository
import com.example.wordmemory.parser.CloudLLMMatcher
import com.example.wordmemory.BuildConfig

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual Dependency Injection
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "word_memory_db"
        ).build()
        
        val repository = WordRepository(db.wordDao())
        val dictionaryRepository = DictionaryRepository(db.dictionaryDao())
        val cloudMatcher = CloudLLMMatcher(GeminiCloudLLMClient(BuildConfig.GEMINI_API_KEY))
        val parser = TextParser(dictionaryRepository, cloudMatcher)
        val mlkitOcrManager = MLKitOCRManager(this)
        val viewModel = WordViewModel(mlkitOcrManager, parser)
        val ttsManager = AndroidTtsManager(this)

        setContent {
            val darkTheme = isSystemInDarkTheme()
            val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
            
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography()
            ) {
                Surface(
                    modifier = Modifier,
                    color = colorScheme.surface
                ) {
                    AppNavigation(viewModel, repository, ttsManager)
                }
            }
        }
    }
}
