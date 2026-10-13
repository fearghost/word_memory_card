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
import com.example.wordmemory.ocr.MockOCRManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual Dependency Injection
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "word_memory_db"
        ).build()
        
        val repository = WordRepository(db.wordDao())
        val parser = TextParser()
        val mockOcrManager = MockOCRManager()
        val viewModel = WordViewModel(mockOcrManager, parser)
        val ttsManager = com.example.wordmemory.tts.AndroidTtsManager(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    AppNavigation(viewModel, repository, ttsManager)
                }
            }
        }
    }
}
