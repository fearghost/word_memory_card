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

        // In this Phase 2 implementation, we try to find a match for each English word 
        // in the Korean meanings list using the local dictionary.
        
        for (englishWord in englishWords) {
            // 1. Check if this English word has a meaning in our local dictionary
            val dictionaryMeaning = dictionaryRepository.getMeaning(englishWord)
            
            if (dictionaryMeaning != null && dictionaryMeaning.length >= 2) {
                // 2. Check if the dictionary meaning exists in the OCR's Korean meanings list.
                // Use exact match to prevent loose "contains" matching which causes false positives.
                val matchInOCR = koreanMeanings.find { it == dictionaryMeaning }
                
                if (matchInOCR != null) {
                    matchedPairs.add(ParsedWord(englishWord, matchInOCR))
                }
            }
        }

        return matchedPairs
    }
}
