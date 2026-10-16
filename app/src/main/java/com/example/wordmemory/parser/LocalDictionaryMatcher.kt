package com.example.wordmemory.parser

import com.example.wordmemory.data.local.room.dictionary.DictionaryRepository
import com.example.wordmemory.domain.model.ParsedWord

/**
 * Phase 2: Local Dictionary Matcher.
 * Uses a local database to find matches for English words and Korean meanings.
 */
class LocalDictionaryMatcher(
    private val dictionaryRepository: DictionaryRepository
) : Matcher {

    override suspend fun match(
        englishWords: List<String>,
        koreanMeanings: List<String>
    ): List<ParsedWord> {
        val matchedPairs = mutableListOf<ParsedWord>()

        for (englishWord in englishWords) {
            // 1. Check if this English word has a meaning in our local dictionary
            val dictionaryMeaning = dictionaryRepository.getMeaning(englishWord)
            
            if (dictionaryMeaning != null && dictionaryMeaning.length >= 2) {
                // 2. Check if the dictionary meaning exists in the OCR's Korean meanings list.
                // We use a slightly more flexible "contains" match to handle OCR noise,
                // but we require the dictionary meaning to be at least 2 characters to avoid false positives.
                val matchInOCR = koreanMeanings.find { it.contains(dictionaryMeaning, ignoreCase = true) }
                
                if (matchInOCR != null) {
                    matchedPairs.add(ParsedWord(englishWord, matchInOCR))
                }
            }
        }

        return matchedPairs
    }
}
