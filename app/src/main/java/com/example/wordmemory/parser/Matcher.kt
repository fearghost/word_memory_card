package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord

/**
 * Common interface for matching strategies.
 */
interface Matcher {
    /**
     * Takes a list of English words and a list of Korean meanings and attempts to match them.
     * Returns a list of matched [ParsedWord] pairs.
     */
    suspend fun match(
        englishWords: List<String>,
        koreanMeanings: List<String>
    ): List<ParsedWord>
}
