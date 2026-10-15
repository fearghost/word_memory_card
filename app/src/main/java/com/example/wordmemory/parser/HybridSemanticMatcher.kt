package com.example.wordmemory.parser

import com.example.wordmemory.domain.model.ParsedWord

/**
 * Phase 3: Hybrid Semantic Matcher.
 * Orchestrates both Local and Cloud matchers to provide a robust, 
 * high-performance, and intelligent matching pipeline.
 */
class HybridSemanticMatcher(
    private val localMatcher: Matcher,
    private val cloudMatcher: Matcher
) : Matcher {

    override suspend fun match(
        englishWords: List<String>,
        koreanMeanings: List<String>
    ): List<ParsedWord> {
        // 1. Run the fast Local Matcher first
        val localMatches = localMatcher.match(englishWords, koreanMeanings)
        
        // Track which words and meanings have already been matched
        val matchedEnglish = localMatches.map { it.word }.toSet()
        val matchedKorean = localMatches.map { it.meaning }.toSet()

        // 2. Identify the remaining (unmatched) words and meanings
        val remainingEnglish = englishWords.filter { it !in matchedEnglish }
        val remainingKorean = koreanMeanings.filter { it !in matchedKorean }

        // 3. If there are remaining items, run the intelligent Cloud Matcher
        val cloudMatches = if (remainingEnglish.isNotEmpty() && remainingKorean.isNotEmpty()) {
            cloudMatcher.match(remainingEnglish, remainingKorean)
        } else {
            emptyList()
        }

        // 4. Combine local and cloud results
        val allMatches = localMatches + cloudMatches

        // 5. Final step: Sort the resulting pairs alphabetically by the English word
        return allMatches.sortedBy { it.word }
    }
}
