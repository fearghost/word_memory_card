package com.example.wordmemory.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordmemory.ocr.OCRManager
import com.example.wordmemory.parser.TextParser
import com.example.wordmemory.domain.model.ParsedWord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WordViewModel(
    private val ocrManager: OCRManager,
    private val textParser: TextParser
) : ViewModel() {

    private val _parsedWords = MutableStateFlow<List<ParsedWord>>(emptyList())
    val parsedWords: StateFlow<List<ParsedWord>> = _parsedWords

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    fun processImages(uris: List<Uri>) {
        viewModelScope.launch {
            _isProcessing.value = true
            val allWords = mutableListOf<ParsedWord>()
            
            for (uri in uris) {
                try {
                    val result = ocrManager.processImage(uri)
                    val words = textParser.parse(result.rawText)
                    allWords.addAll(words)
                } catch (e: Exception) {
                    // In a real app, log the error
                }
            }
            
            _parsedWords.value = allWords
            _isProcessing.value = false
        }
    }
    
    fun saveWordsToBundle(bundleId: Long, words: List<ParsedWord>, repository: com.example.wordmemory.data.repository.WordRepository) {
        viewModelScope.launch {
            repository.saveWordsToBundle(bundleId, words)
            clearWords()
        }
    }

    fun clearWords() {
        _parsedWords.value = emptyList()
    }
}
