package com.example.wordmemory.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.tasks.await

// DEBUG: This is a test comment to verify CI code
class MLKitOCRManager(private val context: Context) : OCRManager {
    private val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun processImage(imageUri: Uri): OCRResult {
        val originalBitmap = context.contentResolver.openInputStream(imageUri)?.use {
            android.graphics.BitmapFactory.decodeStream(it)
        } ?: return OCRResult("", 0.0f)

        val rotations = listOf(0, 90, 180, 270)
        
        for (rotation in rotations) {
            val rotatedBitmap = if (rotation == 0) {
                originalBitmap
            } else {
                val matrix = android.graphics.Matrix().apply { postRotate(rotation.toFloat()) }
                android.graphics.Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
            }

            val image = InputImage.fromBitmap(rotatedBitmap, 0)
            val visionText = try {
                recognizer.process(image).await()
            } catch (e: Exception) {
                null
            }

            if (visionText != null && visionText.text.isNotBlank()) {
                return OCRResult(
                    rawText = visionText.text,
                    confidence = 1.0f
                )
            }
        }

        return OCRResult("", 0.0f)
    }
}
