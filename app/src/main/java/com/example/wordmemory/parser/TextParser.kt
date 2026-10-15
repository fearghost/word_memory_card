package com.example.wordmemory.parser

import android.content.Context
import com.example.wordmemory.api.GeminiCloudLLMClient
import com.example.wordmemory.data.local.room.dictionary.DictionaryDatabase
import com.example.wordmemory.data.local.room.dictionary.DictionaryRepository
import com.example.wordmemory.domain.model.ParsedWord

/**
 * TextParser (Phase 4: Final Integration)
 * 
 * Now acts as the primary entry point for the Hybrid Semantic Matching pipeline.
 * It orchestrates the Cleaner, Local Matcher, and Cloud Matcher.
 */
class TextParser(
    private val context: Context,
    private val apiKey: String
) {

    private val cleaner = TextCleaner()
    private val dictionaryRepository = DictionaryRepository(
        DictionaryDatabase.getDatabase(context).dictionaryDao()
    )
    private val localMatcher = LocalDictionaryMatcher(dictionaryRepository)
    private val cloudMatcher = CloudLLMMatcher(GeminiCloudLLMClient(apiKey))
    private val hybridMatcher = HybridSemanticMatcher(localMatcher, cloudMatcher)

    /**
     * Orchestrates the full semantic matching pipeline.
     */
    suspend fun parse(rawText: String): List<ParsedWord> {
        // 1. Clean the noise
        val cleanedText = cleaner.clean(rawText)

        // 2. Tokenize into language-based pools (English and Korean)
        val (englishTokens, koreanTokens) = cleaner.tokenize(cleanedText)

        // 3. Run the Hybrid Semantic Matching pipeline
        return hybridMatcher.match(englishTokens, koreanTokens)
    }
}
