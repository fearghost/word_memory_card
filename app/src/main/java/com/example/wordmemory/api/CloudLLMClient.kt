package com.example.wordmemory.api

import com.example.wordmemory.domain.model.ParsedWord

/**
 * Placeholder for Cloud LLM API Client (e.g., Gemini or OpenAI).
 * This will be implemented in Phase 3.
 */
interface CloudLLMClient {
    suspend fun match(
        englishWords: List<String>,
        koreanMeanings: List<String>
    ): List<ParsedWord>
}

// A dummy implementation for compilation purposes until Phase 3 is active.
class DummyCloudLLMClient : CloudLLMClient {
    override suspend fun match(
        englishWords: List<String>,
        koreanMeanings: List<String>
    ): List<ParsedWord> {
        return emptyList()
    }
}
