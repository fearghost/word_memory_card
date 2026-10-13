package com.example.wordmemory.ocr

import android.net.Uri
import com.example.wordmemory.domain.model.ParsedWord

class MockOCRManager : OCRManager {
    override suspend fun processImage(imageUri: Uri): OCRResult {
        // Mocking OCR output for testing the Parser
        val mockText = """
            apple: 사과
            banana - 바나나
            orange | 오렌지
            grape = 포도
            cherry: 체리
        """
        return OCRResult(rawText = mockText, confidence = 0.95f)
    }
}
