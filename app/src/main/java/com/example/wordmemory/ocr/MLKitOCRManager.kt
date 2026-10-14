package com.example.wordmemory.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.tasks.await

// DEBUG: This is a test comment to verify CI code
class MLKitOCRManager(private val context: Context) : OCRManager {
    private val recognizer = TextRecognition.getClient()

    override suspend fun processImage(imageUri: Uri): OCRResult {
        val image = InputImage.fromUri(imageUri, context)
        val visionText = recognizer.processImage(image).await()
        
        return OCRResult(
            rawText = visionText.text,
            confidence = 1.0f
        )
    }
}
