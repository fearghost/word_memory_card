package com.example.wordmemory.parser

/**
 * Cleans OCR text and tokenizes it into English and Korean pools.
 */
class TextCleaner {

    // Regex for Hangul (Korean)
    private val hangulRegex = Regex("[\\uAC00-\\uD7A3\\u3130-\\u318F]")

    // Patterns for common noise (case-insensitive)
    private val noisePatterns = listOf(
        Regex("(?i)vocabulary"),
        Regex("(?i)check"),
        Regex("(?i)lesson\\s*\\d+"),
        Regex("(?i)page\\s*\\d+"),
        Regex("(?i)unit\\s*\\d+"),
        Regex("(?i)number\\s*\\d+"),
    )

    /**
     * Removes identified noise patterns from the raw text.
     */
    fun clean(rawText: String): String {
        var cleaned = rawText
        for (pattern in noisePatterns) {
            cleaned = cleaned.replace(pattern, "")
        }
        return cleaned.trim()
    }

    /**
     * Splits the cleaned text into two pools: English words and Korean meanings.
     * Uses a "Bag-of-Words" approach to be robust against OCR noise and label corruption.
     * 
     * @param cleanedText The text after noise removal.
     * @return A Pair containing (List of English tokens, List of Korean tokens).
     */
    fun tokenize(cleanedText: String): Pair<List<String>, List<String>> {
        val englishTokens = mutableListOf<String>()
        val koreanTokens = mutableListOf<String>()

        // Split by whitespace to get initial candidates
        val rawTokens = cleanedText.split(Regex("\\s+"))

        for (token in rawTokens) {
            // Remove all non-alphanumeric characters (punctuation, etc.)
            // \p{L} matches any Unicode letter, \p{N} matches any Unicode number
            val cleanedToken = token.replace(Regex("[^\\p{L}\\p{N}]"), "")
            
            if (cleanedToken.isEmpty()) continue

            val englishPart = cleanedToken.replace(hangulRegex, "")
            val hangulPart = cleanedToken.filter { hangulRegex.matches(it.toString()) }

            if (englishPart.length >= 2) {
                englishTokens.add(englishPart)
            }
            if (hangulPart.isNotEmpty()) {
                koreanTokens.add(hangulPart)
            }
        }

        return Pair(englishTokens, koreanTokens)
    }
}
