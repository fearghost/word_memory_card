package com.example.wordmemory.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
        val cloudMatcher = CloudLLMMatcher(GeminiCloudLLMClient("YOUR_GEMINI_API_KEY"))
        val parser = TextParser(dictionaryRepository, cloudMatcher)
        val mlkitOcrManager = MLKitOCRManager(this)
        val viewModel = WordViewModel(mlkitOcrManager, parser)
        val ttsManager = AndroidTtsManager(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    AppNavigation(viewModel, repository, ttsManager)
                }
            }
        }
    }
}
