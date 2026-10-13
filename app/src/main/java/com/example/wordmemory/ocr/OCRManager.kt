package com.example.wordmemory.ocr

import android.net.Uri

interface OCRManager {
    suspend fun processImage(imageUri: Uri): OCRResult
}
