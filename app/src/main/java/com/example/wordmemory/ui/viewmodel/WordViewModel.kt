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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun processImages(uris: List<Uri>) {
        viewModelScope.launch {
            _isProcessing.value = true
            _errorMessage.value = null
            val allWords = mutableListOf<ParsedWord>()
            
            for (uri in uris) {
                try {
                    val result = ocrManager.processImage(uri)
                    if (result.rawText.isBlank()) {
                        _errorMessage.value = "OCR failed to detect any text in one of the images. Please ensure the photo is clear and well-lit."
                        break
                    }
                    val words = textParser.parse(result.rawText)
                    // Associate each extracted word with the current image URI
                    allWords.addAll(words.map { it.copy(imagePath = uri.toString()) })
                } catch (e: Exception) {
                    _errorMessage.value = "Error processing image: ${e.localizedMessage}"
                    break
                }
            }
            
            _parsedWords.value = allWords
            _isProcessing.value = false
        }
    }
    
    fun saveWordsToBundle(bundleId: Long, words: List<ParsedWord>, repository: com.example.wordmemory.data.repository.WordRepository) {
        viewModelScope.launch {
            try {
                repository.saveWordsToBundle(bundleId, words)
                clearWords()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save bundle: ${e.localizedMessage}"
            }
        }
    }

    fun clearWords() {
        _parsedWords.value = emptyList()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
