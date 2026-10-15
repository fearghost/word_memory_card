package com.example.wordmemory.parser

import com.example.wordmemory.api.CloudLLMClient
import com.example.wordmemory.domain.model.ParsedWord

/**
 * Phase 3: Cloud LLM Matcher.
 * Uses a Cloud LLM (e.g., Gemini) to match English and Korean tokens.
 */
class CloudLLMMatcher(
    private val client: CloudLLMClient
) : Matcher {

    override suspend fun match(
        englishWords: List<String>,
        koreanMeanings: List<String>
    ): List<ParsedWord> {
        // If there's nothing to match, return empty list.
        if (englishWords.isEmpty() || koreanMeanings.isEmpty()) {
            return emptyList()
        }

        // Delegate the actual heavy lifting to the Cloud LLM client.
        return client.match(englishWords, koreanMeanings)
    }
}
